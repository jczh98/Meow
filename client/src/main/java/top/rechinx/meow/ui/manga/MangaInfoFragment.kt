package top.rechinx.meow.ui.manga

import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaredrummler.cyanea.Cyanea
import kotlinx.android.synthetic.main.fragment_manga_info.*
import timber.log.Timber
import toothpick.config.Module
import top.rechinx.meow.R
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.glide.GlideApp
import top.rechinx.meow.rikka.misc.Resource
import top.rechinx.meow.ui.base.BaseFragment
import top.rechinx.meow.ui.base.getSharedViewModel
import top.rechinx.meow.ui.base.viewModel

class MangaInfoFragment : BaseFragment() {

    private lateinit var viewModel: MangaInfoViewModel

    private lateinit var adapter: MangaInfoAdapter

    override fun getModule(): Module? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_manga_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getSharedViewModel()

        toolbar.setNavigationOnClickListener {
            activity?.finish()
        }

        // Constraint attribution contentScrim of CollapsingToolbarLayout
        collapsing_toolbar_layout.contentScrim =
                ColorDrawable(Cyanea.instance.primary)

        initSubscriptions()
    }

    private fun initSubscriptions() {
        viewModel.mangaLiveData.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    setLoading()
                }
                is Resource.Success -> {
                    viewModel.fetchChapters(resource.value)
                    ///setManga(resource.value)
                    adapter = MangaInfoAdapter(resource.value)
                }
            }
        })
        viewModel.chaptersLiveData.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    setManga(viewModel.manga, resource.value)
                }
            }
        })
    }

    private fun setLoading() {

    }

    private fun setManga(manga: Manga, chapters: List<Chapter>) {
        toolbar.title = manga.title
        GlideApp.with(view!!)
                .load(manga)
                .into(backed_cover)

        val layoutManager = GridLayoutManager(activity, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return adapter.getSpanSize(position) ?: spanCount
                }
            }
        }
        recycler.setHasFixedSize(true)
        recycler.layoutManager = layoutManager
        recycler.adapter = adapter

        val newItems = listOf(manga) + chapters
        adapter.submitList(newItems)
    }

}

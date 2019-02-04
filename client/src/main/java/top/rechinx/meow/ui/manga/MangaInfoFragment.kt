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
import com.kennyc.view.MultiStateView
import kotlinx.android.synthetic.main.fragment_manga_info.*
import timber.log.Timber
import top.rechinx.meow.R
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.glide.GlideApp
import top.rechinx.meow.global.Extras
import top.rechinx.meow.rikka.misc.Resource
import top.rechinx.meow.rikka.viewmodel.getSharedViewModel
import top.rechinx.meow.ui.base.BaseFragment
import top.rechinx.meow.ui.reader.ReaderActivity
import javax.inject.Inject
import javax.inject.Provider

class MangaInfoFragment : BaseFragment(), MangaInfoAdapter.Listener {

    private val mangaId: Long by lazy {
        arguments?.getLong(Extras.EXTRA_MANGA_ID, 0) ?: 0
    }

    @Inject lateinit var vmFactory: MangaInfoViewModel.Factory

    private val viewModel by lazy {
        getSharedViewModel<MangaInfoViewModel> {
            vmFactory.create(mangaId)
        }
    }

    private lateinit var adapter: MangaInfoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_manga_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.inflateMenu(R.menu.manga_info_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_subscribe -> handleSubscribe()
            }
            true
        }
        toolbar.setNavigationOnClickListener {
            activity?.finish()
        }

        // Constraint menu icon of Toolbar
        Cyanea.instance.tint(toolbar.menu, requireActivity())

        // Constraint attribution contentScrim of CollapsingToolbarLayout
        collapsing_toolbar_layout.contentScrim =
                ColorDrawable(Cyanea.instance.primary)

        initSubscriptions()
    }

    private fun handleSubscribe() {
        viewModel.subscribeManga()
    }

    private fun initSubscriptions() {
        viewModel.stateLiveData.observe(this, Observer { (state, prevState) ->
            if (state.isLoading != prevState?.isLoading) {
                setLoading(state.isLoading)
            }
            if (state.error != prevState?.error) {
                multi_state_view.viewState = MultiStateView.VIEW_STATE_ERROR
            }
            if (state.manga != null && state.manga !== prevState?.manga) {
                renderManga(state.manga)
            }
            if (state.manga != null && (state.chapters !== prevState?.chapters || state.isLoading != prevState.isLoading
                    || state.hasNextPage != prevState.hasNextPage)) {
                adapter = MangaInfoAdapter(state.manga, this@MangaInfoFragment)
                setManga(state.manga, state.chapters)
                multi_state_view.viewState = MultiStateView.VIEW_STATE_CONTENT
            }
        })
    }

    private fun renderManga(manga: Manga) {
        val subscribeItem = toolbar.menu.findItem(R.id.action_subscribe)
        if (manga.subscribed) {
            subscribeItem.setIcon(R.drawable.ic_favorite_white_24dp)
        } else {
            subscribeItem.setIcon(R.drawable.ic_favorite_border_white_24dp)
        }

        // Constraint menu icon of Toolbar
        Cyanea.instance.tint(toolbar.menu, requireActivity())
    }

    private fun setLoading(isLoading: Boolean) {

    }

    private fun setManga(manga: Manga, chapters: List<Chapter>) {
        renderManga(manga)
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

    override fun onChapterClick(chapter: Chapter) {
        startActivity(ReaderActivity.createIntent(requireContext(),
                viewModel.stateLiveData.value?.first?.manga!!,
                chapter))
    }

    companion object {

        fun newInstance(mangaId: Long): MangaInfoFragment{
            val fragment = MangaInfoFragment()
            val bundle = Bundle()
            bundle.putLong(Extras.EXTRA_MANGA_ID, mangaId)
            fragment.arguments = bundle
            return fragment
        }
    }
}

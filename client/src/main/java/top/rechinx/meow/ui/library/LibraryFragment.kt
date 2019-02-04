package top.rechinx.meow.ui.library

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_library.*
import timber.log.Timber
import top.rechinx.meow.R
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.rikka.livedata.scanWithPrevious
import top.rechinx.meow.rikka.viewmodel.getViewModel
import top.rechinx.meow.ui.base.BaseFragment
import top.rechinx.meow.ui.manga.MangaInfoActivity
import javax.inject.Inject
import javax.inject.Provider


class LibraryFragment : BaseFragment(), LibraryAdapter.Listener {

    @Inject lateinit var vmProvider: Provider<LibraryViewModel>
    private val viewModel: LibraryViewModel by lazy {
        getViewModel(vmProvider)
    }

    private lateinit var adapter: LibraryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = LibraryAdapter(this)

        recycler.setHasFixedSize(true)
        recycler.layoutManager = GridLayoutManager(activity, 2)
        recycler.adapter = adapter
        initSubscriptions()
    }

    private fun initSubscriptions() {
        viewModel.stateLiveData
                .scanWithPrevious()
                .observe(this, Observer { (state, prevState) ->
                    if (state.mangaList !== prevState?.mangaList) {
                        renderList(state.mangaList)
                    }
                })
    }

    private fun renderList(mangaList: List<Pair<Manga, Source>>) {
        adapter.submitList(mangaList)
    }

    override fun onMangaClick(manga: Manga) {
        startActivity(MangaInfoActivity.createIntent(requireContext(), manga))
    }
}

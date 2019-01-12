package top.rechinx.meow.ui.catalogbrowse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_catalog_browse.*
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import toothpick.config.Module
import top.rechinx.meow.R
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.rikka.ext.gone
import top.rechinx.meow.rikka.ext.visibleIf
import top.rechinx.meow.rikka.misc.Resource
import top.rechinx.meow.ui.base.*
import top.rechinx.meow.ui.catalogs.CatalogsFragment
import top.rechinx.meow.ui.manga.MangaInfoActivity
import javax.inject.Inject

class CatalogBrowseFragment : BaseFragment(),
        EndlessRecyclerViewScrollListener.Callback,
        CatalogBrowseAdapter.Listener {

    val sourceId: Long by lazy {
        arguments?.getLong(CatalogsFragment.CATALOG_SOURCE_ID) ?: -1
    }

    @Inject lateinit var factorys: CatalogBrowseViewModelFactory

    private val viewModel by lazy {
        InjectedViewModelProviders.of(this)
                .get(CatalogBrowseViewModel::class.java.name) {
                    factorys.createa(CatalogBrowseParams(sourceId))
                }
    }

    private lateinit var adapter: CatalogBrowseAdapter

    override fun getModule(): Module? {
        return CatalogBrowseModule(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_catalog_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup toolbar
        catalogbrowse_toolbar.title = viewModel.source.name
        catalogbrowse_toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        adapter = CatalogBrowseAdapter(this)

        recycler.setHasFixedSize(true)
        recycler.adapter = adapter
        val layoutManager = GridLayoutManager(activity, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return adapter.getSpanSize(position) ?: spanCount
                }
            }
        }
        recycler.layoutManager = layoutManager
        // Create a endless scroll listener
        val endlessListener = EndlessRecyclerViewScrollListener(layoutManager, this)
        recycler.addOnScrollListener(endlessListener)

        initSubscriptions()
    }

    private fun initSubscriptions() {
        viewModel.stateLiveData.observe(this, Observer { state ->
                adapter.submitList(state.mangas, state.isLoading, !state.hasMorePages)
                progress.gone()
        })
    }

    override fun onMangaClick(manga: Manga) {
        startActivity(MangaInfoActivity.createIntent(activity!!, manga))
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        viewModel.loadMore()
    }

}

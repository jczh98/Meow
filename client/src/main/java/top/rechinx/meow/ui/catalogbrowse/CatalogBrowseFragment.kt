package top.rechinx.meow.ui.catalogbrowse

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_catalog_browse.*
import me.drakeet.multitype.Items
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import top.rechinx.meow.R
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.rikka.ext.gone
import top.rechinx.meow.rikka.ext.visible
import top.rechinx.meow.rikka.misc.Resource
import top.rechinx.meow.ui.base.*
import top.rechinx.meow.ui.catalogs.CatalogBinder
import top.rechinx.meow.ui.catalogs.CatalogsFragment

class CatalogBrowseFragment : Fragment(),
        EndlessRecyclerViewScrollListener.Callback,
        CatalogBrowseAdapter.Listener {

    private val sourceId: Long by lazy {
        arguments?.getLong(CatalogsFragment.CATALOG_SOURCE_ID) ?: -1
    }

    private val viewModel by viewModel<CatalogBrowseViewModel> {
        parametersOf(sourceId)
    }

    private val sourceManager by inject<SourceManager>()

    private val source by lazy {
        sourceManager.getOrStub(sourceId)
    }

    private lateinit var adapter: CatalogBrowseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_catalog_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup toolbar
        catalogbrowse_toolbar.title = source.name
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

        viewModel.loadMore()
    }

    private fun initSubscriptions() {
        viewModel.mangaListLiveData.observe(this, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    viewModel.mangaList.addAll(resource.value.list)
                    adapter.submitList(viewModel.mangaList, true, !resource.value.hasNextPage)
                    progress.gone()
                }
            }
        })
    }

    override fun onMangaClick(manga: Manga) {
        findNavController().navigateUp()
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        viewModel.loadMore()
    }

}

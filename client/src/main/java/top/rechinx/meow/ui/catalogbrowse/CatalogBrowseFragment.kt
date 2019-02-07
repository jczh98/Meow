package top.rechinx.meow.ui.catalogbrowse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.custom_catalogbrowse_filters_sheet.view.*
import kotlinx.android.synthetic.main.fragment_catalog_browse.*
import me.drakeet.multitype.Items
import top.rechinx.meow.R
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.rikka.ext.visibleIf
import top.rechinx.meow.rikka.viewmodel.getViewModel
import top.rechinx.meow.ui.base.*
import top.rechinx.meow.ui.catalogbrowse.filters.FiltersAdapter
import top.rechinx.meow.ui.catalogbrowse.filters.FiltersBottomSheetDialog
import top.rechinx.meow.ui.catalogs.CatalogsFragment
import top.rechinx.meow.ui.manga.MangaInfoActivity
import javax.inject.Inject

class CatalogBrowseFragment : BaseFragment(),
        EndlessRecyclerViewScrollListener.Callback,
        CatalogBrowseAdapter.Listener {

    val sourceId: Long by lazy {
        arguments?.getLong(CatalogsFragment.CATALOG_SOURCE_ID) ?: -1
    }

    @Inject lateinit var vmFactory: CatalogBrowseViewModel.Factory

    private val viewModel by lazy {
        getViewModel<CatalogBrowseViewModel> {
            vmFactory.create(CatalogBrowseParams(sourceId))
        }
    }

    private lateinit var adapter: CatalogBrowseAdapter

    private lateinit var filtersAdapter: FiltersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_catalog_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup toolbar
        setToolbarTitle()
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        toolbar.inflateMenu(R.menu.catalogue_browse_menu)

        // Inflate menu to search view
        val menuItem = toolbar.menu.findItem(R.id.action_search)
        search_view.setMenuItem(menuItem)
        search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrBlank()) return false
                setToolbarTitle(query)
                viewModel.search(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

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

        // Fab
        fab.setOnClickListener {
            showFilters()
        }

        // Initialize filters adapter
        filtersAdapter = FiltersAdapter()

        initSubscriptions()
    }

    private fun showFilters() {
        val view = view ?: return

        val dialog = FiltersBottomSheetDialog(view.context)

        val filtersView = layoutInflater.inflate(R.layout.custom_catalogbrowse_filters_sheet, null)

        filtersView.filter_button_close.setOnClickListener { dialog.cancel() }
        filtersView.filter_button_reset.setOnClickListener { resetFilters() }
        filtersView.filter_button_search.setOnClickListener { applyFilters() }

        filtersView.filters_recycler.layoutManager = LinearLayoutManager(view.context)
        filtersView.filters_recycler.adapter = filtersAdapter

        dialog.setContentView(filtersView)
        dialog.show()
    }

    private fun applyFilters() {
        viewModel.setFilters(filtersAdapter.items)
        // Clear search data
        setToolbarTitle()
    }

    private fun resetFilters() {
        filtersAdapter.items.forEach { it.reset() }
        filtersAdapter.notifyDataSetChanged()
    }

    private fun initSubscriptions() {
        viewModel.stateLiveData.observe(this, Observer { (state, prevState) ->
            if (state.isLoading != prevState?.isLoading) {
                progress.visibleIf { state.isLoading && state.mangas.isEmpty() }
            }
            filtersAdapter.updateItems(state.filters)
            adapter.submitList(state.mangas, state.isLoading, !state.hasMorePages)
        })
    }

    private fun setToolbarTitle(query: String? = null) {
        if (query.isNullOrBlank()) {
            toolbar.title = viewModel.source.name
        } else {
            toolbar.title = getString(R.string.search_result_title).format(query)
        }
    }

    override fun onMangaClick(manga: Manga) {
        startActivity(MangaInfoActivity.createIntent(activity!!, manga))
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        viewModel.loadMore()
    }

}

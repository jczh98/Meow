package top.rechinx.meow.ui.filter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.miguelcatalan.materialsearchview.MaterialSearchView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.custom_filter_sidesheet.view.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import timber.log.Timber
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.exception.NoMoreResultException
import top.rechinx.meow.global.Extras
import top.rechinx.meow.ui.base.BaseMvpActivityWithoutReflection
import top.rechinx.meow.ui.details.DetailActivity
import top.rechinx.meow.ui.filter.items.CatalogueItem
import top.rechinx.meow.ui.filter.items.ProgressItem
import top.rechinx.rikka.ext.gone
import top.rechinx.rikka.ext.visible
import top.rechinx.rikka.mvp.MvpAppCompatActivityWithoutReflection

class FilterActivity: BaseMvpActivityWithoutReflection<FilterPresenter>(),
        FlexibleAdapter.EndlessScrollListener,
        FlexibleAdapter.OnItemClickListener {


    private val sideSheetAdapter: FlexibleAdapter<IFlexible<*>> = FlexibleAdapter<IFlexible<*>>(null)
        .setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true)


    private val sourceId by lazy { intent.getLongExtra(Extras.EXTRA_SOURCE, 0) }

    private var adapter: FlexibleAdapter<IFlexible<*>>? = null

    private lateinit var sideSheetRecycler: RecyclerView

    private var progressItem: ProgressItem? = null

    private var snack: Snackbar? = null

    private var oldPosition = RecyclerView.NO_POSITION

    override fun createPresenter(): FilterPresenter {
        return FilterPresenter(sourceId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        setSupportActionBar(customToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        customToolbar.setNavigationOnClickListener { finish() }
        // for drawer
        val drawerToggle = ActionBarDrawerToggle(this, filterDrawer, null, 0, 0)
        filterDrawer.addDrawerListener(drawerToggle)
        initSideSheet()
        initRecyclerView()
        // setting search hint
        filterSearchView.setHint(getString(R.string.specific_search_hint).format(presenter.source.name))
        filterSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if(query.isEmpty()) {
                    filterSearchView.closeSearch()
                } else {
                    setQueryTitle(query)
                    adapter?.clear()
                    showProgressBar()
                    presenter.restartPager(query = query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        // set title
        setDefaultTitle()
    }

    private fun initRecyclerView() {
        contentProgress.visible()
        adapter = FlexibleAdapter(null, this)
        filterContainer.adapter = adapter
        filterContainer.setHasFixedSize(true)
    }

    private fun initSideSheet() {
        filterSideSheet.setContentView(R.layout.custom_filter_sidesheet)
        filterSideSheet.contentView.title.text = getString(R.string.title_search_filter)
        sideSheetRecycler = RecyclerView(filterSideSheet.context)
        sideSheetRecycler.layoutManager = LinearLayoutManager(this)
        sideSheetRecycler.adapter = sideSheetAdapter
        sideSheetRecycler.setHasFixedSize(true)
        sideSheetAdapter.updateDataSet(presenter.filterItems)
        filterSideSheet.contentView.filterDrawerContainer.addView(sideSheetRecycler)
        filterSideSheet.contentView.resetBtn.setOnClickListener {
            presenter.appliedFilters = FilterList()
            val newFilters = presenter.source.getFilterList()
            presenter.sourceFilters = newFilters
            sideSheetAdapter.updateDataSet(presenter.filterItems)
        }
        filterSideSheet.contentView.filterBtn.setOnClickListener {
            setDefaultTitle()
            adapter?.clear()
            showProgressBar()
            filterDrawer.closeDrawer(GravityCompat.END)
            presenter.restartPager(query = "", filters = presenter.sourceFilters)
        }
    }

    private fun setDefaultTitle() {
        supportActionBar?.title = presenter.source.name
    }

    private fun setQueryTitle(query: String) {
        supportActionBar?.title = String.format(getString(R.string.search_result_title), query)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.filter_menu, menu)
        filterSearchView.setMenuItem(menu.findItem(R.id.action_search))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_filter -> filterSideSheet?.let { filterDrawer?.openDrawer(GravityCompat.END) }
        }
        return true
    }

    fun onAddPage(page: Int, mangas: List<CatalogueItem>) {
        val adapter = adapter ?: return
        hideProgressBar()
        if(page == 1) {
            adapter.clear()
            resetProgressItem()
        }
        adapter.onLoadMoreComplete(mangas)
    }

    private fun resetProgressItem() {
        progressItem = ProgressItem()
        adapter?.endlessTargetCount = 0
        adapter?.setEndlessScrollListener(this, progressItem!!)
    }

    fun onAddPageError(throwable: Throwable) {
        val adapter = adapter ?: return
        adapter.onLoadMoreComplete(null)
        hideProgressBar()
        snack?.dismiss()
        if(throwable is NoMoreResultException) {
            snack = Snackbar.make(catalogueView, getString(R.string.snackbar_result_empty), Snackbar.LENGTH_SHORT)
                    .setAction(R.string.action_retry) {
                        if (adapter.mainItemCount > 0) {
                            val item = progressItem ?: return@setAction
                            adapter.addScrollableFooterWithDelay(item, 0, true)
                        } else {
                            showProgressBar()
                        }
                        presenter.requestNext()
                    }
        } else {
            snack = Snackbar.make(catalogueView, throwable.message.toString(), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_retry) {
                        if (adapter.mainItemCount > 0) {
                            val item = progressItem ?: return@setAction
                            adapter.addScrollableFooterWithDelay(item, 0, true)
                        } else {
                            showProgressBar()
                        }
                        presenter.requestNext()
                    }
        }
        snack?.show()
    }

    override fun noMoreLoad(newItemsSize: Int) {

    }

    override fun onLoadMore(lastPosition: Int, currentPage: Int) {
        if (presenter.hasNextPage()) {
            presenter.requestNext()
        } else {
            adapter?.onLoadMoreComplete(null)
            adapter?.endlessTargetCount = 1
        }
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        val item = adapter?.getItem(position) as? CatalogueItem ?: return false
        item.manga.url?.let {
            val intent = DetailActivity.createIntent(this, sourceId, it)
            startActivity(intent)
        }
        return false
    }

    private fun showProgressBar() {
        contentProgress?.visible()
        snack?.dismiss()
        snack = null
    }

    private fun hideProgressBar() {
        contentProgress.gone()
    }

    companion object {

        fun createIntent(context: Context, sourceId: Long): Intent {
            val intent = Intent(context, FilterActivity::class.java)
            intent.putExtra(Extras.EXTRA_SOURCE, sourceId)
            return intent
        }
    }
}
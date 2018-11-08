package top.rechinx.meow.ui.filter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.miguelcatalan.materialsearchview.MaterialSearchView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.custom_filter_sidesheet.view.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.Filter
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.global.Extras
import top.rechinx.meow.support.log.L
import top.rechinx.rikka.mvp.MvpAppCompatActivityWithoutReflection

class FilterActivity: MvpAppCompatActivityWithoutReflection<FilterPresenter>() {


    private val sideSheetAdapter: FlexibleAdapter<IFlexible<*>> = FlexibleAdapter<IFlexible<*>>(null)
        .setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true)


    private val sourceId by lazy { intent.getLongExtra(Extras.EXTRA_SOURCE, 0) }

    private lateinit var adapter : FilterAdapter

    private lateinit var sideSheetRecycler: RecyclerView

    override fun createPresenter(): FilterPresenter {
        return FilterPresenter(sourceId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.syncSourceFilters(sourceId)
        setContentView(R.layout.activity_filter)
        setSupportActionBar(custom_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        custom_toolbar.setNavigationOnClickListener { finish() }
        // for drawer
        val drawerToggle = ActionBarDrawerToggle(this, filterDrawer, null, 0, 0)
        filterDrawer.addDrawerListener(drawerToggle)
        initSideSheet()
        initRecyclerView()
        filterSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if(query.isBlank()) {
                    filterSearchView.closeSearch()
                } else {
                    adapter = FilterAdapter(this@FilterActivity)
                    filterContainer.adapter = adapter
                    presenter.restartPaging(query = query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    private fun initRecyclerView() {
        adapter = FilterAdapter(this)
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
            adapter = FilterAdapter(this)
            filterContainer.adapter = adapter
            filterDrawer.closeDrawer(GravityCompat.END)
            presenter.restartPaging(query = "", filters = presenter.sourceFilters)
        }
    }

    fun onMangaLoaded(list: PagedList<Manga>) {
        adapter.submitList(list)
    }

    fun onMangaLoadError(throwable: Throwable) {
        Snackbar.make(layoutView, getString(R.string.snackbar_result_empty), Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.filter_menu, menu)
        filterSearchView.setMenuItem(menu?.findItem(R.id.action_search))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_filter -> filterSideSheet?.let { filterDrawer?.openDrawer(GravityCompat.END) }
        }
        return true
    }

    companion object {

        fun createIntent(context: Context, sourceId: Long): Intent {
            val intent = Intent(context, FilterActivity::class.java)
            intent.putExtra(Extras.EXTRA_SOURCE, sourceId)
            return intent
        }
    }
}
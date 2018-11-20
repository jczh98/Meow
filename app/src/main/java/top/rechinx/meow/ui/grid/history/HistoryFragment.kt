package top.rechinx.meow.ui.grid.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.fragment_grid.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.details.DetailActivity
import top.rechinx.meow.ui.grid.GridAdapter
import top.rechinx.meow.ui.grid.items.GridItem
import top.rechinx.rikka.mvp.MvpFragment
import top.rechinx.rikka.mvp.factory.RequiresPresenter

@RequiresPresenter(HistoryPresenter::class)
class HistoryFragment: MvpFragment<HistoryPresenter>(),
        FlexibleAdapter.OnItemClickListener {

    private val adapter by lazy { GridAdapter(activity!!) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    fun initViews() {
        adapter.addListener(this)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        presenter.load()
    }


    fun onMangasLoaded(list: List<Manga>) {
        adapter.updateDataSet(list.map { GridItem(it) })
    }

    fun onMangasLoadError(throwable: Throwable) {
    }

    override fun onItemClick(view: View, position: Int) : Boolean{
        val manga = adapter.getItem(position)?.manga ?: return false
        val intent = DetailActivity.createIntent(activity!!, manga.sourceId, manga.url!!)
        startActivity(intent)
        return true
    }

}
package top.rechinx.meow.ui.grid.favorite

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.koin.android.ext.android.inject
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.viewbinding.bindView
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.base.BaseFragment
import top.rechinx.meow.ui.details.DetailActivity
import top.rechinx.meow.ui.grid.GridAdapter

class HistoryFragment: BaseFragment(), HistoryContract.View, BaseAdapter.OnItemClickListener {

    private val recyclerView by bindView<RecyclerView>(R.id.recycler_view_content)

    private val adapter = GridAdapter(activity!!, ArrayList())

    override fun initViews() {
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        adapter.setOnItemClickListener(this)
        recyclerView.adapter = adapter
    }

    override fun getLayoutId(): Int = R.layout.fragment_grid

    override fun onMangasLoaded(list: List<Manga>) {
        adapter.clear()
        adapter.addAll(list)
    }

    override fun onMangasLoadError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemClick(view: View, position: Int) {
        val manga = adapter.getItem(position)
        val intent = DetailActivity.createIntent(activity!!, manga.sourceId, manga.cid!!)
        startActivity(intent)
    }

    override val presenter: HistoryContract.Presenter by inject()

}
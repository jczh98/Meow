package top.rechinx.meow.ui.grid.download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_grid.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.grid.GridAdapter
import top.rechinx.meow.ui.task.TaskActivity
import top.rechinx.rikka.mvp.MvpFragment
import top.rechinx.rikka.mvp.factory.RequiresPresenter

@RequiresPresenter(DownloadPresenter::class)
class DownloadFragment: MvpFragment<DownloadPresenter>(), BaseAdapter.OnItemClickListener {

    private val adapter by lazy { GridAdapter(activity!!, ArrayList()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setOnItemClickListener(this)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        presenter.load()
    }

    override fun onItemClick(view: View, position: Int) {
        val item = adapter.getItem(position)
        startActivity(TaskActivity.createIntent(activity!!, item.id))
    }

    fun onDownloadLoaded(list: List<Manga>) {
        adapter.clear()
        adapter.addAll(list)
    }

    fun onLoadError(error: Throwable) {

    }
}
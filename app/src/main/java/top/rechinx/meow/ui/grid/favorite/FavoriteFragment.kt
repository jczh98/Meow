package top.rechinx.meow.ui.grid.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_grid.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.viewbinding.ViewBindings
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.details.DetailActivity
import top.rechinx.meow.ui.grid.GridAdapter
import top.rechinx.rikka.mvp.MvpFragment
import top.rechinx.rikka.mvp.factory.RequiresPresenter

@RequiresPresenter(FavoritePresenter::class)
class FavoriteFragment: MvpFragment<FavoritePresenter>(), BaseAdapter.OnItemClickListener {

    private val adapter by lazy { GridAdapter(activity!!, ArrayList()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    fun initViews() {
        adapter.setOnItemClickListener(this)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        presenter.load()
    }

    fun onMangasLoaded(list: List<Manga>) {
        adapter.clear()
        adapter.addAll(list)
    }

    fun onMangasLoadError(throwable: Throwable) {
    }

    override fun onItemClick(view: View, position: Int) {
        val manga = adapter.getItem(position)
        val intent = DetailActivity.createIntent(activity!!, manga.sourceId, manga.url!!)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ViewBindings.reset(this)
    }

}
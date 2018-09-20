package top.rechinx.meow.module.favorite

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import butterknife.BindView
import top.rechinx.meow.R
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BaseAdapter
import top.rechinx.meow.module.base.BaseFragment
import top.rechinx.meow.module.common.GridAdapter
import top.rechinx.meow.module.detail.DetailActivity
import kotlin.collections.ArrayList

class FavoriteFragment: BaseFragment(), FavoriteView, BaseAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view_content) lateinit var mRecyclerView: RecyclerView

    private lateinit var mAdapter: GridAdapter
    private lateinit var mPresenter: FavoritePresenter

    override fun initView() {
        mRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        mAdapter = GridAdapter(activity!!, ArrayList<Comic>())
        mAdapter.setOnItemClickListener(this)
        mRecyclerView.adapter = mAdapter
    }

    override fun initPresenter() {
        mPresenter = FavoritePresenter()
        mPresenter.attachView(this)
    }

    override fun getLayoutId(): Int = R.layout.fragment_grid

    override fun initData() {
        mPresenter.load()
    }

    override fun onComicLoadSuccess(list: List<Comic>) {
        mAdapter.clear()
        mAdapter.addAll(list)
    }

    override fun onComicLoadFailure() {
    }

    override fun onItemClick(view: View, position: Int) {
        val comic = mAdapter.getItem(position)
        val intent = DetailActivity.createIntent(activity!!, comic.source!!, comic.cid!!)
        startActivity(intent)
    }
}
package top.rechinx.meow.module.favorite

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.BindView
import top.rechinx.meow.R
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BaseFragment
import top.rechinx.meow.module.common.GridAdapter
import java.util.*
import kotlin.collections.ArrayList

class FavoriteFragment: BaseFragment(), FavoriteView {

    @BindView(R.id.grid_action_button) lateinit var mActionButton: FloatingActionButton
    @BindView(R.id.recycler_view_content) lateinit var mRecyclerView: RecyclerView

    private lateinit var mAdapter: GridAdapter
    private lateinit var mPresenter: FavoritePresenter

    override fun initView() {
        mRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        mAdapter = GridAdapter(activity!!, ArrayList<Comic>())
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
        mAdapter.addAll(list)
    }

    override fun onComicLoadFailure() {
    }
}
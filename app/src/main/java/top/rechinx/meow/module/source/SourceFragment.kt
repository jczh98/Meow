package top.rechinx.meow.module.source

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import butterknife.BindView
import top.rechinx.meow.R
import top.rechinx.meow.model.Source
import top.rechinx.meow.module.base.BaseFragment

class SourceFragment: BaseFragment(), SourceView, SourceAdapter.OnItemCheckedListener {

    @BindView(R.id.recycler_view_content) lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.custom_progress_bar) lateinit var mProgressBar: ProgressBar

    private lateinit var mPresenter: SourcePresenter
    private lateinit var mAdapter: SourceAdapter

    override fun initView() {
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.itemAnimator = null
        mRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mAdapter = SourceAdapter(activity!!, ArrayList())
        mAdapter.setOnItemCheckedlistener(this)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.addItemDecoration(mAdapter.getItemDecoration())
    }

    override fun initPresenter() {
        mPresenter = SourcePresenter()
        mPresenter.attachView(this)
    }

    override fun getLayoutId(): Int = R.layout.fragment_source

    override fun initData() {
        mPresenter.load()
    }

    override fun onSourceLoadSuccess(list: List<Source>) {
        mAdapter.addAll(list)
        hideProgressBar()
    }

    override fun onSourceLoadFailure() {
    }

    override fun onItemCheckedListener(isChecked: Boolean, position: Int) {
        var source = mAdapter.getItem(position)
        source.isEnable = isChecked
        mPresenter.update(source)
    }

    private fun hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar!!.visibility = View.GONE
        }
    }
}
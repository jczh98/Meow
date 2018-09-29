package top.rechinx.meow.module.source

import android.app.Activity
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import butterknife.BindView
import top.rechinx.meow.R
import top.rechinx.meow.model.Source
import top.rechinx.meow.module.base.BaseFragment
import top.rechinx.meow.module.login.LoginActivity

class SourceFragment: BaseFragment(), SourceView, SourceAdapter.OnItemCheckedListener {

    @BindView(R.id.recycler_view_content) lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.custom_progress_bar) lateinit var mProgressBar: ProgressBar

    private lateinit var mPresenter: SourcePresenter
    private lateinit var mAdapter: SourceAdapter

    private var lastPosition = 0

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
        mPresenter = SourcePresenter(context!!)
        mPresenter.attachView(this)
    }

    override fun getLayoutId(): Int = R.layout.fragment_source

    override fun initData() {
        mPresenter.load()
    }

    override fun onSourceLoadSuccess(list: List<Source>) {
        mAdapter.clear()
        mAdapter.addAll(list)
        hideProgressBar()
    }

    override fun onSourceLoadFailure() {
    }

    override fun onLoginFailured(position: Int) {
        mAdapter.getItem(position).isEnable = false
        mAdapter.notifyItemChanged(position)
    }

    override fun doLogin(name: String, position: Int) {
        startActivityForResult(LoginActivity.createIntent(activity!!, name), 777)
        lastPosition = position
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            var code = data?.getIntExtra("data", 1)
            if(code == 1) {
                Snackbar.make(mRecyclerView, getString(R.string.snackbar_login_success), Snackbar.LENGTH_SHORT).show()
            } else {
                onLoginFailured(lastPosition)
                Snackbar.make(mRecyclerView, getString(R.string.snackbar_login_failure), Snackbar.LENGTH_SHORT).show()
            }
        } else {
            onLoginFailured(lastPosition)
        }
    }

    override fun onItemCheckedListener(isChecked: Boolean, position: Int) {
        var source = mAdapter.getItem(position)
        source.isEnable = isChecked
        mPresenter.update(source, position)
    }

    private fun hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar!!.visibility = View.GONE
        }
    }
}
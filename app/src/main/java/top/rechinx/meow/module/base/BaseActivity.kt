package top.rechinx.meow.module.base

import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import top.rechinx.meow.R

abstract class BaseActivity: AppCompatActivity() {

    @BindView(R.id.custom_toolbar) protected lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        ButterKnife.bind(this)
        initPresenter()
        initToolbar()
        initView()
        initData()
    }

    protected abstract fun initData()

    protected abstract fun initView()

    protected open fun initToolbar() {
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initPresenter()
}
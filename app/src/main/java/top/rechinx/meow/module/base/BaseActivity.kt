package top.rechinx.meow.module.base

import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.LayoutRes
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import top.rechinx.meow.R

abstract class BaseActivity: AppCompatActivity() {

    @Nullable @JvmField @BindView(R.id.custom_toolbar) var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        ButterKnife.bind(this)
        initPresenter()
        initToolbar()
        initData()
        initView()
    }

    protected abstract fun initData()

    protected abstract fun initView()

    protected open fun initToolbar() {
        if(mToolbar != null) {
            setSupportActionBar(mToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initPresenter()


}
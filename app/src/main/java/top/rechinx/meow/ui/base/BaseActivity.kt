package top.rechinx.meow.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.custom_toolbar.*
import top.rechinx.meow.R
import top.rechinx.meow.support.viewbinding.bindOptionalView

abstract class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initToolbar()
        initViews()
        initData()
    }

    protected abstract fun initViews()

    protected open fun initToolbar() {
        if(customToolbar != null) {
            setSupportActionBar(customToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    protected open fun initData() {}
    
    protected abstract fun getLayoutId(): Int
}
package top.rechinx.meow.ui.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import top.rechinx.meow.R
import top.rechinx.meow.support.viewbinding.bindOptionalView
import top.rechinx.meow.support.viewbinding.bindView

abstract class BaseActivity: AppCompatActivity() {

    val toolbar: Toolbar? by bindOptionalView(R.id.custom_toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initToolbar()
        initViews()
        initData()
    }

    protected abstract fun initViews()

    protected open fun initToolbar() {
        if(toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    protected open fun initData() {}
    
    protected abstract fun getLayoutId(): Int
}
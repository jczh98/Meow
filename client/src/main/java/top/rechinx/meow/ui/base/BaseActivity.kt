package top.rechinx.meow.ui.base

import android.os.Bundle
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity

abstract class BaseActivity: CyaneaAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        setUpViews(savedInstanceState)
    }

    open fun setUpViews(savedInstanceState: Bundle?) {

    }

    abstract fun getLayoutRes() : Int

}
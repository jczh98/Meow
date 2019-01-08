package top.rechinx.meow.ui.base

import android.os.Bundle
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity

abstract class BaseActivity: CyaneaAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        setUpViews()
    }

    open fun setUpViews() {

    }

    abstract fun getLayoutRes() : Int

}
package top.rechinx.meow.rikka.common

import android.os.Bundle
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity

abstract class CommonActivity: CyaneaAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        setUpViews()
    }

    open fun setUpViews() {

    }

    abstract fun getLayoutRes() : Int
}
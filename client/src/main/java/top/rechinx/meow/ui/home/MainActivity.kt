package top.rechinx.meow.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.jaredrummler.cyanea.inflator.CyaneaViewProcessor
import kotlinx.android.synthetic.main.activity_main.*
import toothpick.config.Module
import top.rechinx.meow.R
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.base.getCyaneaViewProcessors

class MainActivity : BaseActivity(), CyaneaViewProcessor.Provider {

    override fun getModule(): Module? = null

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

    override fun setUpViews(savedInstanceState: Bundle?) {
        super.setUpViews(savedInstanceState)
        navigation.setupWithNavController(findNavController(R.id.nav_host_fragment))
    }

    override fun getViewProcessors(): Array<CyaneaViewProcessor<out View>> = getCyaneaViewProcessors()

    override fun getLayoutRes(): Int = R.layout.activity_main
}

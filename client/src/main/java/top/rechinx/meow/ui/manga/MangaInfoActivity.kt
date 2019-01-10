package top.rechinx.meow.ui.manga

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.jaredrummler.cyanea.inflator.CyaneaViewProcessor
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import top.rechinx.meow.R
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.global.Extras
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.base.getCyaneaViewProcessors

class MangaInfoActivity : BaseActivity(), CyaneaViewProcessor.Provider {

    private val mangaId by lazy {
        intent.getLongExtra(Extras.EXTRA_MANGA_ID, 0)
    }

    private val viewModel by viewModel<MangaInfoViewModel>()

    override fun onSupportNavigateUp()
            = findNavController(R.id.manga_nav_host_fragment).navigateUp()

    override fun getLayoutRes(): Int
            = R.layout.activity_manga_info

    override fun getViewProcessors(): Array<CyaneaViewProcessor<out View>> = getCyaneaViewProcessors()

    override fun setUpViews(savedInstanceState: Bundle?) {
        super.setUpViews(savedInstanceState)
        viewModel.mangaId = mangaId
    }

    companion object {

        fun createIntent(context: Context, manga: Manga): Intent {
            val intent = Intent(context, MangaInfoActivity::class.java)
            intent.putExtra(Extras.EXTRA_MANGA_ID, manga.id)
            return intent
        }

    }

}

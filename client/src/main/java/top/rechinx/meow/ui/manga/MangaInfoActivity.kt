package top.rechinx.meow.ui.manga

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.jaredrummler.cyanea.inflator.CyaneaViewProcessor
import timber.log.Timber
import top.rechinx.meow.R
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.global.Extras
import top.rechinx.meow.rikka.viewmodel.getViewModel
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.base.getCyaneaViewProcessors
import javax.inject.Inject

class MangaInfoActivity : BaseActivity(), CyaneaViewProcessor.Provider {

    private val mangaId by lazy {
        intent.getLongExtra(Extras.EXTRA_MANGA_ID, 0)
    }

    @Inject lateinit var vmFactory: MangaInfoViewModel.Factory

    private val viewModel by lazy {
        getViewModel<MangaInfoViewModel> {
            vmFactory.create(mangaId)
        }
    }

    override fun getLayoutRes(): Int
            = R.layout.activity_manga_info

    override fun getViewProcessors(): Array<CyaneaViewProcessor<out View>> = getCyaneaViewProcessors()

    override fun setUpViews(savedInstanceState: Bundle?) {
        super.setUpViews(savedInstanceState)
        //viewModel.mangaId = mangaId
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MangaInfoFragment.newInstance(mangaId))
                .commit()
    }

    companion object {

        fun createIntent(context: Context, manga: Manga): Intent {
            val intent = Intent(context, MangaInfoActivity::class.java)
            intent.putExtra(Extras.EXTRA_MANGA_ID, manga.id)
            return intent
        }

    }

}

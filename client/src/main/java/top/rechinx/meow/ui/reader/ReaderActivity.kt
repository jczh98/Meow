package top.rechinx.meow.ui.reader

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_reader.*
import kotlinx.android.synthetic.main.custom_reader_info.*
import me.zhanghai.android.systemuihelper.SystemUiHelper
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import timber.log.Timber
import top.rechinx.meow.R
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.page.model.ReaderChapter
import top.rechinx.meow.domain.page.model.ReaderPage
import top.rechinx.meow.domain.page.model.ViewerChapters
import top.rechinx.meow.global.Extras
import top.rechinx.meow.rikka.ext.gone
import top.rechinx.meow.rikka.ext.visible
import top.rechinx.meow.rikka.livedata.scanWithPrevious
import top.rechinx.meow.rikka.viewmodel.getViewModel
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.reader.viewer.BaseViewer
import top.rechinx.meow.ui.reader.viewer.pager.L2RPagerViewer
import top.rechinx.meow.ui.reader.viewer.pager.R2LPagerViewer
import top.rechinx.meow.ui.reader.viewer.webtoon.WebtoonViewer
import javax.inject.Inject

class ReaderActivity : BaseActivity() {

    private val mangaId by lazy { intent.getLongExtra(Extras.EXTRA_MANGA_ID, -1L) }
    private val chapterId by lazy { intent.getLongExtra(Extras.EXTRA_CHAPTER_ID, -1L) }
    private val isContinued by lazy { intent.getBooleanExtra(Extras.EXTRA_CONTINUE_READ, false) }

    @Inject lateinit var vmFactory: ReaderViewModel.Factory

    val viewModel: ReaderViewModel by lazy {
        getViewModel<ReaderViewModel> {
            vmFactory.create(ReaderViewModelParams(mangaId, chapterId, isContinued))
        }
    }

    var viewer: BaseViewer? = null
        private set

    var menuVisible = false
        private set

    @Inject lateinit var preferences: PreferenceHelper

    private var systemUi: SystemUiHelper? = null

    private var config: ReaderConfig? = null

    override fun getLayoutRes(): Int = R.layout.activity_reader

    override fun setUpViews(savedInstanceState: Bundle?) {
        super.setUpViews(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        config = ReaderConfig()
        initializeMenu()
        initSubscriptions()
    }

    private fun initSubscriptions() {
        viewModel.stateLiveData
                .scanWithPrevious()
                .observe(this, Observer { (state, prevState) ->
                    if (state.manga !== null) {
                        if (state.manga !== prevState?.manga) {
                            viewModel.loadInitialChapter()
                        }
                        if (state.viewer != prevState?.viewer) {
                            setManga(state.manga)
                        }
                    }
                    if (state.manga != null && state.chapters != null) {
                        setChapters(state.chapters)
                    }
                })
    }

    fun setManga(manga: Manga) {
        val preViewer = viewer
        val newViewer = when(viewModel.getMangaViewer()) {
            LEFT_TO_RIGHT -> L2RPagerViewer(this, preferences)
            RIGHT_TO_LEFT -> R2LPagerViewer(this, preferences)
            WEBTOON -> WebtoonViewer(this, preferences)
            else -> L2RPagerViewer(this, preferences)
        }
        if(preViewer != null) {
            preViewer.destroy()
            reader_container.removeAllViews()
        }
        viewer = newViewer
        reader_container.addView(newViewer.getView())
        toolbar.title = manga.title
        reader_seekbar.setReverse(newViewer is R2LPagerViewer)
        wait_progress_bar.visible()
        wait_progress_bar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_long))
    }

    fun setChapters(viewerChapters: ViewerChapters) {
        wait_progress_bar.visibility = View.GONE
        viewer?.setChapters(viewerChapters)
        toolbar.subtitle = viewerChapters.currChapter.chapter.name
        chapter_title.text = viewerChapters.currChapter.chapter.name
    }

    private fun initializeMenu() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        reader_seekbar.setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
                if (viewer != null && fromUser) {
                    moveToPageIndex(value)
                }
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
            }

        })
        setMenuVisibility(menuVisible)
    }

    /**
     * Dispatches a key event. If the viewer doesn't handle it, call the default implementation.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val handled = viewer?.handleKeyEvent(event) ?: false
        return handled || super.dispatchKeyEvent(event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.reader, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> ReaderSetting(this, preferences).show()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus) {
            setMenuVisibility(menuVisible, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewer?.destroy()
        viewer = null
        config?.destroy()
        config = null
    }

    private fun setMenuVisibility(visible: Boolean, animate: Boolean = true) {
        menuVisible = visible
        if(visible) {
            systemUi?.show()
            reader_menu.visibility = View.VISIBLE
            if(animate) {
                val toolbarAnimation = AnimationUtils.loadAnimation(this, R.anim.enter_from_top)
                toolbarAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {}

                    override fun onAnimationStart(animation: Animation) {
                        // Fix status bar being translucent the first time it's opened.
                        if (Build.VERSION.SDK_INT >= 21) {
                            window.addFlags(
                                    WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        }
                    }
                })
                toolbar.startAnimation(toolbarAnimation)

                val bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.enter_from_bottom)
                reader_menu_bottom.startAnimation(bottomAnimation)
            }
        } else {
            systemUi?.hide()
            if (animate) {
                val toolbarAnimation = AnimationUtils.loadAnimation(this, R.anim.exit_to_top)
                toolbarAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation) {
                        reader_menu.visibility = View.GONE
                    }
                })
                toolbar.startAnimation(toolbarAnimation)

                val bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.exit_to_bottom)
                reader_menu_bottom.startAnimation(bottomAnimation)
            }
        }
    }

    fun toggleMenu() {
        setMenuVisibility(!menuVisible)
    }

    fun showMenu() {
        if (!menuVisible) {
            setMenuVisibility(true)
        }
    }

    fun requestPreloadChapter(chapter: ReaderChapter) {
        viewModel.preloadChapter(chapter)
    }

    fun onPageLongTap(page: ReaderPage) {

    }

    @SuppressLint("SetTextI18n")
    fun onPageSelected(page: ReaderPage) {
        viewModel.onPageSelected(page)
        val pages = page.chapter.pages ?: return

        // Set bottom page number
        page_number.text = "${page.number}/${pages.size}"

        // Set seekbar progress
        reader_seekbar.max = pages.lastIndex
        reader_seekbar.progress = page.index
    }

    fun moveToPageIndex(index: Int) {
        val viewer = viewer ?: return
        val currentChapter = viewModel.getCurrentChapter() ?: return
        val page = currentChapter.pages?.getOrNull(index) ?: return
        viewer.moveToPage(page)
    }

    private inner class ReaderConfig {

        private val disposable = CompositeDisposable()

        init {
            disposable += preferences.fullscreen().asObservable()
                    .subscribe { setFullScreen(it) }

            disposable += preferences.hiddenReaderInfo().asObservable()
                    .subscribe { enableReaderInfo(!it) }
        }

        fun destroy() {
            disposable.clear()
        }

        private fun enableReaderInfo(enable: Boolean) {
            if (enable) {
                reader_info.visible()
            } else {
                reader_info.gone()
            }
        }

        private fun setFullScreen(enable: Boolean) {
            systemUi = if(enable) {
                val level = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    SystemUiHelper.LEVEL_IMMERSIVE
                } else {
                    SystemUiHelper.LEVEL_HIDE_STATUS_BAR
                }
                val flags = SystemUiHelper.FLAG_IMMERSIVE_STICKY or SystemUiHelper.FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES
                SystemUiHelper(this@ReaderActivity, level, flags)
            } else {
                null
            }
        }
    }


    companion object {

        const val LEFT_TO_RIGHT = 1
        const val RIGHT_TO_LEFT = 2
        const val WEBTOON = 3

        fun createIntent(context: Context, manga: Manga, chapter: Chapter, isContinued: Boolean = false): Intent {
            val intent = Intent(context, ReaderActivity::class.java)
            intent.putExtra(Extras.EXTRA_MANGA_ID, manga.id)
            intent.putExtra(Extras.EXTRA_CHAPTER_ID, chapter.id)
            intent.putExtra(Extras.EXTRA_CONTINUE_READ, isContinued)
            return intent
        }
    }
}
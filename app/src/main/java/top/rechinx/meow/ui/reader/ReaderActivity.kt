package top.rechinx.meow.ui.reader

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_reader.*
import kotlinx.android.synthetic.main.custom_reader_info.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import me.zhanghai.android.systemuihelper.SystemUiHelper
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.koin.android.ext.android.inject
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.global.Extras
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.ui.reader.model.ReaderChapter
import top.rechinx.meow.ui.reader.model.ReaderPage
import top.rechinx.meow.ui.reader.model.ViewerChapters
import top.rechinx.meow.ui.reader.viewer.BaseViewer
import top.rechinx.meow.ui.reader.viewer.pager.L2RPagerViewer
import top.rechinx.meow.ui.reader.viewer.pager.R2LPagerViewer
import top.rechinx.meow.ui.reader.viewer.webtoon.WebtoonViewer
import top.rechinx.rikka.ext.visible
import top.rechinx.rikka.mvp.MvpAppCompatActivity
import top.rechinx.rikka.mvp.factory.RequiresPresenter

@RequiresPresenter(ReaderPresenter::class)
class ReaderActivity: MvpAppCompatActivity<ReaderPresenter>() {

    var viewer: BaseViewer? = null
        private set

    val sourceId by lazy { intent.getLongExtra(Extras.EXTRA_SOURCE, 0) }
    val mangaId by lazy { intent.getLongExtra(Extras.EXTRA_MANGA_ID, -1L) }
    val chapterId by lazy { intent.getLongExtra(Extras.EXTRA_CHAPTER_ID, -1L) }
    val isContinued by lazy { intent.getBooleanExtra(Extras.EXTRA_CONTINUE_READ, false) }

    var menuVisible = false
        private set

    private val preference: PreferenceHelper by inject()

    private var systemUi: SystemUiHelper? = null

    private var config: ReaderConfig? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
        setSupportActionBar(customToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(presenter.needsInit()) {
            if (mangaId == -1L || chapterId == -1L) {
                finish()
                return
            }
            presenter.loadInit(mangaId, chapterId, isContinued)
        }
        config = ReaderConfig()
        initializeMenu()
    }

    private fun initializeMenu() {
        customToolbar?.setNavigationOnClickListener { onBackPressed() }
        readerSeekBar.setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
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
            R.id.action_settings -> ReaderSetting(this).show()
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

    fun setManga(manga: Manga) {
        val preViewer = viewer
        val newViewer = when(presenter.getMangaViewer()) {
            LEFT_TO_RIGHT -> L2RPagerViewer(this)
            RIGHT_TO_LEFT -> R2LPagerViewer(this)
            WEBTOON -> WebtoonViewer(this)
            else -> L2RPagerViewer(this)
        }
        if(preViewer != null) {
            preViewer.destroy()
            viewerContainer.removeAllViews()
        }
        viewer = newViewer
        viewerContainer.addView(newViewer.getView())
        customToolbar?.title = manga.title
        readerSeekBar.setReverse(newViewer is R2LPagerViewer)
        waitProgressBar.visible()
        waitProgressBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_long))
    }

    private fun setMenuVisibility(visible: Boolean, animate: Boolean = true) {
        menuVisible = visible
        if(visible) {
            systemUi?.show()
            readerMenu.visibility = View.VISIBLE
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
                customToolbar?.startAnimation(toolbarAnimation)

                val bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.enter_from_bottom)
                readerMenuBottom.startAnimation(bottomAnimation)
            }
        } else {
            systemUi?.hide()
            if (animate) {
                val toolbarAnimation = AnimationUtils.loadAnimation(this, R.anim.exit_to_top)
                toolbarAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation) {
                        readerMenu.visibility = View.GONE
                    }
                })
                customToolbar?.startAnimation(toolbarAnimation)

                val bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.exit_to_bottom)
                readerMenuBottom.startAnimation(bottomAnimation)
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
        presenter.preloadChapter(chapter)
    }

    fun onPageLongTap(page: ReaderPage) {

    }

    fun setChapters(viewerChapters: ViewerChapters) {
        waitProgressBar.visibility = View.GONE
        viewer?.setChapters(viewerChapters)
        customToolbar?.subtitle = viewerChapters.currChapter.chapter.name
        readerChapterTitle.text = viewerChapters.currChapter.chapter.name
    }

    @SuppressLint("SetTextI18n")
    fun onPageSelected(page: ReaderPage) {
        presenter.onPageSelected(page)
        val pages = page.chapter.pages ?: return

        // Set bottom page number
        readerPageNumber.text = "${page.number}/${pages.size}"

        // Set seekbar progress
        readerSeekBar.max = pages.lastIndex
        readerSeekBar.progress = page.index
    }

    fun moveToPageIndex(index: Int) {
        val viewer = viewer ?: return
        val currentChapter = presenter.getCurrentChapter() ?: return
        val page = currentChapter.pages?.getOrNull(index) ?: return
        viewer.moveToPage(page)
    }

    override fun onPause() {
        super.onPause()
        presenter.saveLastRead(presenter.getCurrentChapter())
    }

    override fun onDestroy() {
        super.onDestroy()
        viewer?.destroy()
        viewer = null
        config?.destroy()
        config = null
    }


    private inner class ReaderConfig {

        private val disposable = CompositeDisposable()

        init {
            disposable += preference.fullscreen().asObservable()
                    .subscribe { setFullScreen(it) }
        }

        fun destroy() {
            disposable.clear()
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
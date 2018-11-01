package top.rechinx.meow.ui.reader

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import me.zhanghai.android.systemuihelper.SystemUiHelper
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.koin.android.ext.android.inject
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.global.Extras
import top.rechinx.meow.support.ext.visible
import top.rechinx.meow.support.log.L
import top.rechinx.meow.support.preference.PreferenceHelper
import top.rechinx.meow.support.viewbinding.bindView
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.reader.model.ReaderChapter
import top.rechinx.meow.ui.reader.model.ReaderPage
import top.rechinx.meow.ui.reader.model.ViewerChapters
import top.rechinx.meow.ui.reader.viewer.BaseViewer
import top.rechinx.meow.ui.reader.viewer.pager.L2RPagerViewer
import top.rechinx.meow.ui.reader.viewer.pager.R2LPagerViewer
import top.rechinx.meow.ui.reader.viewer.webtoon.WebtoonViewer
import top.rechinx.meow.widget.ReverseSeekBar

class ReaderActivity: BaseActivity(), ReaderContarct.View {

    override val presenter: ReaderContarct.Presenter by inject()

    var viewer: BaseViewer? = null
        private set

    val viewerContainer by bindView<FrameLayout>(R.id.viewer_container)
    val readerMenu : FrameLayout by bindView(R.id.reader_menu)
    val readerMenuBottom by bindView<LinearLayout>(R.id.reader_menu_bottom)
    val readerSeekbar by bindView<ReverseSeekBar>(R.id.reader_seek_bar)
    val readerPageNumber by bindView<TextView>(R.id.reader_chapter_page)
    val waitProgressBar by bindView<ProgressBar>(R.id.please_wait)

    val sourceId by lazy { intent.getLongExtra(Extras.EXTRA_SOURCE, 0) }
    val mangaId by lazy { intent.getLongExtra(Extras.EXTRA_MANGA_ID, -1L) }
    val chapterId by lazy { intent.getLongExtra(Extras.EXTRA_CHAPTER_ID, -1L) }

    var menuVisible = false
        private set

    private val preference: PreferenceHelper by inject()

    private var systemUi: SystemUiHelper? = null

    private var config: ReaderConfig? = null


    override fun initViews() {
        if(presenter.needsInit()) {
            if (mangaId == -1L || chapterId == -1L) {
                finish()
                return
            }
            presenter.loadInit(mangaId, chapterId)
        }
        config = ReaderConfig()
        initializeMenu()
    }

    override fun onStart() {
        super.onStart()
        presenter.subscribe(this)
    }

    private fun initializeMenu() {
        toolbar?.setNavigationOnClickListener { onBackPressed() }
        readerSeekbar.setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
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

    override fun setManga(manga: Manga) {
        val preViewer = viewer
        val newViewer = when(presenter.getMangaViewer()) {
            RIGHT_TO_LEFT -> R2LPagerViewer(this)
            else -> WebtoonViewer(this)
        }
        if(preViewer != null) {
            preViewer.destroy()
            viewerContainer.removeAllViews()
        }
        viewer = newViewer
        viewerContainer.addView(newViewer.getView())
        toolbar?.title = manga.title
        readerSeekbar.setReverse(newViewer is R2LPagerViewer)
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
                toolbar?.startAnimation(toolbarAnimation)

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
                toolbar?.startAnimation(toolbarAnimation)

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

    override fun setChapters(viewerChapters: ViewerChapters) {
        waitProgressBar.visibility = View.GONE
        viewer?.setChapters(viewerChapters)
        toolbar?.subtitle = viewerChapters.currChapter.chapter.name
    }

    @SuppressLint("SetTextI18n")
    fun onPageSelected(page: ReaderPage) {
        presenter.onPageSelected(page)
        val pages = page.chapter.pages ?: return

        // Set bottom page number
        readerPageNumber.text = "${page.number}/${pages.size}"

        // Set seekbar progress
        readerSeekbar.max = pages.lastIndex
        readerSeekbar.progress = page.index
    }

    fun moveToPageIndex(index: Int) {
        val viewer = viewer ?: return
        val currentChapter = presenter.getCurrentChapter() ?: return
        val page = currentChapter.pages?.getOrNull(index) ?: return
        viewer.moveToPage(page)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewer?.destroy()
        viewer = null
        config?.destroy()
        config = null
        presenter.unsubscribe()
    }

    override fun getLayoutId(): Int = R.layout.activity_reader

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
        const val WEBTOON = 4

        fun createIntent(context: Context, manga: Manga, chapter: Chapter): Intent {
            val intent = Intent(context, ReaderActivity::class.java)
            intent.putExtra(Extras.EXTRA_MANGA_ID, manga.id)
            intent.putExtra(Extras.EXTRA_CHAPTER_ID, chapter.id)
            return intent
        }
    }

}
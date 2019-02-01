package top.rechinx.meow.ui.reader.viewer.pager

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.NoTransition
import com.github.chrisbanes.photoview.PhotoView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.glide.GlideApp
import top.rechinx.meow.ui.reader.ReaderProgressBar
import top.rechinx.meow.R
import top.rechinx.meow.domain.page.model.ReaderPage
import top.rechinx.meow.rikka.ext.dpToPx
import java.io.InputStream
import java.util.concurrent.TimeUnit
import top.rechinx.meow.core.source.model.PageInfo
import top.rechinx.meow.rikka.ext.gone
import top.rechinx.meow.rikka.ext.visible

/**
 * View of the ViewPager that contains a page of a chapter.
 */
class PagerPageHolder(
        val viewer: PagerViewer,
        val page: ReaderPage
) : FrameLayout(viewer.activity), ViewPagerAdapter.PositionableView {

    override val item
        get() = page

    private val progressBar = ReaderProgressBar(context, null).apply {

        val size = 48.dpToPx
        layoutParams = FrameLayout.LayoutParams(size, size).apply {
            gravity = Gravity.CENTER
        }
    }

    private var photoView: PhotoView? = null

    private var retryButton: PagerButton? = null

    private var statusDisposable: Disposable? = null

    private var progressDisposable: Disposable? = null


    init {
        addView(progressBar)
        observeStatus()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unsubscribeStatus()
        unsubscribeProgress()
    }

    private fun unsubscribeProgress() {
        progressDisposable?.dispose()
        progressDisposable = null
    }

    private fun unsubscribeStatus() {
        statusDisposable?.dispose()
        statusDisposable = null
    }

    private fun observeStatus() {
        statusDisposable?.dispose()
        val loader = page.chapter.pageLoader ?: return
        statusDisposable = loader.getPage(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { precessStatus(it) }
    }

    private fun observeProgress() {
        progressDisposable?.dispose()
        progressDisposable = Observable.interval(100, TimeUnit.MILLISECONDS)
                .map { page.progress }
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { value -> progressBar.setProgress(value) }
    }

    private fun precessStatus(status: Int) {
        when(status) {
            PageInfo.QUEUE -> setQueued()
            PageInfo.LOAD_PAGE -> setLoading()
            PageInfo.DOWNLOAD_IMAGE -> {
                observeProgress()
                setDownloading()
            }
            PageInfo.READY -> {
                setImage()
            }
            PageInfo.ERROR -> {
                setError()
            }
        }
    }

    private fun setDownloading() {
        progressBar.visible()
        retryButton?.gone()
    }

    private fun setError() {
        progressBar.gone()
        initRetryButton().visible()
    }

    private fun setImage() {
        progressBar.visible()
        progressBar.completeAndFadeOut()
        retryButton?.gone()
        initPhotoView()
        val streamFn = page.stream ?: return
        var openStream: InputStream? = null
        Observable.fromCallable {
                    val stream = streamFn().buffered(16)
                    openStream = stream

                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    initPhotoView().setImage(openStream!!)
                }
                .flatMap { Observable.never<Unit>() }
                .doOnComplete { openStream?.close() }
                .subscribe()
    }

    private fun setLoading() {
        progressBar.visible()
        retryButton?.gone()
    }

    private fun setQueued() {
        progressBar.visible()
        retryButton?.gone()
    }


    private fun onImageDecoded() {
        progressBar.gone()
    }

    private fun onImageDecodeError() {
        progressBar.gone()
    }

    private fun initPhotoView(): PhotoView {
        photoView = PhotoView(context).apply {
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            isZoomable = true
        }
        addView(photoView)
        return photoView!!
    }

    private fun initRetryButton(): PagerButton {
        if (retryButton != null) return retryButton!!

        retryButton = PagerButton(context, viewer).apply {
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
            setText(R.string.action_retry)
            setOnClickListener {
                page.chapter.pageLoader?.retryPage(page)
            }
        }
        addView(retryButton)
        return retryButton!!
    }

    private fun PhotoView.setImage(stream: InputStream) {
        GlideApp.with(this)
                .load(stream)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transition(DrawableTransitionOptions.with(NoTransition.getFactory()))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                    ): Boolean {
                        onImageDecodeError()
                        return false
                    }

                    override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                    ): Boolean {
                        onImageDecoded()
                        return false
                    }
                })
                .into(this)
    }
}

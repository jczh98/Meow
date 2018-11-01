package top.rechinx.meow.ui.reader.viewer.pager

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.NoTransition
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoView
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.ui.reader.model.ReaderPage
import top.rechinx.meow.core.source.model.AbsMangaPage
import top.rechinx.meow.support.ext.gone
import top.rechinx.meow.support.log.L
import top.rechinx.meow.glide.GlideApp
import java.io.InputStream

/**
 * View of the ViewPager that contains a page of a chapter.
 */
class PagerPageHolder(
        val viewer: PagerViewer,
        val page: ReaderPage
) : FrameLayout(viewer.activity), ViewPagerAdapter.PositionableView {

    override val item
        get() = page

    private var photoView: PhotoView? = null

    private var retryButton: PagerButton? = null

    private var statusDisposable: Disposable? = null

    init {
        observeStatus()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unsubscribeStatus()
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

    private fun precessStatus(status: Int) {
        when(status) {
            AbsMangaPage.QUEUE -> setQueued()
            AbsMangaPage.LOAD_PAGE -> setLoading()
            AbsMangaPage.READY -> {
                setImage()
            }
            AbsMangaPage.ERROR -> {
                setError()
            }
        }
    }

    private fun setError() {

    }

    private fun setImage() {
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

    private fun initPhotoView(): PhotoView {
        photoView = PhotoView(context).apply {
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            isZoomable = true
        }
        addView(photoView)
        return photoView!!
    }

    private fun setLoading() {
        retryButton?.gone()
    }

    private fun setQueued() {
        retryButton?.gone()
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
                        //onImageDecodeError()
                        return false
                    }

                    override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                    ): Boolean {
                        //onImageDecoded()
                        return false
                    }
                })
                .into(this)
    }
}

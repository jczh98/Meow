package top.rechinx.meow.ui.reader.viewer.webtoon

import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatImageView
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.AbsMangaPage
import top.rechinx.meow.support.ext.gone
import top.rechinx.meow.ui.reader.model.ReaderPage
import java.io.InputStream
import top.rechinx.meow.glide.GlideApp
import top.rechinx.meow.support.ext.visible
import top.rechinx.meow.support.log.L

class WebtoonPageHolder(
        private val frame: FrameLayout,
        viewer: WebtoonViewer
) : WebtoonBaseHolder(frame, viewer) {

    private var imageView: ImageView? = null
    private var retryContainer: ViewGroup? = null
    private val parentHeight
        get() = viewer.recyclerView.height
    private var page: ReaderPage? = null
    private var statusDisposable: Disposable? = null

    init {
        frame.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun bind(page: ReaderPage) {
        this.page = page
        observeStatus()
    }

    override fun recycle() {
        unsubscribeStatus()
        imageView?.let { GlideApp.with(frame).clear(it) }
        imageView?.gone()
    }

    private fun observeStatus() {
        unsubscribeStatus()

        val page = page ?: return
        val loader = page.chapter.pageLoader ?: return
        statusDisposable = loader.getPage(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { processStatus(it) }

        addDispoable(statusDisposable)
    }

    private fun processStatus(status: Int) {
        when (status) {
            AbsMangaPage.QUEUE -> setQueued()
            AbsMangaPage.LOAD_PAGE -> setLoading()
            AbsMangaPage.DOWNLOAD_IMAGE -> {

            }
            AbsMangaPage.READY -> {
                setImage()
            }
            AbsMangaPage.ERROR -> {
                setError()
            }
        }
    }

    private fun unsubscribeStatus() {
        removeDisposable(statusDisposable)
        statusDisposable = null
    }

    private fun setError() {

    }

    private fun setQueued() {
        retryContainer?.gone()
    }

    private fun setLoading() {
        retryContainer?.gone()
    }

    private fun setImage() {
        retryContainer?.gone()

        val streamFn = page?.stream ?: return

        var openStream: InputStream? = null
        Observable.fromCallable {
                    val stream = streamFn().buffered(16)
                    openStream = stream
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    val imageView = initImageView()
                    imageView.visible()
                    imageView.setImage(openStream!!)
                    imageView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//                    val glideurl = GlideUrl(page?.imageUrl, LazyHeaders.Builder()
//                            .addHeader("Referer", "https://manhua.dmzj.com")
//                            .build())
//                    Glide.with(context).load(glideurl).into(object : SimpleTarget<Drawable>() {
//                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                            imageView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//                            imageView.setImageDrawable(resource)
//                        }
//                    })
                }
                // Keep the Rx stream alive to close the input stream only when unsubscribed
                .flatMap { Observable.never<Unit>() }
                .doOnComplete { openStream?.close() }
                .subscribe()
    }

    private fun initImageView(): ImageView {
        if (imageView != null) return imageView!!

        imageView = AppCompatImageView(context).apply {
            adjustViewBounds = true
        }
        frame.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return imageView!!
    }

    private fun ImageView.setImage(stream: InputStream) {
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
package top.rechinx.meow.ui.reader.viewer.webtoon

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import top.rechinx.meow.R
import top.rechinx.meow.support.ext.dpToPx
import top.rechinx.meow.support.ext.visibleIf
import top.rechinx.meow.support.ext.wrapContent
import top.rechinx.meow.ui.reader.model.ChapterTransition
import top.rechinx.meow.ui.reader.model.ReaderChapter

class WebtoonTransitionHolder(
        val layout: LinearLayout,
        viewer: WebtoonViewer
) : WebtoonBaseHolder(layout, viewer) {

    private var statusDisposable: Disposable? = null
    private var textView = TextView(context).apply {
        setTextColor(Color.WHITE)
    }
    private var pagesContainer = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
    }

    init {
        layout.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER

        val paddingVertical = 48.dpToPx
        val paddingHorizontal = 32.dpToPx
        layout.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)

        val childMargins = 16.dpToPx
        val childParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, childMargins, 0, childMargins)
        }

        layout.addView(textView, childParams)
        layout.addView(pagesContainer, childParams)
    }

    fun bind(transition: ChapterTransition) {
        when (transition) {
            is ChapterTransition.Prev -> bindPrevChapterTransition(transition)
            is ChapterTransition.Next -> bindNextChapterTransition(transition)
        }
    }

    override fun recycle() {
        unsubscribeStatus()
    }

    /**
     * Binds a next chapter transition on this view and subscribes to the load status.
     */
    private fun bindNextChapterTransition(transition: ChapterTransition.Next) {
        val nextChapter = transition.to

        textView.text = if (nextChapter != null) {
            SpannableStringBuilder().apply {
                append(context.getString(R.string.transition_finished))
                setSpan(StyleSpan(Typeface.BOLD), 0, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                append("\n${transition.from.chapter.name}\n\n")
                val currSize = length
                append(context.getString(R.string.transition_next))
                setSpan(StyleSpan(Typeface.BOLD), currSize, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                append("\n${nextChapter.chapter.name}\n\n")
            }
        } else {
            context.getString(R.string.transition_no_next)
        }

        if (nextChapter != null) {
            observeStatus(nextChapter, transition)
        }
    }

    /**
     * Binds a previous chapter transition on this view and subscribes to the page load status.
     */
    private fun bindPrevChapterTransition(transition: ChapterTransition.Prev) {
        val prevChapter = transition.to

        textView.text = if (prevChapter != null) {
            SpannableStringBuilder().apply {
                append(context.getString(R.string.transition_current))
                setSpan(StyleSpan(Typeface.BOLD), 0, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                append("\n${transition.from.chapter.name}\n\n")
                val currSize = length
                append(context.getString(R.string.transition_previous))
                setSpan(StyleSpan(Typeface.BOLD), currSize, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                append("\n${prevChapter.chapter.name}\n\n")
            }
        } else {
            context.getString(R.string.transition_no_previous)
        }

        if (prevChapter != null) {
            observeStatus(prevChapter, transition)
        }
    }

    private fun observeStatus(chapter: ReaderChapter, transition: ChapterTransition) {
        unsubscribeStatus()

        statusDisposable = chapter.stateObserver
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state ->
                    pagesContainer.removeAllViews()
                    when (state) {
                        is ReaderChapter.State.Wait -> {}
                        is ReaderChapter.State.Loading -> setLoading()
                        is ReaderChapter.State.Error -> setError(state.error, transition)
                        is ReaderChapter.State.Loaded -> setLoaded()
                    }
                    pagesContainer.visibleIf { pagesContainer.childCount > 0 }
                }

        addDispoable(statusDisposable)
    }

    private fun setLoaded() {

    }

    private fun setError(error: Throwable, transition: ChapterTransition) {
        val textView = AppCompatTextView(context).apply {
            wrapContent()
            text = context.getString(R.string.transition_pages_error, error.message)
        }

        val retryBtn = AppCompatButton(context).apply {
            wrapContent()
            setText(R.string.action_retry)
            setOnClickListener {
                val toChapter = transition.to
                if (toChapter != null) {
                    viewer.activity.requestPreloadChapter(toChapter)
                }
            }
        }

        pagesContainer.addView(textView)
        pagesContainer.addView(retryBtn)
    }

    private fun setLoading() {
        val progress = ProgressBar(context, null, android.R.attr.progressBarStyle)

        val textView = AppCompatTextView(context).apply {
            wrapContent()
            setText(R.string.transition_pages_loading)
        }

        pagesContainer.addView(progress)
        pagesContainer.addView(textView)
    }

    private fun unsubscribeStatus() {
        removeDisposable(statusDisposable)
        statusDisposable = null
    }
}
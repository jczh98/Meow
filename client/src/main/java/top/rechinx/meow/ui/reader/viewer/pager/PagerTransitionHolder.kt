package top.rechinx.meow.ui.reader.viewer.pager

import android.graphics.Color
import android.graphics.Typeface
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
import top.rechinx.meow.domain.page.model.ChapterTransition
import top.rechinx.meow.domain.page.model.ReaderChapter
import top.rechinx.meow.rikka.ext.dpToPx
import top.rechinx.meow.rikka.ext.wrapContent

class PagerTransitionHolder(
        val viewer: PagerViewer,
        val transition: ChapterTransition
) : LinearLayout(viewer.activity), ViewPagerAdapter.PositionableView {

    override val item
        get() = transition

    private var statusDisposable: Disposable? = null

    private var textView = TextView(context).apply {
        wrapContent()
        setTextColor(Color.WHITE)
    }

    private var pagesContainer = LinearLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
        gravity = Gravity.CENTER
    }

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        val sidePadding = 64.dpToPx
        setPadding(sidePadding, 0, sidePadding, 0)
        addView(textView)
        addView(pagesContainer)

        when (transition) {
            is ChapterTransition.Prev -> bindPrevChapterTransition()
            is ChapterTransition.Next -> bindNextChapterTransition()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        statusDisposable?.dispose()
        statusDisposable = null
    }

    private fun bindNextChapterTransition() {
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
            observeStatus(nextChapter)
        }
    }

    private fun bindPrevChapterTransition() {
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
            observeStatus(prevChapter)
        }
    }

    private fun observeStatus(chapter: ReaderChapter) {
        statusDisposable?.dispose()
        statusDisposable = chapter.stateObserver
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state ->
                    pagesContainer.removeAllViews()
                    when (state) {
                        is ReaderChapter.State.Wait -> {}
                        is ReaderChapter.State.Loading -> setLoading()
                        is ReaderChapter.State.Error -> setError(state.error)
                        is ReaderChapter.State.Loaded -> setLoaded()
                    }
                }
    }

    private fun setLoaded() {

    }

    private fun setError(error: Throwable) {
        val textView = AppCompatTextView(context).apply {
            wrapContent()
            text = context.getString(R.string.transition_pages_error, error.message)
        }

        val retryBtn = PagerButton(context, viewer).apply {
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


}

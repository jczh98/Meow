package top.rechinx.meow.ui.reader.viewer.webtoon

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import io.reactivex.disposables.Disposable

abstract class WebtoonBaseHolder(
        view: View,
        protected val viewer: WebtoonViewer
) : RecyclerView.ViewHolder(view) {

    val context: Context get() = itemView.context

    open fun recycle() {}

    protected fun addDispoable(disposable: Disposable?) {
        disposable?.let { viewer.disposables.add(it) }
    }

    protected fun removeDisposable(disposable: Disposable?) {
        disposable?.let { viewer.disposables.remove(it) }
    }

}

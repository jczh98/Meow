@file:Suppress("PackageDirectoryMismatch")
package androidx.recyclerview.widget

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import top.rechinx.meow.ui.reader.ReaderActivity

class WebtoonLayoutManager(activity: ReaderActivity) : LinearLayoutManager(activity) {

    private val extraLayoutSpace = activity.resources.displayMetrics.heightPixels / 2

    init {
        isItemPrefetchEnabled = false
    }

    /**
     * Returns the custom extra layout space.
     */
    override fun getExtraLayoutSpace(state: RecyclerView.State): Int {
        return extraLayoutSpace
    }

    /**
     * Returns the position of the last item whose end side is visible on screen.
     */
    fun findLastEndVisibleItemPosition(): Int {
        ensureLayoutState()
        @ViewBoundsCheck.ViewBounds val preferredBoundsFlag =
                (ViewBoundsCheck.FLAG_CVE_LT_PVE or ViewBoundsCheck.FLAG_CVE_EQ_PVE)

        val fromIndex = childCount - 1
        val toIndex = -1

        val child = if (mOrientation == HORIZONTAL)
            mHorizontalBoundCheck
                    .findOneViewWithinBoundFlags(fromIndex, toIndex, preferredBoundsFlag, 0)
        else
            mVerticalBoundCheck
                    .findOneViewWithinBoundFlags(fromIndex, toIndex, preferredBoundsFlag, 0)

        return if (child == null) RecyclerView.NO_POSITION else getPosition(child)
    }
}
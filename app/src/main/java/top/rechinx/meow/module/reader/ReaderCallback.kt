package top.rechinx.meow.module.reader

import android.view.View

interface ReaderCallback {

    fun onReaderPageChanged(page: Int)

    fun getCurrentPage(): Int

    fun onPrevChapter()

    fun onNextChapter()

    fun onLoadPrevChapter()

    fun onLoadNextChapter()
}
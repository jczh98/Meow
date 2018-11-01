package top.rechinx.meow.data.database.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import top.rechinx.meow.core.source.model.AbsChapter
import top.rechinx.meow.core.source.model.AbsChapterImpl

@Entity
data class Chapter(@ColumnInfo override var url: String?,
                   @ColumnInfo override var name: String?,
                   @ColumnInfo override var date_updated: Long,
                   @ColumnInfo override var chapter_number: String?,
                   @PrimaryKey(autoGenerate = true) var id: Long = 0,
                   @ColumnInfo var manga_id: Long = 0,
                   @ColumnInfo var last_page_read: Int = 0) : AbsChapter {

    constructor(): this(null, null, 0, null)

    companion object {

        fun create(): Chapter = Chapter()
    }
}
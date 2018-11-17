package top.rechinx.meow.data.database.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import top.rechinx.meow.core.source.model.SChapter

@Entity
data class Chapter(@ColumnInfo override var url: String?,
                   @ColumnInfo override var name: String?,
                   @ColumnInfo override var date_updated: Long,
                   @ColumnInfo override var chapter_number: String?,
                   @PrimaryKey(autoGenerate = true) var id: Long = 0,
                   @ColumnInfo var manga_id: Long = 0,
                   @ColumnInfo var last_page_read: Int = 0,
                   @ColumnInfo var download: Boolean = false) : SChapter, Parcelable {
    constructor() : this(null, null, 0, null)

    @Ignore constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readLong(),
            source.readString(),
            source.readLong(),
            source.readLong(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(url)
        writeString(name)
        writeLong(date_updated)
        writeString(chapter_number)
        writeLong(id)
        writeLong(manga_id)
        writeInt(last_page_read)
    }

    companion object {
        fun create(): Chapter = Chapter()

        @JvmField
        val CREATOR: Parcelable.Creator<Chapter> = object : Parcelable.Creator<Chapter> {
            override fun createFromParcel(source: Parcel): Chapter = Chapter(source)
            override fun newArray(size: Int): Array<Chapter?> = arrayOfNulls(size)
        }
    }
}
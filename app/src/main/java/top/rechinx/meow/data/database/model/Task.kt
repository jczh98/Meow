package top.rechinx.meow.data.database.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import top.rechinx.meow.core.source.model.MangaPage

@Entity
class Task : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    @ColumnInfo
    var mangaId: Long = 0      // 漫画主键
    @ColumnInfo
    var path: String? = null
    @ColumnInfo
    var title: String? = null
    @ColumnInfo
    var progress: Int = 0
    @ColumnInfo
    var max: Int = 0

    @Transient
    @ColumnInfo
    var sourceId: Long = 0
    @Transient
    @ColumnInfo
    var sourceName: String? = null
    @Transient
    @ColumnInfo
    var mangaUrl: String? = null  // 漫画 ID
    @Transient
    @ColumnInfo
    var mangaName: String? = null
    @Transient
    @ColumnInfo
    var state: Int = 0

    @Ignore
    var chapter: Chapter? = null

    val isFinish: Boolean
        get() = max != 0 && progress == max

    constructor(source: Parcel) {
        this.id = source.readLong()
        this.mangaId = source.readLong()
        this.path = source.readString()
        this.title = source.readString()
        this.progress = source.readInt()
        this.max = source.readInt()
        this.sourceId = source.readLong()
        this.mangaUrl = source.readString()
        this.state = source.readInt()
        this.sourceName = source.readString()
        this.mangaName = source.readString()
        this.chapter = source.readParcelable(Chapter.javaClass.classLoader)
    }

    @Ignore
    constructor(id: Long?, key: Long, @NotNull path: String, @NotNull title: String, progress: Int,
                max: Int) {
        this.id = id
        this.mangaId = key
        this.path = path
        this.title = title
        this.progress = progress
        this.max = max
    }

    constructor() {}

    override fun equals(o: Any?): Boolean {
        return o is Task && o.id == id
    }

    override fun hashCode(): Int {
        return if (id == null) super.hashCode() else id!!.hashCode()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id!!)
        dest.writeLong(mangaId)
        dest.writeString(path)
        dest.writeString(title)
        dest.writeInt(progress)
        dest.writeInt(max)
        dest.writeLong(sourceId)
        dest.writeString(mangaUrl)
        dest.writeInt(state)
        dest.writeString(sourceName)
        dest.writeString(mangaName)
        dest.writeParcelable(chapter, flags)
    }

    companion object {

        val STATE_FINISH = 0
        val STATE_PAUSE = 1
        val STATE_PARSE = 2
        val STATE_DOING = 3
        val STATE_WAIT = 4
        val STATE_ERROR = 5

        @JvmField
        val CREATOR: Parcelable.Creator<Task> = object : Parcelable.Creator<Task> {
            override fun createFromParcel(source: Parcel): Task {
                return Task(source)
            }

            override fun newArray(size: Int): Array<Task?> {
                return arrayOfNulls(size)
            }
        }
    }

}

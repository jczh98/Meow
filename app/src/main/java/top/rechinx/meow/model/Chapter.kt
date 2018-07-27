package top.rechinx.meow.model

import android.os.Parcel
import android.os.Parcelable

data class Chapter(var title: String?,
                   var chapter_id: String?,
                   var count: Int) : Parcelable {

    constructor(title: String, chapter_id: String) : this(title, chapter_id, 0)

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(title)
        dest?.writeString(chapter_id)
        dest?.writeInt(count)
    }

    override fun describeContents(): Int  = 0

    companion object CREATOR : Parcelable.Creator<Chapter> {
        override fun createFromParcel(parcel: Parcel): Chapter {
            return Chapter(parcel)
        }

        override fun newArray(size: Int): Array<Chapter?> {
            return arrayOfNulls(size)
        }
    }

}
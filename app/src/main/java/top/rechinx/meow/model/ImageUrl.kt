package top.rechinx.meow.model

import android.os.Parcel
import android.os.Parcelable


data class ImageUrl(var id: Int,
                    var page_number: Int,
                    var imageUrl: String?,
                    var chapter: String?,
                    var headers: String?,
                    var dns: String?): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    constructor(number: Int, imageUrl: String?, chapter: String?, headers: String?, dns: String?): this(0, number, imageUrl, chapter, headers, dns)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(page_number)
        parcel.writeString(imageUrl)
        parcel.writeString(chapter)
        parcel.writeString(headers)
        parcel.writeString(dns)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageUrl> {
        override fun createFromParcel(parcel: Parcel): ImageUrl {
            return ImageUrl(parcel)
        }

        override fun newArray(size: Int): Array<ImageUrl?> {
            return arrayOfNulls(size)
        }
    }

}
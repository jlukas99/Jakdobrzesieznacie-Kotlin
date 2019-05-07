package pl.idappstudio.jakdobrzesieznacie.model

import android.os.Parcel
import android.os.Parcelable

data class UserData(val uid: String, val name: String, val image: String, val fb: Boolean, val gender: String, val type: String, val status: String, val public: Boolean, val notification: Boolean, val registrationTokens: MutableList<String>) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        mutableListOf()
    )

    constructor(): this("", "", "", false, "", "", "", false, true, mutableListOf())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeByte(if (fb) 1 else 0)
        parcel.writeString(gender)
        parcel.writeString(type)
        parcel.writeString(status)
        parcel.writeByte(if (public) 1 else 0)
        parcel.writeByte(if (notification) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }

}
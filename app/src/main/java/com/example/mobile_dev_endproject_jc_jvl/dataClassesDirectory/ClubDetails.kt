package com.example.mobile_dev_endproject_jc_jvl.dataClassesDirectory

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint

data class ClubDetails(
    val clubName: String,
    val clubAddress: String,
    val clubLocation: GeoPoint
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        GeoPoint(parcel.readDouble(), parcel.readDouble())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(clubName)
        parcel.writeString(clubAddress)
        parcel.writeDouble(clubLocation.latitude)
        parcel.writeDouble(clubLocation.longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClubDetails> {
        override fun createFromParcel(parcel: Parcel): ClubDetails {
            return ClubDetails(parcel)
        }

        override fun newArray(size: Int): Array<ClubDetails?> {
            return arrayOfNulls(size)
        }
    }
}




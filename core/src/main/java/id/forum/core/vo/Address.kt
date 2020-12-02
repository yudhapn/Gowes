package id.forum.core.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val city: String = "",
    val province: String = ""
): Parcelable
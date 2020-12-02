package id.forum.core.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Profile(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("avatar")
    var avatar: String = "",
    @SerializedName("biodata")
    val biodata: String = "",
    @SerializedName("createdOn")
    val createdOn: Date = Calendar.getInstance().time
) : Parcelable
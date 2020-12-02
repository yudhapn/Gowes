package id.forum.core.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Token(
    @SerializedName("access_token")
    var accessToken: String = "",
    @SerializedName("refresh_token")
    var refreshToken: String = "",
    @SerializedName("user_id")
    var userId: String = "",
    @SerializedName("token_fb")
    var firebaseToken: String = "",
    var timeRetrive: Date = Calendar.getInstance().time
) : Parcelable
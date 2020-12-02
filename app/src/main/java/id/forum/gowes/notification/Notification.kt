package id.forum.gowes.notification

import com.google.gson.annotations.SerializedName

data class Notification(
   @SerializedName("body")
   val body: String = "",
   @SerializedName("title")
   val title: String = "",
   @SerializedName("receiverId")
   val receiverId: String = "",
   @SerializedName("lastText")
   val lastText: String = "",
   @SerializedName("idChat")
   val idChat: String = ""
)

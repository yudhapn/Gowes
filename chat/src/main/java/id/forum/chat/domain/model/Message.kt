package id.forum.chat.domain.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Message(
    var id: String = "",
    var senderId: String = "",
    var content: String = "",
    var image: String = "",
    var sentOn: Date = Calendar.getInstance().time
) : Parcelable

object MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Message, newItem: Message) =
        oldItem == newItem
}
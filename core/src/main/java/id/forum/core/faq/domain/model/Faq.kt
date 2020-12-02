package id.forum.core.faq.domain.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Faq(
    val id: String = "",
    val title: String = "",
    val question: String = "",
    val answer: String = "",
    val createdOn: Date = Calendar.getInstance().time
) : Parcelable

object FaqDiffCallback : DiffUtil.ItemCallback<Faq>() {
    override fun areItemsTheSame(oldItem: Faq, newItem: Faq) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Faq, newItem: Faq) = oldItem == newItem
}
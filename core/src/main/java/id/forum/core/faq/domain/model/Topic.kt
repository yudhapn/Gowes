package id.forum.core.faq.domain.model

import androidx.recyclerview.widget.DiffUtil
import java.util.*

data class Topic(
    val id: String = "",
    val name: String = "",
    val faqs: List<Faq> = emptyList(),
    var isExpand: Boolean = false,
    val createdOn: Date = Calendar.getInstance().time
)

object TopicDiffCallback : DiffUtil.ItemCallback<Topic>() {
    override fun areItemsTheSame(oldItem: Topic, newItem: Topic) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Topic, newItem: Topic) = oldItem == newItem
}

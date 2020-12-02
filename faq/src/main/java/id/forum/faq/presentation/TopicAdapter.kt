package id.forum.faq.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import id.forum.core.faq.domain.model.Topic
import id.forum.core.faq.domain.model.TopicDiffCallback
import id.forum.faq.databinding.ItemListTopicLayoutBinding
import id.forum.faq.presentation.FaqAdapter.FaqAdapterListener

class TopicAdapter(
    private val listener: TopicAdapterListener,
    private val faqListener: FaqAdapterListener
) : ListAdapter<Topic, TopicViewHolder>(TopicDiffCallback) {

    interface TopicAdapterListener {
        fun onTopicClicked(imageView: ImageView, contentLayout: View, topic: Topic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder =
        TopicViewHolder(
            ItemListTopicLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener,
            faqListener
        )

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) =
        holder.bind(getItem(position))
}

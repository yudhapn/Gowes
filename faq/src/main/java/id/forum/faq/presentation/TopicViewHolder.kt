package id.forum.faq.presentation

import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.forum.core.faq.domain.model.Topic
import id.forum.faq.R
import id.forum.faq.databinding.ItemListTopicLayoutBinding


class TopicViewHolder(
    private val binding: ItemListTopicLayoutBinding,
    private val topicListener: TopicAdapter.TopicAdapterListener,
    private val faqListener: FaqAdapter.FaqAdapterListener
) : ViewHolder(binding.root) {

    init {
        binding.run {
            listener = topicListener
        }
    }

    fun bind(topic: Topic) {
        binding.apply {
            this.topic = topic
            val drawable = if (topic.isExpand) {
                contentLayout.visibility = View.VISIBLE
                R.drawable.ic_animated_arrow_up
            } else {
                contentLayout.visibility = View.GONE
                R.drawable.ic_animated_arrow_down
            }
            arrowDownImageView.setImageResource(drawable)
            if (topic.faqs.isNotEmpty()) {
                faqRecyclerView.adapter = FaqAdapter(faqListener)
                (faqRecyclerView.adapter as FaqAdapter).submitList(topic.faqs)
                faqRecyclerView.addItemDecoration(
                    DividerItemDecoration(
                        faqRecyclerView.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
            arrowDownImageView.setOnClickListener {
                topicListener.onTopicClicked(arrowDownImageView, contentLayout, topic)
            }
            topicTextView.setOnClickListener {
                topicListener.onTopicClicked(arrowDownImageView, contentLayout, topic)
            }
        }
    }
}
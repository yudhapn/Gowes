package id.forum.faq.presentation

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import id.forum.core.faq.domain.model.Topic

@BindingAdapter("faqsData")
fun RecyclerView.bindRvFaq(data: List<Topic>?) {
    val adapter = adapter as TopicAdapter
    adapter.submitList(data)
}

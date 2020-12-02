package id.forum.faq.presentation

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.forum.faq.databinding.ItemListFaqLayoutBinding
import id.forum.core.faq.domain.model.Faq


class FaqViewHolder(
    private val binding: ItemListFaqLayoutBinding,
    private val faqListener: FaqAdapter.FaqAdapterListener
) : ViewHolder(binding.root) {

    init {
        binding.run {
            listener = faqListener
        }
    }

    fun bind(faq: Faq) {
        binding.apply {
            this.faq = faq
            contentTextView.setOnClickListener {
                faqListener.onContentClicked(faq)
            }
        }


    }
}
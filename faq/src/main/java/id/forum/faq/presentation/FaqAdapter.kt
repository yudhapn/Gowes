package id.forum.faq.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import id.forum.core.faq.domain.model.Faq
import id.forum.core.faq.domain.model.FaqDiffCallback
import id.forum.faq.databinding.ItemListFaqLayoutBinding

class FaqAdapter(
    private val listener: FaqAdapterListener
) : ListAdapter<Faq, FaqViewHolder>(FaqDiffCallback) {

    interface FaqAdapterListener {
        fun onContentClicked(faq: Faq)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder =
        FaqViewHolder(
            ItemListFaqLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener
        )

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) =
        holder.bind(getItem(position))
}

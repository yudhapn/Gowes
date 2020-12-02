package id.forum.core.base

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.forum.core.R
import id.forum.core.databinding.MonthStateHeaderViewItemBinding

class MonthStateViewHolder(
    private val binding: MonthStateHeaderViewItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(counter: Int) {
        Log.d("MonthStateViewHolder", "message: $counter MONTH")
        binding.monthMsg.text = if (counter == 0) "THIS MONTH" else "$counter MONTH AGO"
    }

    companion object {
        fun create(parent: ViewGroup): MonthStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.month_state_header_view_item, parent, false)
            val binding = MonthStateHeaderViewItemBinding.bind(view)
            return MonthStateViewHolder(binding)
        }
    }
}

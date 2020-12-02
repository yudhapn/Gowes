package id.forum.post.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import id.forum.post.databinding.ItemListSpinnerPostLayoutBinding

class SpinnerAdapter(
   private val ctx: Context,
   private val texts: List<String>,
   private val images: List<String>
) : BaseAdapter() {

   override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
      val text = texts[position]
      val image = images[position]
      val binding =
         ItemListSpinnerPostLayoutBinding.inflate(LayoutInflater.from(ctx), parent, false)
      binding.apply {
         this.text = text
         this.image = image
         executePendingBindings()
      }
      return binding.root
   }

   override fun getItem(position: Int): Any = texts[position]

   override fun getItemId(position: Int): Long = 0

   override fun getCount() = texts.size
}
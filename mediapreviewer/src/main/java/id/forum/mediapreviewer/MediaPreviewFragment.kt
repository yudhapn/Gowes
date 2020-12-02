package id.forum.mediapreviewer

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.igreenwood.loupe.Loupe
import id.forum.core.post.domain.model.Attachment
import id.forum.core.post.domain.model.AttachmentList
import id.forum.gowes.R
import id.forum.mediapreviewer.databinding.FragmentMediaPreviewBinding
import id.forum.mediapreviewer.databinding.ItemImagePreviewBinding


class MediaPreviewFragment : Fragment() {
    private lateinit var binding: FragmentMediaPreviewBinding
    private var initialPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: MediaPreviewFragmentArgs by navArgs()
        initialPosition = args.initialPosition
        val attachments = args.attachments
        Log.d("imagepreview", "attachments size: ${attachments.attachments.size}")
        initViewPager(initialPosition, attachments)
    }

    private fun initViewPager(initalPosition: Int, attachmentList: AttachmentList) {
        val adapter = ImageAdapter(requireContext(), attachmentList.attachments)
        binding.apply {
            viewpager.adapter = adapter
            viewpager.currentItem = initalPosition
        }
    }

    inner class ImageAdapter(var context: Context, var attachments: List<Attachment>) :
        PagerAdapter() {

        private var loupeMap = hashMapOf<Int, Loupe>()
        private var views = hashMapOf<Int, ImageView>()

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val binding = ItemImagePreviewBinding.inflate(LayoutInflater.from(context))
            container.addView(binding.root)
            loadImage(binding.imageView, binding.container, position)
            views[position] = binding.imageView
            return binding.root
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun getCount() = attachments.size

        private fun loadImage(image: ImageView, container: ViewGroup, position: Int) {
            if (Pref.useSharedElements) {
                // shared elements
                Glide.with(image.context)
                    .load(attachments[position].url)
                    .onlyRetrieveFromCache(true)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            startPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            image.transitionName =
                                context.getString(R.string.shared_image_transition, position)
                            val loupe = Loupe(image, container).apply {
                                useFlingToDismissGesture = !Pref.useSharedElements
                                maxZoom = Pref.maxZoom
                                flingAnimationDuration = Pref.flingAnimationDuration
                                scaleAnimationDuration = Pref.scaleAnimationDuration
                                overScaleAnimationDuration = Pref.overScaleAnimationDuration
                                overScrollAnimationDuration = Pref.overScrollAnimationDuration
                                dismissAnimationDuration = Pref.dismissAnimationDuration
                                restoreAnimationDuration = Pref.restoreAnimationDuration
                                viewDragFriction = Pref.viewDragFriction

                                setOnViewTranslateListener(
                                    onStart = { },
                                    onRestore = { },
                                    onDismiss = { it.findNavController().navigateUp() }
                                )
                            }

                            loupeMap[position] = loupe

                            if (position == initialPosition) {
                                setEnterSharedElementCallback(object : SharedElementCallback() {
                                    override fun onMapSharedElements(
                                        names: MutableList<String>?,
                                        sharedElements: MutableMap<String, View>?
                                    ) {
                                        names ?: return
                                        val view = views[binding.viewpager.currentItem] ?: return
                                        val currentPosition: Int = binding.viewpager.currentItem
                                        view.transitionName = context.getString(
                                            R.string.shared_image_transition,
                                            currentPosition
                                        )
                                        sharedElements?.clear()
                                        sharedElements?.put(view.transitionName, view)
                                    }
                                })
                                startPostponedEnterTransition()
                            }
                            return false
                        }

                    })
                    .into(image)
            } else {
                // swipe to dismiss
                Glide.with(image.context).load(attachments[position].url)
                    .onlyRetrieveFromCache(true)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            startPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            val loupe = Loupe(image, container).apply {
                                useFlingToDismissGesture = !Pref.useSharedElements
                                maxZoom = Pref.maxZoom
                                flingAnimationDuration = Pref.flingAnimationDuration
                                scaleAnimationDuration = Pref.scaleAnimationDuration
                                overScaleAnimationDuration = Pref.overScaleAnimationDuration
                                overScrollAnimationDuration = Pref.overScrollAnimationDuration
                                dismissAnimationDuration = Pref.dismissAnimationDuration
                                restoreAnimationDuration = Pref.restoreAnimationDuration
                                viewDragFriction = Pref.viewDragFriction

                                setOnViewTranslateListener(
                                    onStart = { },
                                    onRestore = { },
                                    onDismiss = { it.findNavController().navigateUp() }
                                )
                            }

                            loupeMap[position] = loupe
                            if (position == initialPosition) {
                                startPostponedEnterTransition()
                            }
                            return false
                        }

                    }).into(image)
            }
        }

    }
}

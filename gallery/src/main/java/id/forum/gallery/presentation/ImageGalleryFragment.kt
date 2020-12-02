package id.forum.gallery.presentation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.forum.core.media.domain.model.Image
import id.forum.core.media.domain.model.MediaList
import id.forum.core.util.VH_TYPE_GALLERY
import id.forum.gallery.databinding.FragmentImageGalleryBinding
import id.forum.gallery.injectFeature
import id.forum.gallery.presentation.GalleryAdapter.GalleryAdapterListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ImageGalleryFragment : Fragment(), GalleryAdapterListener {
    private val TAG = ImageGalleryFragment::class.java.simpleName
    private lateinit var binding: FragmentImageGalleryBinding
    private val args: ImageGalleryFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageGalleryBinding.inflate(inflater, container, false)
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            val viewModel: ImageGalleryViewModel by viewModel { parametersOf(args.mediaList) }
            lifecycleOwner = this@ImageGalleryFragment
            this.viewModel = viewModel
            initRecyclerView(this, viewModel)
            viewModel.setMediaList(args.mediaList)
        }
        return binding.root
    }

    private fun initRecyclerView(
        binding: FragmentImageGalleryBinding,
        galleryViewModel: ImageGalleryViewModel
    ) {
        binding.apply {
            saveButton.setOnClickListener {
                val action = ImageGalleryFragmentDirections.actionToCreatePost(
                    MediaList(galleryViewModel.mediaListSelected)
                )
                findNavController().navigate(action)
            }
            rvThreadImage.apply {
                setHasFixedSize(true)
                adapter = GalleryAdapter(this@ImageGalleryFragment, VH_TYPE_GALLERY)
            }
        }
    }

    override fun onImageClicked(cardView: View, media: Image) {
        binding.viewModel?.updateImages(media)
    }

    override fun onRemoveClicked(cardView: View, media: Image) = TODO()
}

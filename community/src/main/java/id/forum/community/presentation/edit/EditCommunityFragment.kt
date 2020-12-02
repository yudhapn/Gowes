package id.forum.community.presentation.edit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import id.forum.community.databinding.FragmentEditCommunityBinding
import id.forum.core.R
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.data.Status.ERROR
import id.forum.core.data.Status.SUCCESS
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditCommunityFragment : Fragment() {
    private lateinit var binding: FragmentEditCommunityBinding
    private var imageUri: Uri? = null
    private val viewModel: EditCommunityViewModel by viewModel()
    private val accountViewModel: UserAccountViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: EditCommunityFragmentArgs by navArgs()
        binding.apply {
            community = args.community
            switchPrivate.setOnCheckedChangeListener { _, isPrivate ->
                tilQuestion.visibility = if (isPrivate) VISIBLE else GONE
            }
            btnSave.button.setOnClickListener {
                val newName = etName.text.toString()
                val newBio = etBio.text.toString()
                val newProvince = etProvince.text.toString()
                val newCity = etCity.text.toString()
                viewModel.updateUser(
                    community = args.community,
                    newName = newName,
                    newBio = newBio,
                    newProvince = newProvince,
                    newCity = newCity,
                    isPrivate = switchPrivate.isChecked,
                    imageUri = imageUri
                )
                    .observe(viewLifecycleOwner, Observer {
                        responseType = it
                        when (it.status) {
                            SUCCESS -> {
                                accountViewModel.showSnackBar(getString(R.string.community_profile_has_been_updated))
                                findNavController().navigate(
                                    EditCommunityFragmentDirections.actionToCommunity(
                                        it.data ?: args.community
                                    )
                                )
                            }
                            ERROR -> accountViewModel.showSnackBar(it.message.toString())
                            else -> Unit
                        }
                    })

            }
            btnChoose.setOnClickListener {
                startImageCropper()
            }
            navigationIcon.setOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun startImageCropper() =
        CropImage.activity().setOutputCompressQuality(20).start(requireContext(), this)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                imageUri = result.uri
                Glide.with(requireContext()).load(imageUri).circleCrop()
                    .into(binding.ivCommunity)
                binding.ivCommunity.setImageURI(imageUri)
            }
        }
    }
}

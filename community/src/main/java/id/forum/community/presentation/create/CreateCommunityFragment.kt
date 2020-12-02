package id.forum.community.presentation.create

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import id.forum.community.databinding.FragmentCreateCommunityBinding
import id.forum.community.injectFeature
import id.forum.community.presentation.SpinnerAdapter
import id.forum.core.R
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.data.Status.ERROR
import id.forum.core.data.Status.SUCCESS
import id.forum.core.util.isTextValid
import id.forum.core.util.setErrorMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class CreateCommunityFragment : Fragment() {
    private lateinit var binding: FragmentCreateCommunityBinding
    private var imageUri: Uri? = null
    private val viewModel: CreateCommunityViewModel by viewModel()
    private val accountViewModel: UserAccountViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewListener()
    }

    private fun setupViewListener() {
        binding.apply {
            val communityTypes = resources.getStringArray(R.array.community_types)
            val communityTypesImage = resources.getStringArray(R.array.community_types_image)
            typeSpinner.adapter = SpinnerAdapter(
                requireContext(),
                communityTypes.map { it },
                communityTypesImage.map { it }
            )

            btnSave.button.setOnClickListener {
                val name = etName.text.toString()
                val type = typeSpinner.selectedItem.toString()
                val bio = etBio.text.toString()
                val province = etProvince.text.toString()
                val city = etCity.text.toString()
                if (validateInput(name, province, city)) {
                    viewModel.createCommunity(name, type, bio, province, city, imageUri)
                        .observe(viewLifecycleOwner, Observer {
                            responseType = it
                            when (it.status) {
                                SUCCESS -> {
                                    accountViewModel.showSnackBar(getString(R.string.waiting_for_admin_approval))
                                    findNavController().navigate(
                                        CreateCommunityFragmentDirections.actionToHome()
                                    )
                                }
                                ERROR -> accountViewModel.showSnackBar(it.message.toString())
                                else -> Unit
                            }
                        })
                }
            }
            btnChoose.setOnClickListener {
                startImageCropper()
            }
            etName.doOnTextChanged { text, _, _, _ ->
                if (text.toString().isNotBlank()) {
                    tilName.error = null
                }
            }
            etProvince.doOnTextChanged { text, _, _, _ ->
                if (text.toString().isNotBlank()) {
                    tilProvince.error = null
                }
            }
            etCity.doOnTextChanged { text, _, _, _ ->
                if (text.toString().isNotBlank()) {
                    tilCity.error = null
                }
            }
        }
    }

    private fun validateInput(name: String, province: String, city: String): Boolean {
        binding.apply {
            val nameValid = name.isTextValid()
                .also { valid ->
                    tilName.setErrorMessage(if (!valid) getString(R.string.error_community_name) else null)
                }
            val provinceValid = province.isTextValid()
                .also { valid ->
                    tilProvince.setErrorMessage(if (!valid) getString(R.string.error_community_province) else null)
                }
            val cityValid = city.isTextValid()
                .also { valid ->
                    tilCity.setErrorMessage(if (!valid) getString(R.string.error_community_city) else null)
                }
            return nameValid && provinceValid && cityValid
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

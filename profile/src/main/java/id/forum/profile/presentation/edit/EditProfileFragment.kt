package id.forum.profile.presentation.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import id.forum.core.R
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.data.Resource
import id.forum.core.data.Status
import id.forum.core.data.Status.SUCCESS
import id.forum.core.user.domain.model.User
import id.forum.core.util.SHARED_TKN
import id.forum.core.util.SHARED_USR
import id.forum.gowes.MainActivity
import id.forum.profile.databinding.FragmentEditProfileBinding
import kotlinx.android.synthetic.main.profile_progress_button_layout.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private var imageUri: Uri? = null
    private val viewModel: EditProfileViewModel by viewModel()
    private val accountViewModel: UserAccountViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: EditProfileFragmentArgs by navArgs()
        binding.apply {
            user = args.user
            saveButton.button.setOnClickListener {
                val newName = etName.text.toString()
                val newBio = etBio.text.toString()
                viewModel.updateUser(newName, newBio, imageUri)
                    .observe(viewLifecycleOwner, Observer {
                        responseType = it
                        when (it.status) {
                            SUCCESS -> {
                                bindUserData(it)
                                accountViewModel.showSnackBar(getString(R.string.profile_has_been_updated))
                                findNavController().navigate(
                                    EditProfileFragmentDirections.actionToProfile(
                                        it.data ?: args.user
                                    )
                                )
                            }
                            Status.ERROR -> accountViewModel.showSnackBar(it.message.toString())
                            else -> Unit
                        }
                    })

            }
            btnChoose.setOnClickListener {
                startImageCropper()
            }
        }
    }

    private fun bindUserData(userData: Resource<User>) {
        val userJson = Gson().toJson(userData.data)
        requireContext().getSharedPreferences(SHARED_TKN, Context.MODE_PRIVATE).edit {
            putString(SHARED_USR, userJson)
        }
        (requireActivity() as MainActivity).setAccountAvatar()
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
                    .into(binding.ivUser)
                binding.ivUser.setImageURI(imageUri)
            }
        }
    }
}

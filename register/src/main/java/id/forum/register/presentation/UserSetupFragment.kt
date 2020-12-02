package id.forum.register.presentation

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.data.Resource
import id.forum.core.data.Status.SUCCESS
import id.forum.core.data.Token
import id.forum.core.user.domain.model.User
import id.forum.core.util.SHARED_TKN
import id.forum.core.util.SHARED_USR
import id.forum.core.vo.Profile
import id.forum.register.databinding.FragmentUserSetupBinding
import kotlinx.android.synthetic.main.register_progress_button_layout.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserSetupFragment : Fragment() {
    private lateinit var binding: FragmentUserSetupBinding
    private var imageUri: Uri? = null
    private val accountViewModel: UserAccountViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: RegisterViewModel by viewModel()
        binding.apply {
            saveButton.button.setOnClickListener {
                val sharedPref = requireContext().getSharedPreferences(
                    SHARED_TKN, Context.MODE_PRIVATE
                )
                val tknJson = sharedPref.getString(SHARED_TKN, "")
                val token = Gson().fromJson(tknJson, Token::class.java)
                val name = etName.text.toString()
                val username = etUsername.text.toString()
                viewModel.updateUser(
                    User(
                        "",
                        accountId = token.userId,
                        profile = Profile(name = name),
                        userName = username
                    ), imageUri
                )
                    .observe(viewLifecycleOwner, Observer {
                        responseType = it
                        when (it.status) {
                            SUCCESS -> {
                                bindUserData(it)
                                val action = UserSetupFragmentDirections.actionToHome()
                                findNavController().navigate(action)
                            }
                            else -> Unit
                        }
                    })
            }
            chooseButton.setOnClickListener {
                startImageCropper()
            }
        }
    }

    private fun bindUserData(userData: Resource<User>) {
        userData.data?.let {
            Log.d("SetupFragment", "user: $it")
            accountViewModel.updateCurrentUser(it)
        }
        val userJson = Gson().toJson(userData.data)
        Log.d("SetupFragment", "user: $userJson")
        requireContext().getSharedPreferences(SHARED_TKN, Context.MODE_PRIVATE).edit {
            putString(SHARED_USR, userJson)
        }
    }

    private fun startImageCropper() =
        CropImage.activity().setOutputCompressQuality(20).start(requireContext(), this)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                Glide.with(requireContext()).load(imageUri).circleCrop()
                    .into(binding.userProfileImageView)
                binding.userProfileImageView.setImageURI(imageUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}

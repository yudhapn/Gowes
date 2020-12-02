package id.forum.login.presentation.reset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import id.forum.core.R
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.data.Status.ERROR
import id.forum.core.data.Status.SUCCESS
import id.forum.core.util.*
import id.forum.login.databinding.FragmentPasswordResetBinding
import id.forum.login.injectFeature
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class PasswordResetFragment : Fragment() {
    private lateinit var binding: FragmentPasswordResetBinding
    private val viewModel: PasswordResetViewModel by viewModel()
    private val accountViewModel: UserAccountViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordResetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewListener()
    }

    private fun setupViewListener() {
        binding.apply {
            etPassword.setOnEditorActionListener { _, actionId, _ ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleLogin()
                    handled = true
                }
                handled
            }
            btnLogin.button.setOnClickListener { handleLogin() }
            etEmail.setErrorStateTextInput(tilEmail)
            etPassword.setErrorStateTextInput(tilPassword)
        }
    }

    private fun handleLogin() {
        binding.apply {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (validateInput(email, password)) {
                viewModel.resetPassword(email, password).observe(viewLifecycleOwner, Observer {
                    responseType = it
                    when (it.status) {
                        SUCCESS -> {
                            findNavController().navigate(PasswordResetFragmentDirections.actionToLogin())
                        }
                        ERROR -> accountViewModel.showSnackBar(it.message.toString())
                        else -> Unit
                    }
                })
            }
        }
        requireContext().hideSoftKeyboard(requireActivity().currentFocus?.windowToken)
    }

    private fun validateInput(email: String, password: String): Boolean {
        binding.apply {
            val emailValid = email.isValidEmail()
                .also { valid ->
                    tilEmail.setErrorMessage(if (!valid) getString(R.string.error_email) else null)
                }
            val passwordValid = password.isTextValid()
                .also { valid ->
                    tilPassword.setErrorMessage(if (!valid) getString(R.string.error_password) else null)
                }
            return emailValid && passwordValid
        }
    }
}

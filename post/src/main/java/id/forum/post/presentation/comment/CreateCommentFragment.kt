package id.forum.post.presentation.comment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.data.Status
import id.forum.core.data.Status.ERROR
import id.forum.core.data.Status.SUCCESS
import id.forum.core.util.POST_BODY
import id.forum.core.util.SHARED_TKN
import id.forum.core.util.hideSoftKeyboard
import id.forum.core.util.showSoftKeyboard
import id.forum.post.databinding.FragmentCreateCommentBinding
import id.forum.post.presentation.create.CreatePostFragmentDirections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class CreateCommentFragment : Fragment() {
    private lateinit var binding: FragmentCreateCommentBinding

    private val args: CreateCommentFragmentArgs by navArgs()
    private val viewModel: CreateCommentViewModel by viewModel()
    private val accountViewModel: UserAccountViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewListener()
    }

    private fun setupViewListener() {
        binding.apply {
            etContent.requestFocus()
            requireContext().showSoftKeyboard(etContent)
            etContent.addTextChangedListener {
                etContent.isEnabled = it.toString().isNotBlank()
            }
            btnClose.setOnClickListener { findNavController().navigateUp() }
            btnSend.setOnClickListener {
                requireContext().hideSoftKeyboard(requireActivity().currentFocus?.windowToken)
                val post = args.post
                val body = etContent.text.toString()
                viewModel.insertCommentsPost(post.id, body).observe(viewLifecycleOwner, Observer {
                    accountViewModel.setCurrentState(it.status)
                    when (it.status) {
                        SUCCESS -> {
                            findNavController().navigate(CreateCommentFragmentDirections.actionToPost(post))
                        }
                        ERROR -> {
                            accountViewModel.showSnackBar(it.message.toString())
                        }
                        else -> Unit
                    }
                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().hideSoftKeyboard(requireActivity().currentFocus?.windowToken)
    }
}

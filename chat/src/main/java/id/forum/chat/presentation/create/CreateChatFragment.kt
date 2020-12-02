package id.forum.chat.presentation.create

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.forum.chat.databinding.FragmentCreateChatBinding
import id.forum.chat.presentation.create.UserChatListAdapter.UserChatAdapterListener
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class CreateChatFragment : Fragment(), UserChatAdapterListener {
    private lateinit var binding: FragmentCreateChatBinding
    private val viewModel: CreateChatViewModel by viewModel()
    private val userChatAdapter =
        UserChatListAdapter(this@CreateChatFragment)

    override fun onCreateView(
       inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = this@CreateChatFragment
            this.createChatViewModel = viewModel
            recyclerView.adapter = userChatAdapter
            refreshLayout.setOnRefreshListener { viewModel.refreshMembers() }
            searchEditText.addTextChangedListener(object : TextWatcher {
               override fun afterTextChanged(p0: Editable?) = Unit
               override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
               override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) =
                  viewModel.searchMember(p0.toString())
            })
        }
    }

    override fun onUserClicked(user: User) {
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(
               requireActivity().currentFocus?.windowToken,
               InputMethodManager.HIDE_NOT_ALWAYS
            )
        findNavController().navigate(
           CreateChatFragmentDirections.actionToChatRoom(
              "new_chat", user, viewModel.currentUser.id
           )
        )
    }


}

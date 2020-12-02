package id.forum.chat.presentation.room


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.forum.chat.databinding.FragmentChatRoomBinding
import id.forum.chat.injectFeature
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.data.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


@ExperimentalCoroutinesApi
class ChatRoomFragment : Fragment() {
    private lateinit var binding: FragmentChatRoomBinding
    private val args: ChatRoomFragmentArgs by navArgs()
    private val accountViewModel: UserAccountViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            initRecyclerViewAndListner()
            lifecycleOwner = viewLifecycleOwner
        }
    }

    private fun initRecyclerViewAndListner() {
        val chatId = args.chatId
        val receiverArgs = args.receiver
        val currentUserId = args.userId
        val roomViewModel: ChatRoomViewModel by viewModel { parametersOf(chatId, receiverArgs) }
        val chatRoomAdapter =
            ChatRoomAdapter(currentUserId, receiverArgs.profile.avatar)
        binding.apply {
            receiver = receiverArgs
            viewModel = roomViewModel

            refreshLayout.setOnRefreshListener {
                roomViewModel.refreshMessages()
            }
            roomViewModel.messages.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> refreshLayout.isRefreshing = false
                    Status.ERROR -> {
                        refreshLayout.isRefreshing = false
                        accountViewModel.showSnackBar(it.message.toString())
                    }

                    else -> refreshLayout.isRefreshing = true
                }
            })
            rvChatRoom.apply {
                setHasFixedSize(true)
                adapter = chatRoomAdapter
            }

            btnSend.setOnClickListener {
                val content = etText.text.toString()
                roomViewModel.sendMessage(content, currentUserId)
                rvChatRoom.layoutManager?.smoothScrollToPosition(
                    rvChatRoom, null, chatRoomAdapter.itemCount
                )
                etText.setText("")
            }
            backButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

}

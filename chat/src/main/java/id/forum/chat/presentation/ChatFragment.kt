package id.forum.chat.presentation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import id.forum.chat.databinding.FragmentChatBinding
import id.forum.chat.injectFeature
import id.forum.gowes.MainActivity
import id.forum.gowes.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            this.chatViewModel = viewModel
            setupFab()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.refreshLayout.setOnRefreshListener { viewModel.refreshChats() }
        initChatsRecyclerView()
    }

    private fun setupFab() {
        (requireActivity() as MainActivity).findViewById<FloatingActionButton>(R.id.fab)
            .setOnClickListener {
                findNavController().navigate(ChatFragmentDirections.actionGlobalCreateChat())
            }
    }


    private fun initChatsRecyclerView() {
        val args: ChatFragmentArgs by navArgs()
        binding.rvChat.apply {
            setHasFixedSize(true)
            adapter = ChatAdapter(ChatListener { _, chat, receiver ->
                val action =
                    ChatFragmentDirections.actionToChatRoom(chat.id, receiver, args.user.id)
                findNavController().navigate(action)
            }, args.user.id)
        }
    }
}

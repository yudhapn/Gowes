package id.forum.faq.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import id.forum.faq.databinding.FragmentContentBinding
import id.forum.faq.databinding.FragmentTopicBinding
import id.forum.faq.injectFeature
import id.forum.post.presentation.PostFragmentArgs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class ContentFragment : Fragment() {
    private lateinit var binding: FragmentContentBinding
    private val args: ContentFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContentBinding.inflate(inflater, container, false).apply {
            this.faq = args.faq
        }
        return binding.root
    }
}
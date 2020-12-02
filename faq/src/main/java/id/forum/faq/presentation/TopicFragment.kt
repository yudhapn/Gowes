package id.forum.faq.presentation

import android.annotation.SuppressLint
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.data.Status
import id.forum.core.faq.domain.model.Faq
import id.forum.core.faq.domain.model.Topic
import id.forum.faq.R
import id.forum.faq.databinding.FragmentTopicBinding
import id.forum.faq.injectFeature
import id.forum.faq.presentation.FaqAdapter.FaqAdapterListener
import id.forum.faq.presentation.TopicAdapter.TopicAdapterListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class TopicFragment : Fragment(), TopicAdapterListener, FaqAdapterListener {
    private lateinit var binding: FragmentTopicBinding
    private val _viewModel: TopicViewModel by viewModel()
    private val accountViewModel: UserAccountViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopicBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@TopicFragment
            this.viewModel = _viewModel
        }
        setupRecyclerViewAdapter()
        updateUi()
        return binding.root
    }

    private fun setupRecyclerViewAdapter() {
        binding.recyclerView.apply {
            setItemViewCacheSize(20)
            adapter = TopicAdapter(this@TopicFragment, this@TopicFragment)
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun updateUi() {
        binding.apply {
            _viewModel.topics.observe(viewLifecycleOwner, Observer {
                accountViewModel.setCurrentState(it.status)
                when (it.status) {
                    Status.ERROR -> accountViewModel.showSnackBar(it.message.toString())
                    else -> Unit
                }
            })
        }
    }

    override fun onTopicClicked(imageView: ImageView, contentLayout: View, topic: Topic) {
        topic.isExpand = !topic.isExpand
        val animatedDrawable = imageView.drawable as AnimatedVectorDrawable
        animatedDrawable.registerAnimationCallback(object :
            Animatable2.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                super.onAnimationEnd(drawable)
                animatedDrawable.clearAnimationCallbacks()
                imageView.setImageResource(
                    if (topic.isExpand) {
                        R.drawable.ic_animated_arrow_up
                    } else {
                        R.drawable.ic_animated_arrow_down
                    }
                )
                showContent(topic.isExpand, contentLayout)
            }
        })
        animatedDrawable.start()

    }

    private fun showContent(isExpand: Boolean, contentLayout: View) {
        if (isExpand) {
            contentLayout.visibility = View.VISIBLE
        } else {
            contentLayout.visibility = View.GONE
        }

    }

    override fun onContentClicked(faq: Faq) {
        val action = TopicFragmentDirections.actionToContent(faq)
        findNavController().navigate(action)
    }
}
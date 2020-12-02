package id.forum.search.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.data.Status
import id.forum.core.util.hideSoftKeyboard
import id.forum.core.util.showSoftKeyboard
import id.forum.search.databinding.FragmentSearchBinding
import id.forum.search.injectFeature
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalCoroutinesApi
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: SearchViewPagerAdapter
    private val accountViewModel: UserAccountViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabLayout()
        setupListener()
    }

    private fun setupListener() {
        binding.apply {
            keywordEditText.requestFocus()
            requireContext().showSoftKeyboard(keywordEditText)
            keywordEditText.setOnEditorActionListener { _, actionId, _ ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(keywordEditText.text.toString())
                    handled = true
                }
                requireContext().hideSoftKeyboard(requireActivity().currentFocus?.windowToken)
                handled
            }
            keywordEditText.addTextChangedListener {
                closeImageView.visibility = if (it.toString().isNotBlank()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
            backImageView.setOnClickListener { findNavController().navigateUp() }
            closeImageView.setOnClickListener { keywordEditText.setText("") }
        }
    }

    private fun setupTabLayout() {
        binding.apply {
            adapter = SearchViewPagerAdapter(childFragmentManager, requireContext())
            viewPager.adapter = adapter
            tabLayout.setupWithViewPager(viewPager)
        }
    }

    private fun search(keyword: String) {
        accountViewModel.setSearchKeyword(keyword)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().hideSoftKeyboard(requireActivity().currentFocus?.windowToken)
        search("")
        accountViewModel.setCurrentState(Status.SUCCESS)
    }
}

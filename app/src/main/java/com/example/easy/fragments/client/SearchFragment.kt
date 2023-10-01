package com.example.easy.fragments.client

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.easy.R
import com.example.easy.adapters.JobsInfoAdapter
import com.example.easy.databinding.FragmentSearchBinding
import com.example.easy.utils.Resource
import com.example.easy.viewmodels.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val searchViewModel by viewModels<SearchViewModel>()
    val jobsInfoAdapter: JobsInfoAdapter by lazy { JobsInfoAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupJobsInfoAdapter()
        hideLoading()
        jobsInfoAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("jobInfo", it) }
            findNavController().navigate(
                R.id.action_searchFragment_to_detailsJobInfoFragment2,
                bundle
            )
        }
        binding.edSearch.doOnTextChanged { text, _, _, _ ->
            val search = binding.edSearch.text.toString()
            searchViewModel.fetchSearchedJobs(search)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.searchedJobs.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoading()
                        }

                        is Resource.Success -> {
                            Log.d("debuggingSearch", resource.data.toString())
                            jobsInfoAdapter.differ.submitList(resource.data)
                            hideLoading()
                        }

                        is Resource.Failed -> {
                            Log.d("debugging", resource.message.toString())
                            Snackbar.make(view, resource.message.toString(), Snackbar.LENGTH_LONG)
                                .show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE

    }

    fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun setupJobsInfoAdapter() {
        binding.rvSearch.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = jobsInfoAdapter
        }
    }


}
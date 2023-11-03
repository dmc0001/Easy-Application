package com.example.easy.fragments.employer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.easy.adapters.JobsInfoOrdersAdapter
import com.example.easy.databinding.FragmentHome2Binding
import com.example.easy.utils.ItemSpacingDecoration
import com.example.easy.utils.Resource
import com.example.easy.viewmodels.Home2ViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    lateinit var binding: FragmentHome2Binding
    private val mainCategoryViewModel by viewModels<Home2ViewModel>()
    private lateinit var jobsInfoAdapter: JobsInfoOrdersAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHome2Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainCategoryViewModel.getAllOrders()
        setupJobsInfoAdapter()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainCategoryViewModel.orderJobs.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoading()
                        }

                        is Resource.Success -> {
                            Log.d("debugging", resource.data.toString())
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

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE

    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun setupJobsInfoAdapter() {
        jobsInfoAdapter = JobsInfoOrdersAdapter()
        binding.rvOrdersToday.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = jobsInfoAdapter
        }
        binding.rvOrdersToday.addItemDecoration(ItemSpacingDecoration(16))
    }


}
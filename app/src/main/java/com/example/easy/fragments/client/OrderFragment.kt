package com.example.easy.fragments.client

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easy.R
import com.example.easy.adapters.JobsInfoOrdersAdapter
import com.example.easy.databinding.FragmentOrderBinding
import com.example.easy.utils.ItemSpacingDecoration
import com.example.easy.utils.Resource
import com.example.easy.utils.showBottomNav
import com.example.easy.viewmodels.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var jobsInfoOrdersAdapter : JobsInfoOrdersAdapter
    private val viewModel by viewModels<OrderViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOrderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupJobsInfoOrdersAdapter()

        jobsInfoOrdersAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("order",it) }
            findNavController().navigate(R.id.action_orderFragment_to_editOrderFragment,bundle)

        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.orderJobs.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoading()
                        }

                        is Resource.Success -> {
                            Log.d("debugging", resource.data.toString())

                            jobsInfoOrdersAdapter.differ.submitList(resource.data)
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

    private fun setupJobsInfoOrdersAdapter() {
         jobsInfoOrdersAdapter = JobsInfoOrdersAdapter()
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = jobsInfoOrdersAdapter
        }
        binding.rvOrders.addItemDecoration(ItemSpacingDecoration(16))
    }
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE

    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }
    override fun onResume() {
        super.onResume()
        showBottomNav()
    }
}
package com.example.easy.fragments.client.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.easy.R
import com.example.easy.adapters.JobsInfoAdapter
import com.example.easy.databinding.FragmentMainCategoryBinding
import com.example.easy.utils.ItemSpacingDecoration
import com.example.easy.utils.Resource
import com.example.easy.viewmodels.MainCategoryViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCategoryFragment : Fragment() {
    lateinit var binding: FragmentMainCategoryBinding
    private lateinit var jobsInfoAdapter: JobsInfoAdapter
    private val mainCategoryViewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupJobsInfoAdapter()

        jobsInfoAdapter.onClick = {
            val b = Bundle().apply { putParcelable("jobInfo", it)
                putParcelable("user", it)

            }
            findNavController().navigate(R.id.action_homeFragment_to_detailsJobInfoFragment2, b)
        }



        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainCategoryViewModel.specialJobs.collectLatest { resource ->
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
        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                mainCategoryViewModel.fetchJobsInfo()
            }
        })

    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE

    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun setupJobsInfoAdapter() {
        jobsInfoAdapter = JobsInfoAdapter()
        binding.rvJobsInfo.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = jobsInfoAdapter
        }
        binding.rvJobsInfo.addItemDecoration(ItemSpacingDecoration(16))
    }
}
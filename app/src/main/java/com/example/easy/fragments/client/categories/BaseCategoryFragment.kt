package com.example.easy.fragments.client.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easy.R
import com.example.easy.adapters.JobsInfoAdapter
import com.example.easy.databinding.FragmentBaseCategoryBinding
import com.example.easy.fragments.loginRegister.RegisterFragmentDirections


open class BaseCategoryFragment : Fragment() {
    protected val jobsInfoAdapter: JobsInfoAdapter by lazy { JobsInfoAdapter() }
    private lateinit var binding: FragmentBaseCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBaseCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupJobsInfoAdapter()

        jobsInfoAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("jobInfo",it) }
            findNavController().navigate(R.id.action_homeFragment_to_detailsJobInfoFragment2,bundle)
        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                onJobsInfoPagingRequest()
            }
        })

    }

    protected fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE

    }

    protected fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    open fun onJobsInfoPagingRequest() {

    }

    private fun setupJobsInfoAdapter() {
        binding.rvJobsInfo.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = jobsInfoAdapter
        }
    }


}
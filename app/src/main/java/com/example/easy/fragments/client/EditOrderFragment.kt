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
import androidx.navigation.fragment.navArgs
import com.example.easy.R
import com.example.easy.data.Order
import com.example.easy.databinding.FragmentEditOrderBinding
import com.example.easy.utils.Resource
import com.example.easy.utils.hideBottomNav
import com.example.easy.viewmodels.JobDetailsViewModel
import com.example.easy.viewmodels.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditOrderFragment : Fragment() {
    lateinit var binding: FragmentEditOrderBinding
    private val orderViewModel by viewModels<OrderViewModel>()
    private val jobDetailsViewModel by viewModels<JobDetailsViewModel>()
    private val args by navArgs<EditOrderFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditOrderBinding.inflate(layoutInflater)
        hideBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderViewModel.getOrder(args.order.jobInformationUid)

        binding.apply {

            btnEdit.setOnClickListener {
                val description = edDescription.text.toString()
                val date = edDate.text.toString()
                val location = edLocation.text.toString()

                val order = Order(
                    args.order.jobTitle,
                    args.order.jobInformationUid,
                    description,
                    date,
                    location
                )
                jobDetailsViewModel.addOrder(order)
            }
            btnCheck.setOnClickListener {
                orderViewModel.goToJobOrdered(args.order.jobInformationUid)
            }
            imgClose.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.orderUser.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoading()
                        }

                        is Resource.Success -> {
                            Log.d("debugging", resource.data.toString())
                            showUserOrder(resource.data!!)
                            hideLoading()
                        }

                        is Resource.Failed -> {
                            hideLoading()
                        }

                        else -> Unit
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.go.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoading()
                        }

                        is Resource.Success -> {
                            hideLoading()
                            val bundle = Bundle().apply { putParcelable("jobInfo", resource.data) }
                            findNavController().navigate(R.id.action_editOrderFragment_to_detailsJobInfoFragment2,bundle)
                        }

                        is Resource.Failed -> {
                            hideLoading()
                        }

                        else -> Unit
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                jobDetailsViewModel.updatedOrder.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.btnEdit.startAnimation()
                        }

                        is Resource.Success -> {
                            Log.d("debugging", resource.data.toString())
                            Snackbar.make(view, resource.message.toString(), Snackbar.LENGTH_LONG)
                                .show()
                            binding.btnEdit.revertAnimation()
                        }

                        is Resource.Failed -> {
                            Log.d("debugging", resource.message.toString())
                            binding.btnEdit.revertAnimation()
                            Snackbar.make(view, resource.message.toString(), Snackbar.LENGTH_LONG)
                                .show()
                        }

                        else -> Unit
                    }
                }
            }
        }

    }

    private fun showUserOrder(data: Order) {
        binding.apply {
            edDate.setText(data.date)
            edLocation.setText(data.location)
            edDescription.setText(data.description)

        }
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE
    }

}
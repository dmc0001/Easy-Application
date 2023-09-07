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
import androidx.navigation.fragment.navArgs
import com.example.easy.R
import com.example.easy.adapters.ViewPager2Images
import com.example.easy.databinding.FragmentDetailsJobInfoBinding
import com.example.easy.utils.Resource
import com.example.easy.utils.hideBottomNav
import com.example.easy.viewmodels.JobDetailsViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsJobInfoFragment : Fragment() {
    lateinit var binding: FragmentDetailsJobInfoBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val args by navArgs<DetailsJobInfoFragmentArgs>()
    val viewmodel by viewModels<JobDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        /* val bottomNavigationView = (activity as ClientActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
         bottomNavigationView.visibility = View.GONE*/
        hideBottomNav()

        binding = FragmentDetailsJobInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPagerRv()
        viewmodel.fetchEmployerInfo(args.jobInfo.uid.toString())

        binding.apply {
            tvJobTitle.text = args.jobInfo.jobTitle
            tvJobPrice.text = "${args.jobInfo.price} DZD"
            tvJobDescription.text = args.jobInfo.jobDescription
            tvAdress.text = args.jobInfo.location

            val skills = args.jobInfo.jobSkills
            viewPagerAdapter.differ.submitList(args.jobInfo.jobImages)

            if (skills != null) {
                for (skill in skills) {
                    val chip = Chip(requireContext())
                    chip.text = skill
                    chip.setChipBackgroundColorResource(R.color.chipColor) // Customize chip color if needed
                    chipGroupSkills.addView(chip)
                }
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.employerInfo.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            // showLoading()
                        }

                        is Resource.Success -> {
                            Log.d("debugging", resource.data.toString())
                            binding.apply {
                                tvEmail.text = resource.data?.email
                                tvFullName.text = resource.data?.firstName +" "+ resource.data?.lastName
                                tvPhone.text = resource.data?.phoneNumber

                            }
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

    private fun setupViewPagerRv() {
        binding.apply {
            viewpager2Images.adapter = viewPagerAdapter
        }
    }


}
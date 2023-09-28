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
import com.bumptech.glide.Glide
import com.example.easy.adapters.HomeViewpagerAdapter
import com.example.easy.data.User
import com.example.easy.databinding.FragmentHomeBinding
import com.example.easy.fragments.client.categories.BusinessAdministrationFragment
import com.example.easy.fragments.client.categories.ConstructionFragment
import com.example.easy.fragments.client.categories.ServiceIndustryFragment
import com.example.easy.fragments.client.categories.EducationFragment
import com.example.easy.fragments.client.categories.HealthCareFragment
import com.example.easy.fragments.client.categories.LawAndGovernmentFragment
import com.example.easy.fragments.client.categories.MainCategoryFragment
import com.example.easy.fragments.client.categories.ManufacturingFragment
import com.example.easy.fragments.client.categories.TechnologyFragment
import com.example.easy.fragments.client.categories.TransportationFragment
import com.example.easy.utils.Resource
import com.example.easy.utils.showBottomNav
import com.example.easy.viewmodels.CustomizeProfileViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val profileViewModel by viewModels<CustomizeProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.getUser()
        val categoriesFragments =
            arrayListOf(
                MainCategoryFragment(),
                EducationFragment(),
                LawAndGovernmentFragment(),
                HealthCareFragment(),
                ServiceIndustryFragment(),
                TransportationFragment(),
                ConstructionFragment(),
                ManufacturingFragment(),
                BusinessAdministrationFragment(),
                TechnologyFragment()
            )
        val viewPagerAdapter =
            HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.apply {
            viewPagerHome.adapter = viewPagerAdapter
            tabLayout.setSelectedTabIndicator(null)
            TabLayoutMediator(tabLayout, viewPagerHome) { tab, position ->
                when (position) {
                    0 -> tab.text = "Home"
                    1 -> tab.text = "Education"
                    2 -> tab.text = "Law and Government"
                    3 -> tab.text = "Health care"
                    4 -> tab.text = "Service industry"
                    5 -> tab.text = "Transportation"
                    6 -> tab.text = "Construction"
                    7 -> tab.text = "Manufacturing"
                    8 -> tab.text = "Business administration"
                    9 -> tab.text = "Technology"
                }

            }.attach()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.userData.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            Snackbar.make(view, "Loading", Snackbar.LENGTH_LONG).show()

                        }

                        is Resource.Success -> {

                            showUserInformation(resource.data!!)
                            Log.d("debugging", "${resource.message}")
                            //Snackbar.make(view, "${resource.message}", Snackbar.LENGTH_LONG).show()
                        }

                        is Resource.Failed -> {

                            Log.d(
                                "debugging",
                                "the problem is : ${resource.data?.firstName},${resource.data?.lastName},${resource.data?.email},${resource.data?.imagePath}"
                            )
                            Snackbar.make(
                                view,
                                " the problem is : ${resource.message}",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        else -> Unit
                    }
                }

            }
        }

    }
    private fun showUserInformation(data: User) {
        binding.apply {
            tvFirstName.text = "Hi, ${data.firstName} !"
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNav()
    }

}
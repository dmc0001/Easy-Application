package com.example.easy.fragments.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.easy.adapters.HomeViewpagerAdapter
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
import com.example.easy.utils.showBottomNav
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

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

    }

    override fun onResume() {
        super.onResume()
        showBottomNav()
    }

}
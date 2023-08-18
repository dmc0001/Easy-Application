package com.example.easy.fragments.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.easy.R
import com.example.easy.adapters.HomeViewpagerAdapter
import com.example.easy.databinding.FragmentHomeBinding
import com.example.easy.fragments.categories.CatFourFragment
import com.example.easy.fragments.categories.CatOneFragment
import com.example.easy.fragments.categories.CatThreeFragment
import com.example.easy.fragments.categories.CatTwoFragment
import com.example.easy.fragments.categories.MainCategoryFragment
import com.example.easy.utils.Resource
import com.google.android.material.tabs.TabLayoutMediator


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
                CatOneFragment(),
                CatTwoFragment(),
                CatThreeFragment(),
                CatFourFragment()
            )
        val viewPagerAdapter =
            HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.apply {
            viewPagerHome.adapter = viewPagerAdapter
            tabLayout.setSelectedTabIndicator(null)
            TabLayoutMediator(tabLayout, viewPagerHome) { tab, position ->
                when (position) {
                    0 -> tab.text = "Home"
                    1 -> tab.text = "Tab 2"
                    2 -> tab.text = "Tab 3"
                    3 -> tab.text = "Tab 4"
                    4 -> tab.text = "Tab 5"
                    5 -> tab.text = "Tab 6"
                }

            }.attach()
        }

    }

}
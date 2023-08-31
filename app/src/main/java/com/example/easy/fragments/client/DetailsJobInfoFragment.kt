package com.example.easy.fragments.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.easy.R
import com.example.easy.activities.ClientActivity
import com.example.easy.adapters.ViewPager2Images
import com.example.easy.databinding.FragmentDetailsJobInfoBinding
import com.example.easy.utils.HideShowBottomNavView
import com.example.easy.utils.hideBottomNav
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip


class DetailsJobInfoFragment : Fragment() {
    lateinit var binding: FragmentDetailsJobInfoBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val args by navArgs<DetailsJobInfoFragmentArgs>()

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
        binding.apply {
            tvJobTitle.text = args.jobInfo.jobTitle
            tvJobPrice.text = "${args.jobInfo.price} DZD"
            tvJobDescription.text = args.jobInfo.jobDescription
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
    }

    private fun setupViewPagerRv() {
        binding.apply {
            viewpager2Images.adapter = viewPagerAdapter
        }
    }


}
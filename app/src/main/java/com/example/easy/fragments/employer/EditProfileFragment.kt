package com.example.easy.fragments.employer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.easy.R
import com.example.easy.databinding.FragmentEditProfileBinding
import com.google.android.material.chip.Chip


class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val categoryOptions = resources.getStringArray(R.array.jobs_categories)
            val categoryAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categoryOptions
            )
            edCategory.setAdapter(categoryAdapter)

            val chipGroupSkills = chipGroupSkills
            val layoutAddSkill = layoutAddSkill
            val edAddSkill = edAddSkill
            val btnAddSkill = btnAddSkill
            val chipAddSkill = chipSkill1

            chipAddSkill.setOnClickListener {
                layoutAddSkill.visibility = View.VISIBLE
            }

            btnAddSkill.setOnClickListener {
                val newSkill = edAddSkill.text.toString().trim()
                if (newSkill.isNotEmpty()) {
                    val newChip = Chip(requireContext())
                    newChip.text = newSkill
                    newChip.isCloseIconVisible = true
                    newChip.setOnCloseIconClickListener {
                        chipGroupSkills.removeView(newChip)
                    }
                    chipGroupSkills.addView(
                        newChip,
                        chipGroupSkills.childCount - 1
                    ) // Add before the "+" chip
                    edAddSkill.text.clear()
                }
                layoutAddSkill.visibility = View.GONE
            }
        }
    }
}
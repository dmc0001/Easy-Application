package com.example.easy.fragments.employer

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.easy.R
import com.example.easy.data.JobInformation
import com.example.easy.databinding.FragmentEditProfileBinding
import com.example.easy.utils.Resource
import com.example.easy.viewmodels.EditProfileViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private val editProfileViewModel by viewModels<EditProfileViewModel>()


    private val selectedImageUris = mutableListOf<Uri>()

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
        val selectedSkills: MutableList<String> = mutableListOf()
        binding.apply {

            val categoryOptions = resources.getStringArray(R.array.jobs_categories)
            val categoryAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categoryOptions
            )
            edCategory.setAdapter(categoryAdapter)
            
            val locationOptions = resources.getStringArray(R.array.jobs_loacations)
            val locationAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                locationOptions
            )
            edLocation.setAdapter(locationAdapter)

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
                    selectedSkills.add(newSkill)
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


            val selectImageActivityResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == RESULT_OK) {
                        val intent = result.data

                        //Multiple images selected
                        if (intent?.clipData != null) {
                            val count = intent.clipData?.itemCount ?: 0
                            (0 until count).forEach { it ->
                                val imageUri = intent.clipData?.getItemAt(it)?.uri
                                imageUri?.let {
                                    selectedImageUris.add(it)
                                }
                            }
                        } else {
                            val imageUri = intent?.data
                            imageUri?.let {
                                selectedImageUris.add(it)
                            }
                        }
                        updateImages()
                    }
                }
            btnImagePicker.setOnClickListener {
                Intent(ACTION_GET_CONTENT).also {
                    it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    it.type = "image/*"
                    selectImageActivityResult.launch(it)
                }

            }
            btnApply.setOnClickListener {
                val jobTitle = edJobTitle.text.toString()
                val jobCategory = edCategory.text.toString()
                val jobDescription = edDescription.text.toString()
                val jobLocation = edLocation.text.toString()
                val price = edCompensation.text.toString()

                val jobInformation = JobInformation(
                    "",
                    jobTitle,
                    jobCategory,
                    jobDescription,
                    selectedSkills,
                    selectedImageUris.map { it.toString() },
                    jobLocation,
                    price
                )
                editProfileViewModel.saveJobInfo(jobInformation)


                val snackBarMessage =
                    "Job Title: ${jobInformation.jobTitle}\n" +
                            "Job Category: ${jobInformation.jobCategory}\n" +
                            "Job Description: ${jobInformation.jobDescription}\n" +
                            "Job Skills: ${jobInformation.jobSkills?.joinToString(", ")}\n" +
                            "Resume Employer: ${jobInformation.location}\n" +
                            "Job Images: ${jobInformation.jobImages?.joinToString(", ")}\n" +
                            "Price: ${jobInformation.price}"

                Log.d("debugging", snackBarMessage)

            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                editProfileViewModel.uploadData.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.btnApply.startAnimation()

                        }

                        is Resource.Success -> {
                            binding.btnApply.revertAnimation()
                            Log.d("debugging", "${resource.message}")
                            Snackbar.make(view, "${resource.message}", Snackbar.LENGTH_LONG).show()
                        }

                        is Resource.Failed -> {
                            binding.btnApply.revertAnimation()
                            Log.d("debugging", "${resource.message}")
                            Snackbar.make(view, "${resource.message}", Snackbar.LENGTH_LONG).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun updateImages() {
        binding.tvCountImage.text = "You upload ${selectedImageUris.size} images"
        binding.tvCountImage.visibility = View.VISIBLE
    }

}


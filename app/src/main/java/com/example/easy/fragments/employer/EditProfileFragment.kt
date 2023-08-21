package com.example.easy.fragments.employer

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
    private var selectedResumeUri: Uri? = null

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
            btnUploadResume.setOnClickListener {
                openFilePicker()
            }
            imageView.setOnClickListener {
                openImagePicker()
            }
            btnApply.setOnClickListener {
                val jobTitle = edJobTitle.text.toString()
                val jobCategory = edCategory.text.toString()
                val jobDescription = edDescription.text.toString()
                val price = edCompensation.text.toString()
                //  selectedImages.addAll(selectedImageUris.map { it.toString() })
                val jobInformation = JobInformation(
                     // Assign appropriate ID
                    jobTitle = jobTitle,
                    jobCategory = jobCategory,
                    jobDescription = jobDescription,
                    jobSkills = selectedSkills,
                    jobImages = selectedImageUris.map { it.toString() },
                    resumeEmployer = selectedResumeUri?.toString()
                        ?: "", // Handle resume information if needed
                    price = price
                )
                editProfileViewModel.saveJobInfo(jobInformation)


                val snackBarMessage =
                            "Job Title: ${jobInformation.jobTitle}\n" +
                            "Job Category: ${jobInformation.jobCategory}\n" +
                            "Job Description: ${jobInformation.jobDescription}\n" +
                            "Job Skills: ${jobInformation.jobSkills?.joinToString(", ")}\n" +
                            "Resume Employer: ${jobInformation.resumeEmployer}\n" +
                            "Job Images: ${jobInformation.jobImages?.joinToString(", ")}\n" +
                            "Price: ${jobInformation.price}"

                Log.d("debugging",snackBarMessage)

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
                            Log.d("debugging", "${ resource.message }")
                            Snackbar.make(view,"${ resource.message }",Snackbar.LENGTH_LONG).show()
                        }

                        is Resource.Failed -> {
                            binding.btnApply.revertAnimation()
                            Log.d("debugging", "${ resource.message }")
                            Snackbar.make(view,"${ resource.message }",Snackbar.LENGTH_LONG).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun openImagePicker() {
        val mimeTypes = arrayOf("image/*")
        imagePickerLauncher.launch(mimeTypes)
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri>? ->
            if (uris != null) {
                selectedImageUris.addAll(uris) // Add the URIs to the list
                // Now the selectedImageUris list contains all the selected image URIs
            }
        }

    private fun openFilePicker() {
        val resumeType = "application/pdf"
        filePickerLauncher.launch(resumeType)
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedResumeUri = uri
                val fileName = uri.lastPathSegment
                binding.textViewResumeFileName.text = fileName
                binding.textViewResumeFileName.visibility = View.VISIBLE
            }
        }


}


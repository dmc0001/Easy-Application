package com.example.easy.fragments.client

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.easy.data.User
import com.example.easy.databinding.FragmentCustomizeProfileBinding
import com.example.easy.dialogs.setupBottomSheetForgetPasswordDialog
import com.example.easy.utils.Resource
import com.example.easy.utils.hideBottomNav
import com.example.easy.viewmodels.CustomizeProfileViewModel
import com.example.easy.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomizeProfileFragment : Fragment() {

    private lateinit var binding: FragmentCustomizeProfileBinding
    private var selectedImageUri: Uri? = null
    private val customizeProfileViewModel by viewModels<CustomizeProfileViewModel>()
    private val viewModelLogin by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNav()
        // Inflate the layout for this fragment
        binding = FragmentCustomizeProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            customizeProfileViewModel.getUser()
            buttonSave.setOnClickListener {

                val firstName = edFirstName.text.toString().trim()
                val lastName = edLastName.text.toString().trim()
                val email = edEmail.text.toString().trim()
              //  val data =  customizeProfileViewModel.getUser()
                val user = User(firstName, lastName, email)
                customizeProfileViewModel.updateUser(user, selectedImageUri)
            }
            imageEdit.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT).also {
                    it.type = "image/*"
                    selectImageActivityResult.launch(it)
                }

            }
            imgClose.setOnClickListener {
                findNavController().navigateUp()
            }
            tvUpdatePassword.setOnClickListener {
                setupBottomSheetForgetPasswordDialog { email ->
                    viewModelLogin.resetPassword(email)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                customizeProfileViewModel.userData.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showUserLoading()
                        }

                        is Resource.Success -> {

                              hideUserLoading()
                            showUserInformation(resource.data!!)
                            Log.d("debugging", "${resource.message}")
                           // Snackbar.make(view, "${resource.message}", Snackbar.LENGTH_LONG).show()
                        }

                        is Resource.Failed -> {
                            showUserLoading()
                            Log.d("debugging", " the problem is : ${resource.message}")
                            Snackbar.make(view, " the problem is : ${resource.message}", Snackbar.LENGTH_LONG).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                customizeProfileViewModel.editUserInfo.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.buttonSave.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.buttonSave.revertAnimation()
                            showUserInformation(resource.data!!)
                            Log.d("debugging", "${resource.message}")
                            Snackbar.make(view, "${resource.message}", Snackbar.LENGTH_LONG).show()
                        }

                        is Resource.Failed -> {
                            binding.buttonSave.revertAnimation()
                            Log.d("debugging", "the problem is : ${resource.data?.firstName},${resource.data?.lastName},${resource.data?.email},${resource.data?.imagePath}")
                            Snackbar.make(view, " the problem is : ${resource.message}", Snackbar.LENGTH_LONG).show()
                        }

                        else -> Unit
                    }
                }
            }
        }


    }

    private fun showUserInformation(data: User) {
        binding.apply {
            customizeProfileViewModel.getUser()
            Glide.with(this@CustomizeProfileFragment).load(data.imagePath).into(imageUser)
            edFirstName.setText(data.firstName)
            edLastName.setText(data.lastName)
            edEmail.setText(data.email)

        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }


    private val selectImageActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val imageUri = intent?.data
                imageUri?.let {
                    selectedImageUri = it
                }
            }
        }

}

package com.example.easy.fragments.client

import android.content.Intent
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
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.easy.R
import com.example.easy.activities.LoginRegisterActivity
import com.example.easy.data.User
import com.example.easy.databinding.FragmentProfileBinding
import com.example.easy.utils.Resource
import com.example.easy.utils.showBottomNav
import com.example.easy.viewmodels.LogoutViewModel
import com.example.easy.viewmodels.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val logOutViewModel by viewModels<LogoutViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            profileViewModel.getUser()
            btnEdit.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_profileFragment_to_customizeProfileFragment)
            }
            btnLogout.setOnClickListener {
                // Clear Glide's memory cache
                Glide.get(requireContext()).clearMemory()

                // Clear Glide's disk cache (This may take some time)
                lifecycleScope.launch {
                    try {
                        Glide.get(requireContext()).clearDiskCache()
                    } catch (e: Exception) {
                        // Handle any exceptions that may occur during disk cache clearing.
                        Log.e("GlideDiskCache", "Failed to clear disk cache: ${e.message}")
                    }
                  //  Glide.with(this@ProfileFragment).load(R.drawable.ic_profile).into(imageUser)

                    // Logout and navigate to the login/register activity
                    logOutViewModel.logout()
                    Intent(requireActivity(), LoginRegisterActivity::class.java).also {
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(it)
                        requireActivity().finish()
                    }
                }
            }


        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.userData.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            Snackbar.make(view, "Loading", Snackbar.LENGTH_LONG).show()
                            binding.progressbarAccount.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.progressbarAccount.visibility = View.GONE
                            showUserInformation(resource.data!!)
                            Log.d("debugging", "${resource.message}")
                            //Snackbar.make(view, "${resource.message}", Snackbar.LENGTH_LONG).show()
                        }

                        is Resource.Failed -> {
                            binding.progressbarAccount.visibility = View.GONE
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

    private fun showUserInformation(data: User?) {
        binding.apply {
           // if (data != null) {
                Glide.with(this@ProfileFragment).load(data?.imagePath).error(R.drawable.ic_profile)
                    .into(imageUser)
                tvFirstName.text = data?.firstName
                tvLastName.text = data?.lastName
                tvEmail.text = data?.email
           // } else {
                // Handle the case where user data is null
                // You can display a default user image or show an error message

           // }
        }
    }


    override fun onResume() {
        super.onResume()
        showBottomNav()


    }


}
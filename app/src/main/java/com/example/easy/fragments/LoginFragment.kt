package com.example.easy.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.example.easy.R
import com.example.easy.activities.ClientActivity
import com.example.easy.databinding.FragmentLoginBinding
import com.example.easy.dialogs.setupBottomSheetDialog
import com.example.easy.utils.LoginValidation
import com.example.easy.utils.Resource
import com.example.easy.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModelLogin by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()
                viewModelLogin.loginWithEmailAndPassword(email, password)

            }
            tvLogin.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_loginFragment_to_registerFragment)
            }
            tvForgetPassword.setOnClickListener {
                setupBottomSheetDialog { email ->
                    viewModelLogin.resetPassword(email)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelLogin.resetPassword.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {

                        }

                        is Resource.Success -> {
                            Snackbar.make(
                                requireView(),
                                "Rest link was sent to your email",
                                Snackbar.LENGTH_LONG
                            ).show()


                        }

                        is Resource.Failed -> {
                            Snackbar.make(
                                requireView(),
                                "Error :${resource.message.toString()}",
                                Snackbar.LENGTH_LONG
                            ).show()


                        }

                        else -> Unit
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelLogin.login.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.btnLogin.startAnimation()
                        }

                        is Resource.Success -> {
                            Log.d("debugging", "Login has been success.")
                            binding.btnLogin.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                resource.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            Intent(requireContext(), ClientActivity::class.java).also {
                                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(it)
                            }

                        }

                        is Resource.Failed -> {
                            Log.d(
                                "test",
                                "Login has been failed: ${resource.message}"
                            )
                            Toast.makeText(requireContext(),"Login has been failed: ${resource.message}",Toast.LENGTH_LONG)
                            binding.btnLogin.revertAnimation()
                            // Show an error message to the user or take appropriate action.
                        }

                        else -> Unit
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelLogin.validationLogin.collect { validation ->
                    if (validation.email is LoginValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.etEmail.apply {
                                requestFocus()
                                error = validation.email.message

                            }
                        }
                    }
                    if (validation.password is LoginValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.etPassword.apply {
                                requestFocus()
                                error = validation.password.message
                            }
                        }
                    }
                }
            }
        }
    }
}
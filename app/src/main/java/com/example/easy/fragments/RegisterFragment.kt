package com.example.easy.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.easy.R
import com.example.easy.data.User
import com.example.easy.databinding.FragmentRegisterBinding
import com.example.easy.utils.RegisterValidation
import com.example.easy.utils.Resource
import com.example.easy.viewmodels.AuthPhoneNumberViewModel
import com.example.easy.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var user : User
    private lateinit var password :String
    private val viewModelRegister by viewModels<RegisterViewModel>()
    private val viewModelPhoneAuth by viewModels<AuthPhoneNumberViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnRegister.setOnClickListener {
                val firstName = etFirstName.text.toString().trim()
                val lastName = etLastName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val cc = ccp.selectedCountryCodeWithPlus
                val number = etPhoneNumberRegister.text.toString().trim()
                val phoneNumber = "$cc$number"
                password = etPasswordRegister.text.toString()
                user = User(firstName, lastName, email, phoneNumber)
                viewModelPhoneAuth.sendVerificationCode(phoneNumber, requireActivity())
                /*if (viewModelRegister.checkValidation(user, password)) {
                 val action = RegisterFragmentDirections.actionRegisterFragmentToVerificationFragment(user)
                    Navigation.findNavController(view)
                        .navigate(action)
                }*/
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModelPhoneAuth.isVerificationInProgress.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.btnRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        Log.d("SendOtpFragment", "Verification initiated.")
                        binding.btnRegister.revertAnimation()
                        val verificationId = viewModelPhoneAuth.verificationId.value
                        if (verificationId != null) {
                            Log.d("SendOtpFragment", "Verification ID is $verificationId.")
                            // Proceed to VerificationOtpFragment with the verification ID.
                            if (viewModelRegister.checkValidation(user,password)) {
                                val action = RegisterFragmentDirections.actionRegisterFragmentToVerificationFragment(verificationId,password,user)
                                Navigation.findNavController(view)
                                    .navigate(action)
                            }

                        } else {
                            // Handle error: Verification ID is null.
                            Log.d("SendOtpFragment", "Verification ID is null.")
                        }
                    }
                    is Resource.Failed -> {
                        Log.d(
                            "SendOtpFragment",
                            "Verification initiation failed: ${resource.message}"
                        )
                        binding.btnRegister.revertAnimation()
                        // Show an error message to the user or take appropriate action.
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModelRegister.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.etEmail.apply {
                            requestFocus()
                            error = validation.email.message

                        }
                    }
                }
                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.etPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
                if (validation.firstname is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.etFirstName.apply {
                            requestFocus()
                            error = validation.firstname.message
                        }
                    }
                }
                if (validation.lastname is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.etLastName.apply {
                            requestFocus()
                            error = validation.lastname.message
                        }
                    }
                }
                if (validation.phoneNumber is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.etPhoneNumberRegister.apply {
                            requestFocus()
                            error = validation.phoneNumber.message
                        }
                    }
                }
            }
        }
    }

}
package com.example.easy.fragments

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
import androidx.navigation.fragment.navArgs
import com.example.easy.databinding.FragmentVerificationBinding
import com.example.easy.utils.Resource
import com.example.easy.utils.VerificationOTPValidation
import com.example.easy.viewmodels.AuthPhoneNumberViewModel
import com.example.easy.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class VerificationFragment : Fragment() {

    private lateinit var binding: FragmentVerificationBinding
    private val viewModelPhoneAuth by viewModels<AuthPhoneNumberViewModel>()
    private val viewModelRegister by viewModels<RegisterViewModel>()
    private val args by navArgs<VerificationFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerificationBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), args.userdata?.role ?: "none", Toast.LENGTH_SHORT)
            .show()
        binding.apply {
            btnVerify.setOnClickListener {
                val smsCode = etCodeSendVerification.text.toString()
                val verificationId = args.verificationId
                if (verificationId != null) {
                    viewModelPhoneAuth.signInWithVerificationCode(verificationId, smsCode)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPhoneAuth.isVerificationInProgress.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.btnVerify.startAnimation()
                        }

                        is Resource.Success -> {
                            Log.d(
                                "VerificationOtpFragment",
                                "Verification success: ${resource.data}"
                            )
                            binding.btnVerify.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                resource.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModelRegister.createAccountWithEmailAndPassword(
                                args.userdata!!,
                                args.password!!
                            )
                            viewModelRegister.register
                            // Proceed to the next screen or perform necessary actions.
                        }

                        is Resource.Failed -> {
                            Log.d(
                                "VerificationOtpFragment",
                                "Verification failed: ${resource.message}"
                            )
                            binding.btnVerify.revertAnimation()
                            // Show an error message to the user or take appropriate action.
                        }

                        else -> Unit
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPhoneAuth.validationOtp.collect { validation ->
                    if (validation.otp is VerificationOTPValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.etCodeSendVerification.apply {
                                requestFocus()
                                error = validation.otp.message

                            }
                        }
                    }
                }
            }
        }

    }
}



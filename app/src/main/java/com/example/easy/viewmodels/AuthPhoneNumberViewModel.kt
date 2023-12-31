package com.example.easy.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.User
import com.example.easy.utils.OTPFieldsState
import com.example.easy.utils.RegisterFieldsState
import com.example.easy.utils.RegisterValidation
import com.example.easy.utils.Resource
import com.example.easy.utils.VerificationOTPValidation
import com.example.easy.utils.validEmailRegister
import com.example.easy.utils.validFirstName
import com.example.easy.utils.validLastName
import com.example.easy.utils.validOTP
import com.example.easy.utils.validPasswordRegister
import com.example.easy.utils.validPhoneNumber
import com.example.easy.utils.validRole
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AuthPhoneNumberViewModel @Inject constructor(private val auth: FirebaseAuth) : ViewModel() {

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId

    private val _isVerificationInProgress =
        MutableStateFlow<Resource<Boolean>>(Resource.Unspecified())
    val isVerificationInProgress: Flow<Resource<Boolean>> = _isVerificationInProgress

    private val _validationOtp = Channel<OTPFieldsState>()
    val validationOtp = _validationOtp.receiveAsFlow()

    private val _validationRegister = Channel<RegisterFieldsState>()
    val validationRegister = _validationRegister.receiveAsFlow()

    fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        user: User,
        password: String
    ) {

        if (checkValidationRegister(user, password)) {

            runBlocking {
                _isVerificationInProgress.emit(Resource.Loading())
            }

            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.

                    Log.d("sendVerificationCode", "Verification Failed: $e")


                    // Show a message and update the UI
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken,
                ) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.

                    _verificationId.value = verificationId
                    _isVerificationInProgress.value =
                        Resource.Success(true, "onCodeSent: $verificationId")

                    Log.d("sendVerificationCode", "onCodeSent: $verificationId")

                    // Save verification ID and resending token so we can use them later
                    // storedVerificationId = verificationId
                    // resendToken = token
                }

            }
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(activity) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } else {
            val registerFieldsState =
                RegisterFieldsState(
                    validEmailRegister(user.email),
                    validPasswordRegister(password),
                    validFirstName(user.firstName),
                    validLastName(user.lastName),
                    validPhoneNumber(user.phoneNumber),
                    validPhoneNumber(user.phoneNumber)
                )
            viewModelScope.launch {
                _validationRegister.send(registerFieldsState)
            }

        }
    }

    private fun checkValidationRegister(user: User, password: String): Boolean {
        val emailValidation = validEmailRegister(user.email)
        val passwordValidation = validPasswordRegister(password)
        val phoneNumberValidation = validPhoneNumber(user.phoneNumber)
        val firstnameValidation = validFirstName(user.firstName)
        val lastnameValidation = validLastName(user.lastName)
        val roleValidation = validRole(user.role)
        return (emailValidation is RegisterValidation.Success
                && passwordValidation is RegisterValidation.Success
                && phoneNumberValidation is RegisterValidation.Success
                && firstnameValidation is RegisterValidation.Success
                && lastnameValidation is RegisterValidation.Success
                && roleValidation is RegisterValidation.Success)
    }

    fun signInWithVerificationCode(verificationId: String, codeSendVerification: String) {
        if (checkOTPValidation(codeSendVerification)) {
            val credential = PhoneAuthProvider.getCredential(verificationId, codeSendVerification)
            signInWithPhoneAuthCredential(credential)
        } else {
            val otpFieldsState = OTPFieldsState(validOTP(codeSendVerification))
            viewModelScope.launch {
                _validationOtp.send(otpFieldsState)
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        runBlocking {
            _isVerificationInProgress.emit(Resource.Loading())
        }

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(
                        "signInWithPhoneAuthCredential",
                        "signInWithCredential success: ${task.result} "
                    )
                    _isVerificationInProgress.value =
                        Resource.Success(false, "signInWithCredential success: ${task.result}")
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d(
                        "signInWithPhoneAuthCredential",
                        "signInWithCredential failure: ${task.exception} "
                    )
                    _isVerificationInProgress.value =
                        Resource.Failed("signInWithCredential failure: ${task.exception}")
                }
            }
    }

    private fun checkOTPValidation(codeSendVerification: String): Boolean {
        val otpValidation = validOTP(codeSendVerification)
        return otpValidation is VerificationOTPValidation.Success
    }

}
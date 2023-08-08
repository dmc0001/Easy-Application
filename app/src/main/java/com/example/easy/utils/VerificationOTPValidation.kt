package com.example.easy.utils

sealed class VerificationOTPValidation {
    object Success : VerificationOTPValidation()
    data class Failed(val message: String) : VerificationOTPValidation()
}

data class OTPFieldsState(
    val otp: VerificationOTPValidation,
)
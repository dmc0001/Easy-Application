package com.example.easy.utils

import com.example.easy.data.User

sealed class RegisterValidation {
    object Success: RegisterValidation()
    data class Failed(val message:String): RegisterValidation()
}
data class RegisterFieldsState(
    val email : RegisterValidation,
    val password :RegisterValidation,
    val firstname:RegisterValidation,
    val lastname:RegisterValidation,
    val phoneNumber:RegisterValidation,
    val role:RegisterValidation
)
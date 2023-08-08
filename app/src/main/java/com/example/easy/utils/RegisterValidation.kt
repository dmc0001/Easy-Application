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
    val phoneNumber:RegisterValidation
) {
    fun isValid( user : User, password: String): Boolean {
        val emailValidation = validEmail(user.email)
        val passwordValidation = validPassword(password)
        val firstNameValidation = validFirstName(user.firstName)
        val lastnameValidation = validLastName(user.lastName)
        val phoneNumberValidation = validPhoneNumber(user.phoneNumber)

        return (emailValidation is RegisterValidation.Success
                && passwordValidation is RegisterValidation.Success
                && lastnameValidation is RegisterValidation.Success
                && firstNameValidation is RegisterValidation.Success
                && phoneNumberValidation is RegisterValidation.Success)
    }
}
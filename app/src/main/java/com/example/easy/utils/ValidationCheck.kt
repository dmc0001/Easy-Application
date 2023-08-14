package com.example.easy.utils

import android.util.Patterns

fun validOTP(
    codeSendVerification: String
): VerificationOTPValidation {

    if (codeSendVerification.isEmpty()) {
        return VerificationOTPValidation.Failed("OTP is not valid!")
    }
    if (codeSendVerification.length > 6) return VerificationOTPValidation.Failed("Code verification should contains 6 char only")
    return VerificationOTPValidation.Success
}

fun validEmailRegister(email: String): RegisterValidation {
    if (email.isEmpty()) return RegisterValidation.Failed("Email cannot be an empty")

    if (!Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    ) return RegisterValidation.Failed("Wrong email format")
    return RegisterValidation.Success
}

fun validFirstName(firstName: String): RegisterValidation {
    if (firstName.isEmpty()) return RegisterValidation.Failed("First name cannot be an empty")
    return RegisterValidation.Success
}

fun validRole(role: String): RegisterValidation {
    if (role.isEmpty()) return RegisterValidation.Failed("Role cannot be an empty")
    return RegisterValidation.Success
}

fun validLastName(lastName: String): RegisterValidation {
    if (lastName.isEmpty()) return RegisterValidation.Failed("Last name cannot be an empty")
    return RegisterValidation.Success
}

fun validPasswordRegister(password: String): RegisterValidation {
    if (password.isEmpty()) return RegisterValidation.Failed("Password cannot be an empty")
    if (password.length < 6) return RegisterValidation.Failed("Password should contains 6 char at least")
    return RegisterValidation.Success
}

fun validEmailLogin(email: String): LoginValidation {
    if (email.isEmpty()) return LoginValidation.Failed("Email cannot be an empty")

    if (!Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    ) return LoginValidation.Failed("Wrong email format")
    return LoginValidation.Success
}

fun validPasswordLogin(password: String): LoginValidation {
    if (password.isEmpty()) return LoginValidation.Failed("Password cannot be an empty")
    if (password.length < 6) return LoginValidation.Failed("Password should contains 6 char at least")
    return LoginValidation.Success
}

fun validPhoneNumber(phone: String): RegisterValidation {
    if (phone.isEmpty()) return RegisterValidation.Failed("Phone number is required!")
    else if (!Patterns.PHONE.matcher(phone).matches() || phone.length < 11)

        return RegisterValidation.Failed("Need valid phone number!")

    return RegisterValidation.Success
}

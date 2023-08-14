package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.utils.LoginFieldsState
import com.example.easy.utils.RegisterValidation
import com.example.easy.utils.Resource
import com.example.easy.utils.validEmailLogin
import com.example.easy.utils.validEmailRegister
import com.example.easy.utils.validPasswordLogin
import com.example.easy.utils.validPasswordRegister
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private var auth: FirebaseAuth) : ViewModel() {
    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    private val _validationLogin = Channel<LoginFieldsState>()
    val validationLogin = _validationLogin.receiveAsFlow()

    fun loginWithEmailAndPassword(
        email: String,
        password: String
    ) {

        if (checkValidationLogin(email, password)) {
            runBlocking {
                _login.emit(Resource.Loading())
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { it ->

                    // Sign in success, update UI with the signed-in user's information
                    viewModelScope.launch {

                        it.user?.let {

                            _login.emit(
                                Resource.Success(
                                    it,
                                    "signInWithEmailAndPassword : success"
                                )
                            )
                        }
                    }
                    //updateUI(user)

                }.addOnFailureListener {

                    // If sign in fails, display a message to the user.
                    viewModelScope.launch {

                        _login.emit(Resource.Failed("signInWithEmailAndPassword : failure"))
                    }
                    // updateUI(null)
                }
        } else {
            val loginFieldsState =
                LoginFieldsState(
                    validEmailLogin(email),
                    validPasswordLogin(password)
                )
            viewModelScope.launch {
                _validationLogin.send(loginFieldsState)
            }
        }
    }
    fun resetPassword(email: String){
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }
        auth
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email,"Successfully rest password"))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Failed(it.message.toString()))
                }
            }
    }

    private fun checkValidationLogin(email: String, password: String): Boolean {
        val emailValidation = validEmailRegister(email)
        val passwordValidation = validPasswordRegister(password)

        return (emailValidation is RegisterValidation.Success
                && passwordValidation is RegisterValidation.Success)
    }
}
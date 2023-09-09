package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.utils.Constants.USER_COLLECTION
import com.example.easy.utils.LoginFieldsState
import com.example.easy.utils.LoginValidation
import com.example.easy.utils.RegisterValidation
import com.example.easy.utils.Resource
import com.example.easy.utils.validEmailLogin
import com.example.easy.utils.validEmailRegister
import com.example.easy.utils.validPasswordLogin
import com.example.easy.utils.validPasswordRegister
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private var auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {
    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    private val _validationLogin = Channel<LoginFieldsState>()
    val validationLogin = _validationLogin.receiveAsFlow()

    private val _userRole = MutableSharedFlow<Resource<String>>()
    val userRole = _userRole.asSharedFlow()

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

                        _login.emit(Resource.Failed("${it.message}"))
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

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }
        auth
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email, "Successfully rest password"))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Failed(it.message.toString()))
                }
            }
    }

    // Function to fetch user data and determine user role
    fun fetchUserRole(email: String) {
        db.collection(USER_COLLECTION) // Replace with your user collection name
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]
                    val role = userDocument.getString("role")
                    role?.let {
                        viewModelScope.launch {
                            _userRole.emit(Resource.Success(it, "the role is : $it"))
                        }
                    } ?: run {
                        viewModelScope.launch {
                            _userRole.emit(Resource.Failed("User role not found"))
                        }
                    }
                }
            }
            .addOnFailureListener {
                _userRole.tryEmit(Resource.Failed(it.message.toString()))
            }
    }

    private fun checkValidationLogin(email: String, password: String): Boolean {
        val emailValidation = validEmailLogin(email)
        val passwordValidation = validPasswordLogin(password)

        // Check if both email and password are valid and not empty
        return (emailValidation is LoginValidation.Success
                && passwordValidation is LoginValidation.Success
                && password.isNotBlank()) // Ensure password is not empty
    }

}
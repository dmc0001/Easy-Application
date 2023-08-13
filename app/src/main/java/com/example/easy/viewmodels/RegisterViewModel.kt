package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.User
import com.example.easy.utils.Constants.USER_COLLECTION
import com.example.easy.utils.RegisterFieldsState
import com.example.easy.utils.Resource
import com.example.easy.utils.validEmail
import com.example.easy.utils.validFirstName
import com.example.easy.utils.validLastName
import com.example.easy.utils.validPassword
import com.example.easy.utils.validPhoneNumber
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) :
    ViewModel() {
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register
    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()


    fun createAccountWithEmailAndPassword(
        user: User,
        password: String
    ) {
        /*runBlocking {
            _register.emit(Resource.Loading())
        }*/
        firebaseAuth.createUserWithEmailAndPassword(user.email, password)
            .addOnSuccessListener { it ->
                it.user?.let {
                    // _register.value = Resource.Success(it, "Creating account successful")
                    saveUserData(it.uid, user)
                }
            }.addOnFailureListener {
                _register.value = Resource.Failed(it.message.toString())
            }

    }

    private fun saveUserData(userUid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user, "Creating account successful")
            }
            .addOnFailureListener {
                _register.value = Resource.Failed(it.message.toString())
            }
    }

    fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validEmail(user.email)
        val passwordValidation = validPassword(password)
        val firstNameValidation = validFirstName(user.firstName)
        val lastnameValidation = validLastName(user.lastName)
        val phoneNumberValidation = validPhoneNumber(user.phoneNumber)

        val registerFieldState = RegisterFieldsState(
            emailValidation,
            passwordValidation,
            firstNameValidation,
            lastnameValidation,
            phoneNumberValidation
        )
        return if (registerFieldState.isValid(user, password)) {
            true
        } else {
            viewModelScope.launch {
                _validation.send(registerFieldState)
            }
            false
        }
    }

}
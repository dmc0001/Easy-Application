package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import com.example.easy.data.User
import com.example.easy.utils.Constants.USER_COLLECTION
import com.example.easy.utils.RegisterFieldsState
import com.example.easy.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) :
    ViewModel() {
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register


    fun createAccountWithEmailAndPassword(
        user: User,
        password: String
    ) {

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

}
package com.example.easy.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.User
import com.example.easy.utils.Constants.USER_COLLECTION
import com.example.easy.utils.RegisterValidation
import com.example.easy.utils.Resource
import com.example.easy.utils.validEmailRegister
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CustomizeProfileViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
) : ViewModel() {
    private val _userData = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val userData: Flow<Resource<User>> = _userData

    private val _editUserInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val editUserInfo: Flow<Resource<User>> = _editUserInfo

    fun getUser() {
        db.collection(USER_COLLECTION)
            .document(auth.uid!!) // Use the UID as the document ID
            .get()
            //   data, SetOptions.merge()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    _userData.value = Resource.Success(user, "Getting data successful")
                }
            }
            .addOnFailureListener { e ->
                _userData.value = Resource.Failed(e.message.toString())
            }
    }

    private fun uploadFile(
        fileToUpload: String,
        location: String,
        onSuccess: (Uri) -> Unit,
        onFailure: (Exception) -> Unit
    ): String {
        viewModelScope.launch {
            _userData.emit(Resource.Loading())
        }
        val storageRef = storage.reference

        val child = storageRef.child(location)
        child.putFile(Uri.parse(fileToUpload)).addOnCompleteListener { task ->
            if (task.isSuccessful)
                child.downloadUrl.addOnSuccessListener(onSuccess)
                    .addOnFailureListener(onFailure)
            else
                task.exception?.let { onFailure(it) }
        }
        return child.downloadUrl.toString()
    }

    fun updateUser(user: User, imageUri: Uri?) {
        if (!checkValid(user)) {
            viewModelScope.launch {
                _editUserInfo.emit(Resource.Failed("Failed"))
            }
        }
        viewModelScope.launch {
            _editUserInfo.emit(Resource.Loading())
        }
        if (imageUri == null) {
            saveUserInformation(user, true)
        } else {
            saveUserInformationWithNewImage(user, imageUri)
        }

    }

    private fun saveUserInformation(user: User, shouldRetrievedOldImage: Boolean) {
        Log.d("LogSavingUserInformation", "$user")
        db.runTransaction { transition ->
            val documentRef = db.collection(USER_COLLECTION).document(auth.uid!!)
            if (shouldRetrievedOldImage) {
                val currentUser = transition.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                val mapUser = mapOf(
                    "firstName" to newUser.firstName,
                    "lastName" to newUser.lastName,
                    "email" to newUser.email,
                    "imagePath" to newUser.imagePath
                )
                transition.update(documentRef, mapUser)


            } else {
                val mapUser = mapOf(
                    "firstName" to user.firstName,
                    "lastName" to user.lastName,
                    "email" to user.email,
                    "imagePath" to user.imagePath
                )
                transition.update(documentRef, mapUser)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _userData.emit(Resource.Success(user, "successful saved user data"))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _userData.emit(Resource.Failed(it.message.toString()))
            }
        }
    }

    private fun saveUserInformationWithNewImage(user: User, imageUri: Uri) {
        Log.d("SaveUserInformationWithNewImage", imageUri.toString())
        viewModelScope.launch {
            try {

                uploadFile(
                    imageUri.toString(),
                    "ProfileImage/${auth.uid}/${UUID.randomUUID()}",
                    onSuccess = {
                        saveUserInformation(
                            user.copy(imagePath = it.toString()),
                            false
                        )
                        viewModelScope.launch {
                            _editUserInfo.emit(
                                Resource.Success(
                                    user.copy(
                                        imagePath = it.toString()
                                    ), ""
                                )
                            )
                        }
                    },
                    onFailure = {})

                //   val imageUrl = result.result.storage.downloadUrl.await().toString()

            } catch (e: Exception) {
                Log.d("SaveUserInformationWithNewImage", "Exception : ${e.message}")
                _editUserInfo.emit(Resource.Failed(e.message.toString())) // Emit the error message
            }
        }
    }


    private fun checkValid(user: User): Boolean {

        return ((validEmailRegister(user.email) is RegisterValidation.Success) &&
                (user.firstName.trim().isNotEmpty()) &&
                (user.firstName.trim().isNotEmpty())
                )
    }
}




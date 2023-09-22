package com.example.easy.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.EasyApp
import com.example.easy.data.User
import com.example.easy.utils.Constants.USER_COLLECTION
import com.example.easy.utils.RegisterValidation
import com.example.easy.utils.Resource
import com.example.easy.utils.validEmailRegister
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject
import kotlin.reflect.KFunction1

@HiltViewModel
class CustomizeProfileViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val app: Application
) : AndroidViewModel(app) {
    private val _userData = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val userData: Flow<Resource<User>> = _userData

    private val _editUserInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val editUserInfo: Flow<Resource<User>> = _editUserInfo

    fun getUser() {
        viewModelScope.launch {
            _userData.emit(Resource.Loading())
        }
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
        onSuccess: KFunction1<Uri, Unit>,
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
        Log.d("SaveUserInformationWithNewImage",imageUri.toString())
        viewModelScope.launch {
            try {

                val imageBitMap = MediaStore.Images.Media.getBitmap(
                    getApplication<EasyApp>().contentResolver,
                    imageUri
                )
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitMap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                val imageDirectory =
                    storage.reference.child("ProfileImage/${auth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray)
                val imageUrl = result.result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = imageUrl), false)
            } catch (e: Exception) {
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


/* private fun uploadFile(
     fileToUpload: String,
     location: String,
     onSuccess: KFunction1<Uri, Unit>,
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

 fun userInfo(user: User) {
     val uid = auth.currentUser?.uid
     if (uid != null) {
         val image = user.imagePath

         // Function to handle successful image upload
         fun handleImageUpload(imageDownloadUrl: Uri) {
             // All images uploaded, continue with Firestore document creation
             createFirestoreDocument(uid, user)

         }

         // Function to handle image upload failure
         fun handleImageUploadFailure(exception: Exception) {
             // Handle image upload failure if needed
             Log.e("ImageUpload", "Image upload failed: ${exception.message}")
         }

         // Upload images

         val id = UUID.randomUUID().toString()
         Log.d("Uploading Images", image)

         uploadFile(
             image, // Assuming image is a valid URI
             "UserImage /$uid/$id",
             onSuccess = ::handleImageUpload,
             onFailure = ::handleImageUploadFailure
         )


     }
 }

 private fun createFirestoreDocument(
     uid: String,
     user: User
 ) {
     // Create Firestore document
     val data = hashMapOf(
         "uid" to uid,
         "jobTitle" to user.firstName,
         "jobCategory" to user.lastName,
         "jobDescription" to user.email,
         "jobSkills" to user.imagePath,
     )

     db.collection(USER_COLLECTION)
         .document(uid) // Use the UID as the document ID
         .set(data, SetOptions.merge())
         .addOnSuccessListener {
             _userData.value = Resource.Success(user, "Saving data successful")
         }
         .addOnFailureListener { e ->
             _userData.value = Resource.Failed(e.message.toString())
         }
 }*/


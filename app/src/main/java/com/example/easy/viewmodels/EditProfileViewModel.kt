package com.example.easy.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.JobInformation
import com.example.easy.utils.Constants.JOB_INFO_COLLECTION
import com.example.easy.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.reflect.KFunction1

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _uploadData = MutableStateFlow<Resource<JobInformation>>(Resource.Unspecified())
    val uploadData: Flow<Resource<JobInformation>> = _uploadData

    private fun uploadFile(
        fileToUpload: String,
        location: String,
        onSuccess: KFunction1<Uri, Unit>,
        onFailure: (Exception) -> Unit
    ): String {
        viewModelScope.launch {
            _uploadData.emit(Resource.Loading())
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

    fun saveJobInfo(information: JobInformation) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            val images = information.jobImages
            val uploadedImageUrls =
                mutableListOf<String>() // Store download URLs of uploaded images

            // Function to handle successful image upload
            fun handleImageUpload(imageDownloadUrl: Uri) {
                uploadedImageUrls.add(imageDownloadUrl.toString())
                if (uploadedImageUrls.size == images?.size) {
                    // All images uploaded, continue with Firestore document creation
                    createFirestoreDocument(uid, information, uploadedImageUrls)
                }
            }

            // Function to handle image upload failure
            fun handleImageUploadFailure(exception: Exception) {
                // Handle image upload failure if needed
                Log.e("ImageUpload", "Image upload failed: ${exception.message}")
            }

            // Upload images
            images?.forEach { image ->
                val id = UUID.randomUUID().toString()
                Log.d("Uploading Images", image)

                uploadFile(
                    image, // Assuming image is a valid URI
                    "Job Employer/$uid/$id",
                    onSuccess = ::handleImageUpload,
                    onFailure = ::handleImageUploadFailure
                )
            }


        }
    }

    private fun createFirestoreDocument(
        uid: String,
        information: JobInformation,
        imageUrls: List<String?>,
    ) {
        // Create Firestore document
        val data = hashMapOf(
            "uid" to uid,
            "jobTitle" to information.jobTitle,
            "jobCategory" to information.jobCategory,
            "jobDescription" to information.jobDescription,
            "jobSkills" to information.jobSkills,
            "price" to information.price,
            "jobImages" to imageUrls, // Include the list of image download URLs
            "location" to information.location
        )

        db.collection(JOB_INFO_COLLECTION)
            .document(uid) // Use the UID as the document ID
            .set(data)
            .addOnSuccessListener {
                _uploadData.value = Resource.Success(information, "Saving data successful")
            }
            .addOnFailureListener { e ->
                _uploadData.value = Resource.Failed(e.message.toString())
            }


    }
}



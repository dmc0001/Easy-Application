package com.example.easy.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.easy.data.JobInformation
import com.example.easy.utils.Constants.JOB_INFO_COLLECTION
import com.example.easy.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _uploadData = MutableStateFlow<Resource<JobInformation>>(Resource.Unspecified())
    val uploadData: Flow<Resource<JobInformation>> = _uploadData

    fun saveJobInfo(information: JobInformation) {
        runBlocking {
            _uploadData.emit(Resource.Loading())
        }

        // Get the current user's ID
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            val data = hashMapOf(
                "uid" to firebaseAuth.currentUser?.uid,
                "jobTitle" to information.jobTitle,
                "jobCategory" to information.jobCategory,
                "jobDescription" to information.jobDescription,
                "jobSkills" to information.jobSkills,
                "jobImages" to information.jobImages,
                "resumeEmployer" to information.resumeEmployer,
                "price" to information.price

            )

            db.collection(JOB_INFO_COLLECTION)
                .document(uid) // Use the UID as the document ID
                .set(data)
                .addOnSuccessListener {
                    _uploadData.value = Resource.Success(information, "Saving data successful")
                }
                .addOnFailureListener {
                    _uploadData.value = Resource.Failed(it.message.toString())
                }
            val images = information.jobImages
            if (images != null) {

                for (index in images.indices) {
                    Log.d("debugging my", images[index].substringBefore("%"))
                    uploadFile(
                        images[index],
                        "Job Employer/${uid}/${(images[index]).substringAfter("%")}",
                        onSuccess = {},
                        onFailure = {})
                }
            }
            Log.d("debugging", information.resumeEmployer.toString())
            information.resumeEmployer?.let {
                uploadFile(
                    it,
                    "Job Employer/${uid}/Resume",
                    onSuccess = {},
                    onFailure = {})
            }
        }
    }

    private fun uploadFile(
        fileToUpload: String,
        location: String,
        onSuccess: (Uri?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = storage.reference

        val child = storageRef.child(location)
        child.putFile(Uri.parse(fileToUpload)).addOnCompleteListener { task ->
            if (task.isSuccessful)
                child.downloadUrl.addOnSuccessListener(onSuccess)
                    .addOnFailureListener(onFailure)
            else
                task.exception?.let { onFailure(it) }
        }
    }

    /* fun getFileUri(location: String, onSuccess: (Uri?) -> Unit, onFailure: (Exception) -> Unit) {
         var storageRef = storage.reference

         storageRef.child(location)
         storageRef.downloadUrl.addOnSuccessListener(onSuccess).addOnFailureListener(onFailure)
     }*/


}



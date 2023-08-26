package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.JobInformation
import com.example.easy.utils.Constants.JOB_INFO_COLLECTION
import com.example.easy.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val db: FirebaseFirestore
    //,
    //private val storage: FirebaseStorage,
    //private val auth: FirebaseAuth
) : ViewModel() {
    private val _specialJobs =
        MutableStateFlow<Resource<List<JobInformation>>>(Resource.Unspecified())
    val specialJobs: StateFlow<Resource<List<JobInformation>>> = _specialJobs

    /* private val _jobs =
         MutableStateFlow<Resource<List<HashMap<Any, Any>>>>(Resource.Unspecified())
     val jobs: StateFlow<Resource<List<HashMap<Any, Any>>>> = _jobs*/

    private val pageInfo = PagingInfo()

    init {
        fetchSpecialJobs()
    }

    fun fetchSpecialJobs() {
        runBlocking {
            _specialJobs.emit(Resource.Loading())
        }
        db.collection(JOB_INFO_COLLECTION)
            .limit(pageInfo.jobInfoPage * 10)
            .get()
            .addOnSuccessListener { result ->
                val specialJobs = result.toObjects(JobInformation::class.java)
                pageInfo.isPagingEnd = specialJobs == pageInfo.oldJobsInfo
                pageInfo.oldJobsInfo = specialJobs
                viewModelScope.launch {

                    _specialJobs.emit(Resource.Success(specialJobs, "Get data jobs successfully"))
                }
                pageInfo.jobInfoPage++
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialJobs.emit(Resource.Failed("Get data jobs failure"))
                }
            }
    }

    /* private fun getJobs() {
         runBlocking {
             _jobs.emit(Resource.Loading())
         }
         val userRef = db.collection(JOB_INFO_COLLECTION).document(auth.currentUser?.uid!!)
         userRef.addSnapshotListener { snapshot, e ->

             if (e != null) {
                 viewModelScope.launch {
                     _specialJobs.emit(Resource.Failed("Get data jobs failure : $e"))
                 }

             }

             if (snapshot != null) {
                 if (snapshot.exists()) {

                     val data = hashMapOf(
                         "jobTitle" to snapshot.getString("jobTitle"),
                         "jobCategory" to snapshot.getString("jobCategory"),
                         "jobDescription" to snapshot.getString("jobDescription"),
                         "jobSkills" to snapshot.getString("jobSkills"),
                         "jobImages" to snapshot.getString("jobImages"),
                         "resumeEmployer" to snapshot.getString("resumeEmployer"),
                         "price" to snapshot.getString("price")
                     )

                 }

             }

         }

     }*/

    /* private suspend fun getImagesForJob(job: JobInformation): String? {
         val jobImagesReference = storage.reference.child("Job Employer/${auth.currentUser?.uid}/${job.uid}/")

         val items = jobImagesReference.listAll().await()

         // Get the first item (image) if available
         val firstImageItem = items.items.firstOrNull()

         // Retrieve and return the download URL of the first image
         return firstImageItem?.downloadUrl?.await()?.toString()
     }
 */
    internal data class PagingInfo(
        var jobInfoPage: Long = 1,
        var oldJobsInfo: List<JobInformation> = emptyList(),
        var isPagingEnd: Boolean = false

    )
}
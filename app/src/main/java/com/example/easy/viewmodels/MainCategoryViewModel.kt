package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.JobInformation
import com.example.easy.data.User
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
) : ViewModel() {
    private val _specialJobs =
        MutableStateFlow<Resource<List<JobInformation>>>(Resource.Unspecified())
    val specialJobs: StateFlow<Resource<List<JobInformation>>> = _specialJobs

    /*  private val _employerInfo =
          MutableStateFlow<Resource<List<User>>>(Resource.Unspecified())
      val employerInfo: StateFlow<Resource<List<User>>> = _employerInfo*/


    private val pageInfo = PagingInfo()
    //private val pageInfoTwo = PagingInfoTwo()

    init {
        fetchJobsInfo()
        //fetchEmployerInfo()
    }

    fun fetchJobsInfo() {
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

    /* fun fetchEmployerInfo() {
         runBlocking {
             _employerInfo.emit(Resource.Loading())
         }
         db.collection(USER_COLLECTION)
             .limit(pageInfo.jobInfoPage * 10)
             .get()
             .addOnSuccessListener { result ->
                 val employerInfo = result.toObjects(User::class.java)
                 pageInfoTwo.isPagingEnd = employerInfo == pageInfo.oldJobsInfo
                 pageInfoTwo.oldEmployerInfo = employerInfo
                 viewModelScope.launch {

                     _employerInfo.emit(
                         Resource.Success(
                             employerInfo,
                             "Get employer info successfully"
                         )
                     )
                 }
                 pageInfo.jobInfoPage++
             }.addOnFailureListener {
                 viewModelScope.launch {
                     _employerInfo.emit(Resource.Failed("Get data jobs failure"))
                 }
             }
     }*/


    internal data class PagingInfo(
        var jobInfoPage: Long = 1,
        var oldJobsInfo: List<JobInformation> = emptyList(),
        var isPagingEnd: Boolean = false

    )

    internal data class PagingInfoTwo(
        var jobEmployerPage: Long = 1,
        var oldEmployerInfo: List<User> = emptyList(),
        var isPagingEnd: Boolean = false

    )


}
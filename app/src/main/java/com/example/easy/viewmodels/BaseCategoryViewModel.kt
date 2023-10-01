package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.Category
import com.example.easy.data.JobInformation
import com.example.easy.utils.Constants
import com.example.easy.utils.Constants.JOB_INFO_COLLECTION
import com.example.easy.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class BaseCategoryViewModel constructor(
    private val db: FirebaseFirestore,
    private val category: Category
    ) : ViewModel() {
    private val _specialJobs =
        MutableStateFlow<Resource<List<JobInformation>>>(Resource.Unspecified())
    val specialJobs: StateFlow<Resource<List<JobInformation>>> = _specialJobs

    private val pageInfo = MainCategoryViewModel.PagingInfo()

    init {
        fetchSpecialJobs()
    }

    fun fetchSpecialJobs() {
        runBlocking {
            _specialJobs.emit(Resource.Loading())
        }

        db.collection(JOB_INFO_COLLECTION)
            .whereEqualTo("jobCategory",category.category)
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
    internal data class PagingInfo(
        var jobInfoPage: Long = 1,
        var oldJobsInfo: List<JobInformation> = emptyList(),
        var isPagingEnd: Boolean = false

    )
}
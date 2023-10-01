package com.example.easy.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.JobInformation
import com.example.easy.utils.Constants
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
class SearchViewModel @Inject constructor(private val db: FirebaseFirestore) : ViewModel() {

    private val _searchedJobs =
        MutableStateFlow<Resource<List<JobInformation>>>(Resource.Unspecified())
    val searchedJobs: StateFlow<Resource<List<JobInformation>>> = _searchedJobs
    fun fetchSearchedJobs(searchedJob: String) {
        runBlocking {
            _searchedJobs.emit(Resource.Loading())
        }

        val normalizedSearchQuery = capitalizeFirstLetterOfEachWord(searchedJob)
        db.collection(JOB_INFO_COLLECTION)
            .orderBy("jobTitle")
            .startAt(normalizedSearchQuery)
            .endAt(normalizedSearchQuery + "\uf8ff")
            .get()
            .addOnSuccessListener  { result ->
                val searchedJobs = result.toObjects(JobInformation::class.java)
                Log.d("debuggingSearch", searchedJobs.toString())
                viewModelScope.launch {
                    _searchedJobs.emit(Resource.Success(searchedJobs, "Get data jobs successfully"))
                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    _searchedJobs.emit(Resource.Failed("Get data jobs failure"))
                }
            }
    }
    fun capitalizeFirstLetterOfEachWord(input: String): String {
        val words = input.split(" ")
        val capitalizedWords = words.map { it.capitalize() }
        return capitalizedWords.joinToString(" ")
    }
}
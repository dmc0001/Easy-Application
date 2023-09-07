package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.User
import com.example.easy.utils.Constants.USER_COLLECTION
import com.example.easy.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class JobDetailsViewModel @Inject constructor(private val db: FirebaseFirestore) : ViewModel() {
    private val _employerInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val employerInfo: StateFlow<Resource<User>> = _employerInfo

    fun fetchEmployerInfo(document: String) {
        _employerInfo.value = Resource.Loading() // Emit loading state

        db.collection(USER_COLLECTION)
            .document(document)
            .get()
            .addOnSuccessListener { result ->
                val employerInfo = result.toObject(User::class.java)
                if (employerInfo != null) {
                    viewModelScope.launch {
                        _employerInfo.value =
                            Resource.Success(employerInfo, "Get employer info successfully")
                    }
                } else {
                    viewModelScope.launch {
                        _employerInfo.value = Resource.Failed("User not found")
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _employerInfo.value = Resource.Failed("Get user data failure")
                }
            }
    }
}

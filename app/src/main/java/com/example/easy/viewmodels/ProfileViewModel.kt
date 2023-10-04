package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.User
import com.example.easy.utils.Constants
import com.example.easy.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _userData = MutableSharedFlow<Resource<User>>()
    val userData: Flow<Resource<User>> = _userData
    init {
        getUser()
    }
    fun getUser() {
        db.collection(Constants.USER_COLLECTION)
            .document(auth.uid!!) // Use the UID as the document ID

            //   data, SetOptions.merge()
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _userData.emit(Resource.Failed(error.message.toString()))
                    }
                } else {
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        viewModelScope.launch {
                            _userData.emit(Resource.Success(user, "Getting data successful"))
                        }

                    }
                }

            }

    }

}
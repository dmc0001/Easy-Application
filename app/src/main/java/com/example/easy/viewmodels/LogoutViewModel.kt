package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(private var auth: FirebaseAuth) : ViewModel() {

    fun logout() {
        auth.signOut()
    }

}
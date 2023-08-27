package com.example.easy.viewmodels.providerfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.easy.data.Category
import com.example.easy.viewmodels.BaseCategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryProviderFactory(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BaseCategoryViewModel(firestore,category) as T
    }
}
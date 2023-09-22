package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.Order
import com.example.easy.utils.Constants
import com.example.easy.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _orderJobs = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val orderJobs: StateFlow<Resource<List<Order>>> = _orderJobs
    init {
        fetchJobsInfoOrders()
    }
    private fun fetchJobsInfoOrders() {
        runBlocking {
            _orderJobs.emit(Resource.Loading())
        }
        db.collection(Constants.USER_COLLECTION).document(auth.uid!!)
            .collection(Constants.ORDER_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                val specialJobOrders = result.toObjects(Order::class.java)
                viewModelScope.launch {

                    _orderJobs.emit(
                        Resource.Success(
                            specialJobOrders,
                            "Get data jobs successfully"
                        )
                    )
                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    _orderJobs.emit(Resource.Failed("Get data jobs failure"))
                }
            }
    }

}
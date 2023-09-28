package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.JobInformation
import com.example.easy.data.Order
import com.example.easy.data.User
import com.example.easy.utils.Constants.ORDER_COLLECTION
import com.example.easy.utils.Constants.USER_COLLECTION
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
    private val db: FirebaseFirestore, private val auth: FirebaseAuth
) : ViewModel() {

    private val _orderJobs = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val orderJobs: StateFlow<Resource<List<Order>>> = _orderJobs

    private val _orderUser = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val orderUser: StateFlow<Resource<Order>> = _orderUser


    init {
        fetchJobsInfoOrders()
    }

    private fun fetchJobsInfoOrders() {
        runBlocking {
            _orderJobs.emit(Resource.Loading())
        }
        db.collection(USER_COLLECTION).document(auth.uid!!).collection(ORDER_COLLECTION).get()
            .addOnSuccessListener { result ->
                val jobOrders = result.toObjects(Order::class.java)
                viewModelScope.launch {

                    _orderJobs.emit(
                        Resource.Success(
                            jobOrders, "Get data jobs successfully"
                        )
                    )

                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    _orderJobs.emit(Resource.Failed("Get data jobs failure"))
                }
            }
    }

    fun getOrder(document: String) {
        db.collection(USER_COLLECTION).document(auth.uid!!)
            .collection(ORDER_COLLECTION).whereEqualTo("jobInformationUid", document)
            .get()
            .addOnSuccessListener {
                val order = it.toObjects(Order::class.java)
                _orderUser.value = Resource.Success(order[0],"Get Order")
            }.addOnFailureListener {
                _orderUser.value = Resource.Failed("Failed Get Order")
            }

    }
}



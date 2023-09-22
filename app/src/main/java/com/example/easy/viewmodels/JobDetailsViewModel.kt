package com.example.easy.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.Order
import com.example.easy.data.User
import com.example.easy.firebase.FirebaseCommon
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
class JobDetailsViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {
    private val _employerInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val employerInfo: StateFlow<Resource<User>> = _employerInfo

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order: StateFlow<Resource<Order>> = _order
    private val _updatedOrder = MutableStateFlow<Resource<Map<String, Any>>>(Resource.Unspecified())
    val updatedOrder: StateFlow<Resource<Map<String, Any>>> = _updatedOrder

    fun fetchEmployerInfo(document: String) {
        runBlocking {
            _employerInfo.value = Resource.Loading()
        }
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

    fun addOrder(cartOrder: Order) {
        runBlocking {
            _order.emit(Resource.Loading())
        }
        Log.d("test", cartOrder.jobInformation.uid!!)
        db.collection(USER_COLLECTION).document(auth.uid!!).collection(ORDER_COLLECTION)
            .whereEqualTo("jobInformation.uid", cartOrder.jobInformation.uid)
            .get()
            .addOnSuccessListener { query ->
                Log.d("yasser", query.isEmpty.toString())
                if (query.isEmpty) {
                    addNewOrder(cartOrder)
                } else {
                    val updatedMap = update(cartOrder)
                    updateOrder(query.documents[0].id, updatedMap)
                }
            }
            .addOnFailureListener { }

    }


    private fun addNewOrder(cartOrder: Order) {
        firebaseCommon.addOrder(cartOrder) { addedOrder, e ->
            viewModelScope.launch {
                if (e == null) {
                    _order.emit(Resource.Success(addedOrder!!, "Sent Order sucessfully"))
                } else {
                    _order.emit(Resource.Failed(e.message.toString()))
                }
            }
        }

    }

    private fun update(updatedOrder: Order): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        if (updatedOrder.description.isNotEmpty()) {
            map["jobInformation"] = updatedOrder.jobInformation
            map["description"] = updatedOrder.description
            map["date"] = updatedOrder.date
            map["location"] = updatedOrder.location

        }
        return map
    }


    private fun updateOrder(orderId: String, updatedOrder: Map<String, Any>) {
        firebaseCommon.updateOrder(orderId, updatedOrder) { updatedOrder, e ->
            viewModelScope.launch {
                if (e == null) {
                    _updatedOrder.emit(
                        Resource.Success(
                            updatedOrder!!,
                            "Order updated successfully"
                        )
                    )
                } else {
                    _updatedOrder.emit(Resource.Failed(e.message.toString()))
                }
            }
        }
    }


}

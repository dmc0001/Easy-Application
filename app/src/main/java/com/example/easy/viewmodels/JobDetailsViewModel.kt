package com.example.easy.viewmodels

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
        db.collection(USER_COLLECTION).document(auth.uid!!).collection(ORDER_COLLECTION)
            .whereEqualTo("JobInformation.id", cartOrder.jobInformation.uid)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    addNewOrder(cartOrder)
                } else {

                   /* val existingOrderDocument = it.documents.first()
                    val existingOrder = existingOrderDocument.toObject(Order::class.java)*/
                  //  if (existingOrder != null) {
                        // Update the quantity of the existing order
                      /*  existingOrder.date = cartOrder.date
                        existingOrder.location = cartOrder.location
                        existingOrder.description = cartOrder.description
                        updateOrder(existingOrderDocument.id, existingOrder)*/

                  //  }
                    /*val order = it.first().toObject(Order::class.java)
                    if (order == cartOrder) {

                    } else {
                        addNewOrder(cartOrder)
                    }*/
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

    private fun updateOrder(orderId: String, updatedOrder: Order) {
        firebaseCommon.updateOrder(orderId, updatedOrder) { updatedOrder, e ->
            viewModelScope.launch {
                if (e == null) {
                    _order.emit(Resource.Success(updatedOrder!!, "Order updated successfully"))
                } else {
                    _order.emit(Resource.Failed(e.message.toString()))
                }
            }
        }
    }


}

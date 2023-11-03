package com.example.easy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.Order
import com.example.easy.utils.Constants.ORDER_COLLECTION
import com.example.easy.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Home2ViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) :
    ViewModel() {
    private val _orderJobs = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val orderJobs: StateFlow<Resource<List<Order>>> = _orderJobs

    fun getAllOrders() {
        db.collectionGroup(ORDER_COLLECTION)
            .whereEqualTo("role", "Client") // Add any additional conditions as needed
            .get()
            .addOnSuccessListener { querySnapshot ->
                val orders = mutableListOf<Order>()

                for (document in querySnapshot.documents) {
                    val order = document.toObject(Order::class.java)
                    if (order != null) {
                        orders.add(order)
                    }
                }

                if (orders.isNotEmpty()) {
                    // Handle the retrieved orders here
                    viewModelScope.launch {
                        _orderJobs.emit(
                            Resource.Success(
                                orders,
                                "Get All Orders"
                            )
                        )
                    }

                } else {
                    // No orders found
                    _orderJobs.value = Resource.Failed("No Orders Found")
                }
            }
            .addOnFailureListener { e ->
                // Handle the error
                _orderJobs.value = Resource.Failed("Failed to fetch Orders: ${e.message}")
            }
    }


    /* fun getOrders() {
         db.collection(USER_COLLECTION)
             .whereEqualTo("role", "Client")
             .whereEqualTo("jobInformationUid", auth.uid)
             .get()
             .addOnSuccessListener { querySnapshot ->
                 val orders = mutableListOf<Order>()

                 for (document in querySnapshot.documents) {
                     val order = document.toObject(Order::class.java)
                     if (order != null) {
                         order.id = document.id // Set the document ID in your Order model
                         orders.add(order)
                     }
                 }

                 if (orders.isNotEmpty()) {
                     // You can loop through all orders and delete them one by one
                     for (order in orders) {
                         db.collection(USER_COLLECTION)
                             .document(auth.uid!!)
                             .collection(ORDER_COLLECTION)
                           //  .document(order.documentId) // Specify the order document ID to delete
                             .delete()
                             .addOnSuccessListener {
                                 // Order deleted successfully, you can handle it here
                                 _orderJobs.value = Resource.Success(order, "Get Order")
                             }
                             .addOnFailureListener { e ->
                                 // Handle the error
                                 _orderJobs.value = Resource.Failed("Failed to delete Order: ${e.message}")
                             }
                     }
                 } else {
                     // No orders found
                     _orderJobs.value = Resource.Success(emptyList(), "No Orders Found")
                 }
             }
             .addOnFailureListener { e ->
                 // Handle the error
                 _orderJobs.value = Resource.Failed("Failed to fetch Orders: ${e.message}")
             }
     }*/

    /* fun getOrders() {
         db.collection(USER_COLLECTION).whereEqualTo("role","Client")
             .collection(ORDER_COLLECTION).whereEqualTo("jobInformationUid",auth.uid)
             .get()
             .addOnSuccessListener { querySnapshot ->
                 val orders = querySnapshot.toObjects(Order::class.java)


                 if (orders.isNotEmpty()) {
                     // Delete the order
                     val deletedOrder = orders[0]
                     db.collection(Constants.USER_COLLECTION).document(auth.uid!!)
                         .collection(Constants.ORDER_COLLECTION).document()
                         .delete()
                         .addOnSuccessListener {
                             // Order deleted successfully, notify the user
                             _orderJobs.value = Resource.Success(deletedOrder, "Get Order")
                         }
                         .addOnFailureListener { e ->
                             // Handle the error
                             _orderJobs.value = Resource.Failed("Failed delete Order")
                         }
                 }
             }

     }*/
}
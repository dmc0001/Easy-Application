package com.example.easy.firebase

import com.example.easy.data.Order
import com.example.easy.utils.Constants.ORDER_COLLECTION
import com.example.easy.utils.Constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val orderCollection = firestore.collection(USER_COLLECTION)
        .document(auth.uid!!)
        .collection(ORDER_COLLECTION)

    fun addOrder(cartOrder: Order, onResult: (Order?, Exception?) -> Unit) {
        orderCollection.document().set(cartOrder).addOnSuccessListener {
            onResult(cartOrder, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun updateOrder(orderId: String, updatedOrder: Order, onResult: (Order?, Exception?) -> Unit) {
        orderCollection.document(orderId).set(updatedOrder)
            .addOnSuccessListener {
                onResult(updatedOrder, null)
            }
            .addOnFailureListener { error ->
                onResult(null, error)
            }
    }
}
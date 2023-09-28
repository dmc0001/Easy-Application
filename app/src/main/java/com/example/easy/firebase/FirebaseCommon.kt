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
        //TODO replace onResult with onSuccess and onFailure
        orderCollection.document().set(cartOrder).addOnSuccessListener {
            onResult(cartOrder, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun updateOrder(
        orderId: String,
        updatedOrder: Map<String, Any>,
        onResult: (Map<String, Any>?, Exception?) -> Unit
    ) {

        firestore.runTransaction { transition ->
            val docRef = orderCollection.document(orderId)

            transition.update(docRef, updatedOrder)

        }
            .addOnSuccessListener {
                onResult(updatedOrder, null)
            }
            .addOnFailureListener { error ->
                onResult(null, error)
            }
    }
}
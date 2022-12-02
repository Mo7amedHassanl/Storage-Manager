package com.storagemanagerapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.storagemanagerapp.model.StorageDataModel

class StorageData {
    private val db = FirebaseFirestore.getInstance()

    fun add(storage: String, item: StorageDataModel) {
        db.collection(storage).document(item.name).set(item)
    }

    fun delete(storage: String, itemName: String) {
        db.collection(storage).document(itemName).delete()
    }

    fun update(storage: String, item: StorageDataModel) {
        val new = mapOf(
            "date" to item.date,
            "incoming" to item.incoming,
            "spent" to item.spent,
            "name" to item.name,
            "supplier" to item.supplier,
            "spender" to item.spender,
            "total" to item.total,
            "storage" to item.storage
        )
        db.collection(storage).document(item.name).set(new)
    }
}
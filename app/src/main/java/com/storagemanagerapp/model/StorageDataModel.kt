package com.storagemanagerapp.model


data class StorageDataModel(
    var date: String = "",
    val name: String = "",
    var incoming: Long = 0,
    var spent: Long = 0,
    var total : Long = incoming - spent,
    var spender : String = "",
    var supplier : String = "",
    val storage : String = ""
)

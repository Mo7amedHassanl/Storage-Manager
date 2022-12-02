package com.storagemanagerapp.model

import androidx.annotation.StringRes

data class StorageModel(
    @StringRes val  name : Int,
    val dataType : Storages,
    @StringRes val storageNum : Int?
)

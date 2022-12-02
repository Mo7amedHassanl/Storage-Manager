package com.storagemanagerapp.data

import com.storagemanagerapp.R
import com.storagemanagerapp.model.StorageModel
import com.storagemanagerapp.model.Storages

object StoragesListData {
    fun getData(): MutableList<StorageModel> {
        return mutableListOf(
            StorageModel(name = R.string.storage1, Storages.Furniture, R.string.storage1_num),
            StorageModel(name = R.string.storage2, Storages.Electric, R.string.storage2_num),
            StorageModel(name = R.string.storage3, Storages.Valuable, R.string.storage3_num),
            StorageModel(name = R.string.storage4, (Storages.Office), R.string.storage4_num),
            StorageModel(name = R.string.storage5, (Storages.Consumed), R.string.storage5_num),
            StorageModel(name = R.string.storage6, (Storages.Stamp), null),
            StorageModel(name = R.string.storage7, (Storages.Lawyer), null),
        )

    }
}
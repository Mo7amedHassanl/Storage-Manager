package com.storagemanagerapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.storagemanagerapp.R
import com.storagemanagerapp.data.StoragesListData
import com.storagemanagerapp.model.StorageDataModel
import com.storagemanagerapp.model.StorageModel
import com.storagemanagerapp.model.Storages
import com.storagemanagerapp.ui.theme.StorageManagerAppTheme
import com.storagemanagerapp.ui.theme.Typography
import kotlinx.coroutines.launch

var lowList = ArrayList<StorageDataModel>()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (st in Storages.values()) {
            db.collection(st.name).whereLessThanOrEqualTo("total", 10)
                .addSnapshotListener { it, _ ->
                    if (it != null) {
                        for (doc in it) {
                            if (doc.toObject(StorageDataModel::class.java) !in lowList) {
                                lowList.add(doc.toObject(StorageDataModel::class.java))
                            }
                        }
                    }
                    if (st == Storages.values().last()) {
                        setContent {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

                                StorageManagerAppTheme {
                                    MainActivityUI(lawList = lowList)
                                }
                            }
                        }
                    }
                }
        }

    }


    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun MainActivityUI(lawList: ArrayList<StorageDataModel>) {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        var items = ""
        lawList.forEach { items += it.name + "\n" }
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = { AppTopBar() },
        ) {
            it
            StoragesList(StoragesListData.getData())
            if (lawList.isNotEmpty()) {
                coroutineScope.launch {
                    coroutineScope.launch {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = "يوجد ${lawList.size} صنف قليل الكمية", actionLabel = "عرض"
                        )
                        when (snackbarResult) {
                            SnackbarResult.Dismissed -> {}
                            SnackbarResult.ActionPerformed -> {
                                val i = Intent(this@MainActivity, LowQuantityActivity::class.java)
                                startActivity(i)
                            }
                        }
                    }
                }
            }

        }


    }


    @Composable
    fun AppTopBar() {
        TopAppBar(
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column() {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = Typography.h1,
                    )
                }
            }
        }
    }

    @Composable
    fun StoragesList(list: List<StorageModel>) {
        val context = LocalContext.current

        LazyColumn(
            modifier = Modifier.wrapContentSize(),
        ) {
            items(list) {
                StorageCard(name = stringResource(id = it.name),
                    storageNumber = it.storageNum,
                    onClick = {
                        val i = Intent(context, StorageActivity::class.java)
                        i.putExtra("title", it.name)
                        i.putExtra("type", it.dataType.name)
                        context.startActivity(i)
                    })
            }
            item {
                StorageCard(
                    name = stringResource(id = R.string.fewTypes),
                    storageNumber = null,
                    onClick = {
                        val i = Intent(context, LowQuantityActivity::class.java)
                        context.startActivity(i)
                    })

            }
        }

    }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun StorageCard(name: String, @StringRes storageNumber: Int?, onClick: () -> Unit) {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(8.dp),
            elevation = 8.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp, end = 16.dp)
            ) {
                if (storageNumber != null) {
                    Text(
                        text = stringResource(id = storageNumber),
                        fontSize = 24.sp,
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = name,
                    fontSize = 26.sp,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentSize(),
                    textAlign = TextAlign.Center,
                )
            }

        }
    }
}

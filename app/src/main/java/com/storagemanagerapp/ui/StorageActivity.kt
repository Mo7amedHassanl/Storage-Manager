@file:OptIn(ExperimentalMaterialApi::class)

package com.storagemanagerapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.storagemanagerapp.R
import com.storagemanagerapp.data.StorageData
import com.storagemanagerapp.model.StorageDataModel
import com.storagemanagerapp.ui.theme.StorageManagerAppTheme
import com.storagemanagerapp.ui.theme.Typography

val db = FirebaseFirestore.getInstance()
var list = ArrayList<StorageDataModel>()

class StorageActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getIntExtra("title", R.string.app_name)
        val storage = intent.getStringExtra("type")!!

        val docRef = db.collection(storage)
        docRef.addSnapshotListener { snapshot, e ->
            list.clear()
            if (snapshot != null) {
                for (doc in snapshot) {
                    list.add(doc.toObject(StorageDataModel::class.java))

                }
            }
            setContent {
                StorageManagerAppTheme() {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        StorageActivityUi(
                            title = title,
                            items = list,
                            onAddClick = {
                                val i = Intent(this, AddItemActivity::class.java)
                                i.putExtra("storage", storage)
                                startActivity(i)
                            },
                            onBackClick = {
                                finish()
                            },
                            storage = storage,

                            )
                    }
                }
            }
        }


    }

    @Composable
    fun StorageActivityUi(
        items: List<StorageDataModel>,
        @StringRes title: Int,
        onAddClick: () -> Unit,
        onBackClick: () -> Unit,
        storage: String,
    ) {
        var searchValue by remember {
            mutableStateOf("")
        }
        val docRef = db.collection(storage)
        val focus = LocalFocusManager.current
        Scaffold(
            topBar = {
                ActivityTopBar(
                    title = title,
                    onAddClick = onAddClick,
                    onBackClick = onBackClick,
                    storage
                )
            })
        {
            it
            Column(Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = searchValue,
                    onValueChange = { value ->
                        searchValue = value
                        if (value.isEmpty()) {
                            docRef.addSnapshotListener { snapshot, e ->
                                list.clear()
                                if (snapshot != null) {
                                    for (doc in snapshot) {
                                        list.add(doc.toObject(StorageDataModel::class.java))

                                    }
                                }
                            }
                        } else {
                            val newList = ArrayList<StorageDataModel>()
                            if (list.isNotEmpty()) {
                                list.forEach {
                                    if (it.name.contains(value) || it.date.contains(value)) {
                                        newList.add(it)
                                    }
                                }
                            }
                            list.clear()
                            list.addAll(newList)
                        }

                    },
                    label = { Text(text = stringResource(id = R.string.search)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
                )
                ItemsList(
                    items = items,
                    storage = storage
                )
            }
        }
    }

    @Composable
    private fun ActivityTopBar(
        @StringRes title: Int,
        onAddClick: () -> Unit,
        onBackClick: () -> Unit,
        storage: String
    ) {
        TopAppBar(
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
                }
                Text(
                    text = stringResource(id = title),
                    style = Typography.h2,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
                IconButton(
                    onClick = {
                        recreate()
                    },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                }
            }
        }
    }

    @Composable
    fun ItemsList(
        items: List<StorageDataModel>,
        storage: String
    ) {
        val context = LocalContext.current
        val i = Intent(context, EditItemActivity::class.java)

        if (items.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(items) {
                    TypeCard(
                        name = it,
                        onItemEditClick =
                        {
                            i.putExtra("storage", storage)
                            i.putExtra("name", it.name)
                            i.putExtra("total", it.total)
                            i.putExtra("output", it.spent)
                            context.startActivity(i)
                        },
                        storage = storage
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_items),
                    textAlign = TextAlign.Center,
                    style = Typography.h2,
                )
            }

        }

    }


    @Composable
    fun TypeCard(name: StorageDataModel, onItemEditClick: () -> Unit, storage: String) {
        var expanded by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .animateContentSize(spring())
                .padding(8.dp),
            elevation = 8.dp,
            backgroundColor = MaterialTheme.colors.surface,
            onClick = { expanded = !expanded }

        ) {
            Column() {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = name.name,
                        style = Typography.h3
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    ExpandButton(
                        expanded = expanded,
                        onclick = {
                            expanded = !expanded
                        })
                }
                if (expanded) {
                    TypeCardInfo(
                        type = name,
                        onDeleteClick = {
                            StorageData().delete(itemName = name.name, storage = storage)

                        },
                        onItemEditClick = onItemEditClick
                    )
                }
            }
        }
    }

    @Composable
    fun TypeCardInfo(
        type: StorageDataModel,
        onItemEditClick: () -> Unit,
        onDeleteClick: () -> Unit
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(start = 12.dp)
        ) {
            Text(
                text = "التاريــــخ:  ${type.date}",
                style = Typography.body1,

                )
            Text(
                text = "الــــــوارد:  ${type.incoming}",
                style = Typography.body1
            )
            Text(
                text = "المـــــورد:  ${type.supplier}",
                style = Typography.body1
            )
            Text(
                text = "المنصرف:  ${type.spent}",
                style = Typography.body1
            )
            Text(
                text = "المنصرف إليه:  ${type.spender}",
                style = Typography.body1
            )
            Text(
                text = "الرصــيــد:  ${type.total}",
                style = Typography.body1
            )
            ItemDeleteEditButtons(
                onEditClick = onItemEditClick,
                onDeleteClick = onDeleteClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    fun ItemDeleteEditButtons(
        onDeleteClick: () -> Unit,
        onEditClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onEditClick) {
                Text(text = stringResource(id = R.string.edit))
            }
            TextButton(onClick = onDeleteClick) {
                Text(text = stringResource(id = R.string.delete))
            }
        }
    }

    @Composable
    fun ExpandButton(
        expanded: Boolean,
        onclick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        IconButton(
            onClick = onclick,
            modifier = modifier.size(50.dp)
        ) {
            Icon(
                imageVector = if (!expanded) Icons.Filled.ExpandMore else Icons.Filled.ExpandLess,
                contentDescription = null
            )
        }
    }



}

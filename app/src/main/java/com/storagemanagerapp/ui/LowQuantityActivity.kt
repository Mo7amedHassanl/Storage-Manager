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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.storagemanagerapp.R
import com.storagemanagerapp.data.StorageData
import com.storagemanagerapp.model.StorageDataModel
import com.storagemanagerapp.model.Storages
import com.storagemanagerapp.ui.theme.StorageManagerAppTheme
import com.storagemanagerapp.ui.theme.Typography

@OptIn(ExperimentalMaterialApi::class)
class LowQuantityActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lowList.clear()
        for (st in Storages.values()) {
            db.collection(st.name).whereLessThanOrEqualTo("total", 10)
                .addSnapshotListener() { it , _ ->
                    if (it != null) {
                        for (doc in it) {
                            if (doc.toObject(StorageDataModel::class.java) !in lowList) {
                                lowList.add(doc.toObject(StorageDataModel::class.java))
                            }
                        }
                    }
                    if (st == Storages.values().last()) {
                        setContent {
                            StorageManagerAppTheme {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                    LowQuantityActivityUi(
                                        items = lowList,
                                        title = R.string.fewTypes,
                                        onBackClick = { finish() },
                                    )
                                }
                            }
                        }
                    }
                }
        }

    }


    @Composable
    fun LowQuantityActivityUi(
        items: List<StorageDataModel>,
        @StringRes title: Int,
        onBackClick: () -> Unit,
    ) {
        Scaffold(
            topBar = {
                FewActivityTopBar(
                    title = title,
                    onBackClick = onBackClick,
                )
            })
        {
            it
            Column(Modifier.fillMaxSize()) {
                ItemsList(
                    items = items,
                )
            }
        }
    }

    @Composable
    private fun FewActivityTopBar(
        @StringRes title: Int,
        onBackClick: () -> Unit,
    ) {
        TopAppBar(
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                //back arrow button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
                }
                //title
                Text(
                    text = stringResource(id = title),
                    style = Typography.h2,
                    modifier = Modifier.weight(1f),
                )
                //refresh button
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
    ) {
        val context = LocalContext.current
        val i = Intent(context, EditItemActivity::class.java)

        if (items.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(items) {
                    TypeCard(
                        item = it,
                        onItemEditClick =
                        {
                            i.putExtra("storage", it.storage)
                            i.putExtra("name", it.name)
                            i.putExtra("total", it.total)
                            i.putExtra("output", it.spent)
                            context.startActivity(i)
                            finish()
                        }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
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
    fun TypeCard(item: StorageDataModel, onItemEditClick: () -> Unit) {
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
                        text = item.name,
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
                        type = item,
                        onDeleteClick = {
                            StorageData().delete(itemName = item.name, storage = item.storage)
                            recreate()
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
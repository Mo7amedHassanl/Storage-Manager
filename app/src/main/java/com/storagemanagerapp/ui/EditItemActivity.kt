package com.storagemanagerapp.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.storagemanagerapp.R
import com.storagemanagerapp.data.StorageData
import com.storagemanagerapp.model.StorageDataModel
import com.storagemanagerapp.ui.theme.StorageManagerAppTheme
import com.storagemanagerapp.ui.theme.Typography

class EditItemActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("name")!!
        val storage = intent.getStringExtra("storage")!!
        val total = intent.getLongExtra("total", 0)
        val output = intent.getLongExtra("output", 0)

        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                StorageManagerAppTheme {
                    EditActivityUi(
                        onBackClick = { finish() },
                        storage = storage,
                        activity = this,
                        currentTotal = total,
                        currentOutput = output,
                        name = name
                    )
                }
            }
        }
    }


    @Composable
    fun EditActivityUi(
        onBackClick: () -> Unit,
        currentTotal: Long,
        currentOutput: Long,
        storage: String,
        activity: Activity,
        name: String
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = { EditActivityTopBar(onBackClick) }) {
            it
            EditActivityContent(
                storage = storage,
                activity = activity,
                currentOutput = currentOutput,
                currentTotal = currentTotal,
                name = name
            )
        }
    }

    @Composable
    fun EditActivityContent(
        activity: Activity, storage: String, currentTotal: Long, currentOutput: Long, name: String
    ) {
        var incoming by remember { mutableStateOf("") }
        var output by remember { mutableStateOf("") }
        var supplier by remember { mutableStateOf("") }
        var getter by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("") }
        val focus = LocalFocusManager.current

        var inp = if (incoming != "") {
            incoming.toLong()
        } else 0
        var outp = if (output != "") {
            output.toLong()
        } else 0
        var getterName = if (getter != "") {
            getter
        } else "لا يوجد"
        var supplierName = if (supplier != "") {
            supplier
        } else "لا يوجد"

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = "اسم الصنف: $name",
                style = Typography.h3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "الرصيد الحالي: $currentTotal",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
            )

            ItemTextField(
                label = R.string.input,
                value = incoming,
                onValueChange = { incoming = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(8.dp))

            ItemTextField(
                label = R.string.supplier,
                value = supplier,
                onValueChange = { supplier = it },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(8.dp))
            ItemTextField(
                label = R.string.output,
                value = output,
                onValueChange = { output = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(16.dp))
            ItemTextField(
                label = R.string.getter,
                value = getter,
                onValueChange = { getter = it },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(16.dp))
            ItemTextField(
                label = R.string.date,
                value = date,
                onValueChange = { date = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
            )

            Spacer(modifier = Modifier.height(16.dp))

            EditSaveItemButton(
                input = inp,
                output = outp,
                storage = storage,
                activity = activity,
                currentName = name,
                getter = getterName,
                supplier = supplierName,
                date = date,
                currentTotal = currentTotal
            )
        }
    }

    @Composable
    fun EditActivityTopBar(onBackClick: () -> Unit) {
        val title = stringResource(id = R.string.edit_item)
        TopAppBar(
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                IconButton(
                    onClick = onBackClick, modifier = Modifier.size(50.dp)
                ) {
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
                }

                Text(
                    text = title,
                    style = Typography.h2,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    @Composable
    fun EditSaveItemButton(
        activity: Activity,
        input: Long,
        output: Long,
        storage: String,
        currentName: String,
        getter: String,
        supplier: String,
        date: String,
        currentTotal: Long
    ) {

        Button(onClick = {
            if (date != "" || storage != "")
                StorageData().update(
                    storage = storage, item = StorageDataModel(
                        date = date,
                        incoming = input,
                        total = currentTotal + input - output,
                        spent = output,
                        name = currentName,
                        supplier = supplier,
                        spender = getter,
                        storage = storage
                    )
                )
            activity.finish()
        }) {
            Text(text = stringResource(id = R.string.save))
        }
    }


}
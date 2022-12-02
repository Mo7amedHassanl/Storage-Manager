package com.storagemanagerapp.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.storagemanagerapp.R
import com.storagemanagerapp.data.StorageData
import com.storagemanagerapp.model.StorageDataModel
import com.storagemanagerapp.ui.theme.StorageManagerAppTheme
import com.storagemanagerapp.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.*

class AddItemActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storage = intent.getStringExtra("storage")
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                StorageManagerAppTheme {
                    ActivityUi(
                        storage = storage!!,
                        onBackClick = {
                            finish()
                        },
                        activity = this
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityUi(onBackClick: () -> Unit, storage: String, activity: Activity) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ActivityTopBar(onBackClick) }
    ) {
        it
        ActivityContent(
            storage = storage,
            activity = activity
        )
    }
}

@Composable
fun ActivityContent(activity: Activity, storage: String) {
    var name by remember { mutableStateOf("") }
    var input by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    var getter by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    val focus = LocalFocusManager.current

    var inp = if (input != "") {
        input.toLong()
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
        ItemTextField(
            label = R.string.item_name,
            value = name,
            onValueChange = { name = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })

        )
        Spacer(modifier = Modifier.height(8.dp))
        ItemTextField(
            label = R.string.input,
            value = input,
            onValueChange = { input = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(modifier = Modifier.height(8.dp))
        ItemTextField(
            label = R.string.supplier,
            value = supplier,
            onValueChange = { supplier = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier = Modifier.height(16.dp))
        ItemTextField(
            label = R.string.output,
            value = output,
            onValueChange = { output = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier = Modifier.height(16.dp))
        ItemTextField(
            label = R.string.getter,
            value = getter,
            onValueChange = { getter = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier = Modifier.height(16.dp))
        ItemTextField(
            label = R.string.date,
            value = date,
            onValueChange = { date = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
        )
        Spacer(modifier = Modifier.height(16.dp))

        SaveItemButton(
            name = name,
            input = inp,
            output = outp,
            supplier = supplier,
            getter = getterName,
            storage = storage,
            activity = activity,
            date = date
        )
    }
}

@Composable
fun SaveItemButton(
    activity: Activity,
    name: String,
    input: Long,
    output: Long,
    storage: String,
    supplier: String,
    getter: String,
    date: String
) {
    Button(
        onClick = {
            if (name != "" || date != "" || storage != "")
                StorageData().add(
                    storage = storage,
                    item = StorageDataModel(
                        date = date,
                        incoming = input,
                        name = name,
                        spent = output,
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


@Composable
private fun ActivityTopBar(onBackClick: () -> Unit) {
    val title = stringResource(id = R.string.add_item)
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
                text = title,
                style = Typography.h2,
                modifier = Modifier.weight(1f),
            )
        }
    }
}


@Composable
fun ItemTextField(
    @StringRes label: Int,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions()
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp, start = 16.dp),
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = stringResource(id = label))
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,

        )

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StorageManagerAppTheme {
    }
}
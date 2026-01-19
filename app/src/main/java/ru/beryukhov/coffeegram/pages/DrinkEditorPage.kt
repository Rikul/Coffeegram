@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.UserCoffeeType
import ru.beryukhov.coffeegram.data.asPrintableText
import ru.beryukhov.coffeegram.data.printableText
import ru.beryukhov.coffeegram.model.DrinksIntent
import ru.beryukhov.coffeegram.model.DrinksStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationStore

@Composable
fun DrinkEditorPage(
    navigationStore: NavigationStore,
    drinksStore: DrinksStore,
    drinkId: String? = null
) {
    val drinksState by drinksStore.state.collectAsState()
    val existingDrink = drinksState.drinks.find { it.dbKey == drinkId }

    // Name state
    var name by remember { mutableStateOf("") }
    val resolvedName = existingDrink?.localizedName?.let { printableText(it) } ?: ""
    LaunchedEffect(resolvedName) {
        if (name.isEmpty() && resolvedName.isNotEmpty()) {
            name = resolvedName
        }
    }

    // Icon state (we select a CoffeeType to use the icon from)
    val initialIconType = existingDrink?.let { drink ->
        CoffeeTypes.entries.find { it.dbKey == drink.iconKey }
    } ?: CoffeeTypes.Cappuccino
    var selectedIconType by remember { mutableStateOf(initialIconType) }
    var price by remember { mutableStateOf(existingDrink?.price?.toString() ?: "") }

    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (drinkId == null) {
                            stringResource(R.string.add_new_drink)
                        } else {
                            stringResource(R.string.edit_drink)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigationStore.newIntent(NavigationIntent.ToManageDrinksPage) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.drink_name)) },
                modifier = Modifier.fillMaxWidth().testTag("DrinkNameInput")
            )
            Spacer(Modifier.height(16.dp))

            // Icon Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = printableText(selectedIconType.localizedName),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.drink_icon)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    leadingIcon = { selectedIconType.icon(Modifier.size(24.dp)) },
                    modifier = Modifier.fillMaxWidth().menuAnchor().testTag("DrinkIconDropdown")
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CoffeeTypes.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(printableText(type.localizedName)) },
                            leadingIcon = { type.icon(Modifier.size(24.dp)) },
                            onClick = {
                                selectedIconType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = price,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.length <= 3)) {
                        price = newValue
                    }
                },
                label = { Text(stringResource(R.string.drink_default_cost)) },
                modifier = Modifier.fillMaxWidth().testTag("DrinkPriceInput"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val newDrink = UserCoffeeType(
                        dbKey = existingDrink?.dbKey ?: "custom_${System.currentTimeMillis()}",
                        localizedName = name.asPrintableText(),
                        icon = selectedIconType.icon,
                        price = price.toIntOrNull()?.coerceIn(0, 999),
                        iconKey = selectedIconType.dbKey
                    )
                    if (existingDrink != null) {
                        drinksStore.newIntent(DrinksIntent.UpdateDrink(newDrink))
                    } else {
                        drinksStore.newIntent(DrinksIntent.AddDrink(newDrink))
                    }
                    navigationStore.newIntent(NavigationIntent.ToManageDrinksPage)
                },
                modifier = Modifier.fillMaxWidth().testTag("SaveDrinkButton"),
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.save))
            }

            if (drinkId != null) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("DeleteDrinkButton")
                ) {
                    Text(stringResource(R.string.delete_drink))
                }
            }
        }
    }

    if (showDeleteDialog && drinkId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_drink_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    drinksStore.newIntent(DrinksIntent.DeleteDrink(drinkId))
                    showDeleteDialog = false
                    navigationStore.newIntent(NavigationIntent.ToManageDrinksPage)
                }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

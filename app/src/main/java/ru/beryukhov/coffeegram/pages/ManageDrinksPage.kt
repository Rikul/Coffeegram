@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import ru.beryukhov.coffeegram.R
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.data.printableText
import ru.beryukhov.coffeegram.model.DrinksStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationStore

@Composable
fun ManageDrinksPage(
    navigationStore: NavigationStore,
    drinksStore: DrinksStore
) {
    val drinksState by drinksStore.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.manage_drinks)) },
                navigationIcon = {
                    IconButton(onClick = { navigationStore.newIntent(NavigationIntent.ToSettingsPage) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigationStore.newIntent(NavigationIntent.ToAddDrinkPage) },
                modifier = Modifier.testTag("AddDrinkButton")
            ) {
                Icon(Icons.Default.Add, stringResource(R.string.add_new_drink))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().testTag("DrinkList"),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(drinksState.drinks) { drink ->
                ListItem(
                    headlineContent = { Text(printableText(drink.localizedName)) },
                    leadingContent = {
                        drink.icon(modifier = Modifier.size(40.dp))
                    },
                    modifier = Modifier.clickable {
                        navigationStore.newIntent(NavigationIntent.ToEditDrinkPage(drink.dbKey))
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

package ru.beryukhov.coffeegram.repository

import repository.DrinksRepository
import repository.model.DbCustomDrink
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.UserCoffeeType
import ru.beryukhov.coffeegram.data.asPrintableText
import ru.beryukhov.coffeegram.model.DrinksState
import ru.beryukhov.coffeegram.store_lib.Storage

class DrinksDbStorage(private val repository: DrinksRepository) : Storage<DrinksState> {

    override suspend fun getState(): DrinksState {
        // Seed default drinks on first run
        if (repository.isEmpty()) {
            seedDefaultDrinks()
        }
        val drinks = repository.getAllDrinks().map { it.toDomain() }
        return DrinksState(drinks = drinks)
    }

    override suspend fun saveState(state: DrinksState) {
        val currentDrinks = repository.getAllDrinks()
        val newDrinkKeys = state.drinks.map { it.dbKey }.toSet()
        val currentDrinkKeys = currentDrinks.map { it.dbKey }.toSet()

        // Delete removed drinks
        (currentDrinkKeys - newDrinkKeys).forEach { key ->
            repository.deleteDrink(key)
        }

        // Insert or update drinks
        state.drinks.forEach { drink ->
            repository.insertDrink(drink.toDb())
        }
    }

    private suspend fun seedDefaultDrinks() {
        CoffeeTypes.entries.forEach { coffeeType ->
            repository.insertDrink(
                DbCustomDrink(
                    dbKey = coffeeType.dbKey,
                    name = coffeeType.name, // Use enum name as default
                    iconKey = coffeeType.dbKey,
                    price = null
                )
            )
        }
    }

    private fun DbCustomDrink.toDomain(): UserCoffeeType {
        val systemDrink = CoffeeTypes.entries.find { it.dbKey == iconKey } ?: CoffeeTypes.Cappuccino
        return UserCoffeeType(
            dbKey = dbKey,
            localizedName = name.asPrintableText(),
            icon = systemDrink.icon,
            price = price,
            iconKey = iconKey
        )
    }

    private fun UserCoffeeType.toDb(): DbCustomDrink {
        val nameString = (localizedName as? ru.beryukhov.coffeegram.data.PrintableText.Raw)?.text
            ?: "Unknown Drink"
        return DbCustomDrink(
            dbKey = dbKey,
            name = nameString,
            iconKey = iconKey,
            price = price
        )
    }
}

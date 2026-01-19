@file:OptIn(ExperimentalResourceApi::class)

package ru.beryukhov.coffeegram.repository

import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.ExperimentalResourceApi
import repository.CoffeeRepository
import repository.DrinksRepository
import repository.model.DbDayCoffee
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.UserCoffeeType
import ru.beryukhov.coffeegram.data.asPrintableText
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.store_lib.Storage

class CoffeeStorage(
    private val repository: CoffeeRepository,
    private val drinksRepository: DrinksRepository
) : Storage<DaysCoffeesState> {
    override suspend fun getState(): DaysCoffeesState {
        // All drinks come from database
        val drinks = drinksRepository.getAllDrinks().map { dbDrink ->
            val iconSource = CoffeeTypes.entries.find { it.dbKey == dbDrink.iconKey }
                ?: CoffeeTypes.Cappuccino
            UserCoffeeType(
                dbKey = dbDrink.dbKey,
                localizedName = dbDrink.name.asPrintableText(),
                icon = iconSource.icon,
                price = dbDrink.price,
                iconKey = dbDrink.iconKey
            )
        }
        return repository.getAll().toState { key ->
            drinks.find { it.dbKey == key }
                ?: CoffeeTypes.Cappuccino // Fallback for orphaned records
        }
    }

    override suspend fun saveState(state: DaysCoffeesState) {
        repository.createOrUpdate(state.coffees.toDaysCoffeesList())
    }
}

fun List<DbDayCoffee>.toState(resolveDrink: (String) -> CoffeeType): DaysCoffeesState {
    val map = mutableMapOf<LocalDate, DayCoffee>()
    this.forEach {
        val date: LocalDate = LocalDate.parse(it.date)
        val coffeeType = resolveDrink(it.coffeeName)
        val dayCoffee1: Map<CoffeeType, Int> = map[date]?.let { mapDate ->
            val dayCoffee: MutableMap<CoffeeType, Int> = mapDate.coffeeCountMap.toMutableMap()
            dayCoffee[coffeeType] = it.count
            dayCoffee
        } ?: mapOf(coffeeType to it.count)
        map[date] = DayCoffee(dayCoffee1)
    }
    return DaysCoffeesState(map)
}

fun Map<LocalDate, DayCoffee>.toDaysCoffeesList(): List<DbDayCoffee> {
    val list = mutableListOf<DbDayCoffee>()
    this.forEach { entry: Map.Entry<LocalDate, DayCoffee> ->
        val date = entry.key.toString()
        val dayCoffee: DayCoffee = entry.value
        dayCoffee.coffeeCountMap.forEach { inner_entry: Map.Entry<CoffeeType, Int> ->
            list.add(
                DbDayCoffee(
                    date = date,
                    coffeeName = inner_entry.key.dbKey,
                    count = inner_entry.value
                )
            )
        }
    }
    return list
}

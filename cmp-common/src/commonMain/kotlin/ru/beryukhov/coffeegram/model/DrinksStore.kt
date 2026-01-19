package ru.beryukhov.coffeegram.model

import ru.beryukhov.coffeegram.data.UserCoffeeType
import ru.beryukhov.coffeegram.store_lib.Storage
import ru.beryukhov.coffeegram.store_lib.StoreImpl

data class DrinksState(
    val drinks: List<UserCoffeeType> = emptyList()
)

sealed interface DrinksIntent {
    data class AddDrink(val drink: UserCoffeeType) : DrinksIntent
    data class UpdateDrink(val drink: UserCoffeeType) : DrinksIntent
    data class DeleteDrink(val drinkKey: String) : DrinksIntent
}

class DrinksStore(storage: Storage<DrinksState>) : StoreImpl<DrinksIntent, DrinksState>(
    initialState = DrinksState(),
    storage = storage
) {
    override fun DrinksState.handleIntent(intent: DrinksIntent): DrinksState {
        return when (intent) {
            is DrinksIntent.AddDrink -> copy(drinks = drinks + intent.drink)
            is DrinksIntent.UpdateDrink -> copy(drinks = drinks.map {
                if (it.dbKey == intent.drink.dbKey) intent.drink else it
            })
            is DrinksIntent.DeleteDrink -> copy(drinks = drinks.filter { it.dbKey != intent.drinkKey })
        }
    }
}

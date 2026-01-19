package repository

import repository.model.DbCustomDrink

interface DrinksRepository {
    suspend fun getAllDrinks(): List<DbCustomDrink>
    suspend fun insertDrink(drink: DbCustomDrink)
    suspend fun deleteDrink(dbKey: String)
    suspend fun isEmpty(): Boolean
}

class InMemoryDrinksRepository : DrinksRepository {
    private val drinks = mutableListOf<DbCustomDrink>()

    override suspend fun getAllDrinks(): List<DbCustomDrink> = drinks.toList()

    override suspend fun insertDrink(drink: DbCustomDrink) {
        drinks.removeAll { it.dbKey == drink.dbKey }
        drinks.add(drink)
    }

    override suspend fun deleteDrink(dbKey: String) {
        drinks.removeAll { it.dbKey == dbKey }
    }

    override suspend fun isEmpty(): Boolean = drinks.isEmpty()
}

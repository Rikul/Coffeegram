package repository

import repository.model.DbCustomDrink
import repository.room.AppDatabase
import repository.room.DrinkDao
import repository.room.DrinkEntity

class RoomDrinksRepository(private val database: AppDatabase) : DrinksRepository {
    private val dao: DrinkDao by lazy {
        database.getDrinkDao()
    }

    override suspend fun getAllDrinks(): List<DbCustomDrink> {
        return dao.getAll().map { it.toDb() }
    }

    override suspend fun insertDrink(drink: DbCustomDrink) {
        dao.insert(drink.toRoom())
    }

    override suspend fun deleteDrink(dbKey: String) {
        dao.delete(dbKey)
    }

    override suspend fun isEmpty(): Boolean {
        return dao.count() == 0
    }

    private fun DrinkEntity.toDb() = DbCustomDrink(
        dbKey = dbKey,
        name = name,
        iconKey = iconKey,
        price = price
    )

    private fun DbCustomDrink.toRoom() = DrinkEntity(
        dbKey = dbKey,
        name = name,
        iconKey = iconKey,
        price = price
    )
}

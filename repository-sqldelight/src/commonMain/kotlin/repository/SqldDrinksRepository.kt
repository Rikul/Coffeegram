package repository

import repository.model.DbCustomDrink
import ru.beryukhov.repository.SqlCustomDrinkQueries

internal class SqldDrinksRepository(private val db: SqlCustomDrinkQueries) : DrinksRepository {

    init {
        db.createSqlCustomDrinkTable()
    }

    override suspend fun getAllDrinks(): List<DbCustomDrink> {
        return db.selectAllCustomDrinks().executeAsList().map {
            DbCustomDrink(
                dbKey = it.dbKey,
                name = it.name,
                iconKey = it.iconKey,
                price = it.price?.toInt()
            )
        }
    }

    override suspend fun insertDrink(drink: DbCustomDrink) {
        db.insertCustomDrink(
            dbKey = drink.dbKey,
            name = drink.name,
            iconKey = drink.iconKey,
            price = drink.price?.toLong()
        )
    }

    override suspend fun deleteDrink(dbKey: String) {
        db.deleteCustomDrink(dbKey)
    }

    override suspend fun isEmpty(): Boolean {
        return db.selectAllCustomDrinks().executeAsList().isEmpty()
    }
}

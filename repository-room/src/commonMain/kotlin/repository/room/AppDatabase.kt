package repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import org.koin.core.module.Module

@Database(
    entities = [DayCoffee::class, DrinkEntity::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): DayCoffeeDao
    abstract fun getDrinkDao(): DrinkDao
}

expect fun roomDriverModule(): Module

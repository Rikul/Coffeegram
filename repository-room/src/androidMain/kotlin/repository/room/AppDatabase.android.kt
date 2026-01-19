package repository.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

actual fun roomDriverModule() = module {
    single<AppDatabase> { getDatabase(get()) }
}

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        // Old migration - kept for users upgrading from v1
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS CustomDrink (" +
                "dbKey TEXT NOT NULL PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "iconKey TEXT NOT NULL, " +
                "price INTEGER)"
        )
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS HiddenDrink (" +
                "dbKey TEXT NOT NULL PRIMARY KEY)"
        )
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        // Create new Drink table
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS Drink (" +
                "dbKey TEXT NOT NULL PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "iconKey TEXT NOT NULL, " +
                "price INTEGER)"
        )
        // Migrate data from CustomDrink to Drink
        connection.execSQL(
            "INSERT OR REPLACE INTO Drink (dbKey, name, iconKey, price) " +
                "SELECT dbKey, name, iconKey, price FROM CustomDrink"
        )
        // Drop old tables
        connection.execSQL("DROP TABLE IF EXISTS CustomDrink")
        connection.execSQL("DROP TABLE IF EXISTS HiddenDrink")
    }
}

private val MIGRATION_1_3 = object : Migration(1, 3) {
    override fun migrate(connection: SQLiteConnection) {
        // Direct migration from v1 to v3
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS Drink (" +
                "dbKey TEXT NOT NULL PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "iconKey TEXT NOT NULL, " +
                "price INTEGER)"
        )
    }
}

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("my_room.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
}

fun getDatabase(ctx: Context): AppDatabase {
    return getDatabaseBuilder(ctx).build()
}

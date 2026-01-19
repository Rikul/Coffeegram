package repository.room

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "Drink")
data class DrinkEntity(
    @PrimaryKey val dbKey: String,
    val name: String,
    val iconKey: String,
    val price: Int?
)

@Dao
interface DrinkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drink: DrinkEntity)

    @Query("DELETE FROM Drink WHERE dbKey = :dbKey")
    suspend fun delete(dbKey: String)

    @Query("SELECT * FROM Drink")
    suspend fun getAll(): List<DrinkEntity>

    @Query("SELECT COUNT(*) FROM Drink")
    suspend fun count(): Int
}

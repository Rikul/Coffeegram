package repository.model

data class DbCustomDrink(
    val dbKey: String,
    val name: String,
    val iconKey: String,
    val price: Int?
)

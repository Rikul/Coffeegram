package ru.beryukhov.repository

import org.koin.dsl.module
import repository.CoffeeRepository
import repository.DrinksRepository
import repository.RoomCoffeeRepository
import repository.RoomDrinksRepository
import repository.room.AppDatabase
import repository.room.roomDriverModule

val databaseModule = module {
    includes(roomDriverModule())
    single<CoffeeRepository> { RoomCoffeeRepository(get()) }
    single<DrinksRepository> { RoomDrinksRepository(get()) }
}

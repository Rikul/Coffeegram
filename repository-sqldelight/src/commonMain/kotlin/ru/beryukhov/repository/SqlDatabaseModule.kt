package ru.beryukhov.repository

import org.koin.core.module.Module
import org.koin.dsl.module
import repository.CoffeeRepository
import repository.DrinksRepository
import repository.SqldCoffeeRepository
import repository.SqldDrinksRepository

expect fun sqlDriverModule(): Module

val databaseModule = module {
    includes(sqlDriverModule())

    single {
        CoffeeDb(get())
    }
    single<SqlDayCoffeeQueries> {
        get<CoffeeDb>().sqlDayCoffeeQueries
    }
    single<SqlCustomDrinkQueries> {
        get<CoffeeDb>().sqlCustomDrinkQueries
    }
    single<CoffeeRepository> { SqldCoffeeRepository(get()) }
    single<DrinksRepository> { SqldDrinksRepository(get()) }
}

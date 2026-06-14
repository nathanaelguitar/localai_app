package com.nathanael.canopy.db

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): CanopyDatabase {
    return CanopyDatabase(driverFactory.createDriver())
}

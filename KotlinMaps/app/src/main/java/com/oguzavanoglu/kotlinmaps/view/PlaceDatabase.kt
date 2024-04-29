package com.oguzavanoglu.kotlinmaps.view

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Place::class], version = 1)
abstract class PlaceDatabase : RoomDatabase() {
    abstract fun PlaceDao(): PlaceDao
}
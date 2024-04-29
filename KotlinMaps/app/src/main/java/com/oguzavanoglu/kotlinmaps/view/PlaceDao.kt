package com.oguzavanoglu.kotlinmaps.view

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PlaceDao {

     @Insert
     fun insert(place : Place) : Completable

     @Query("SELECT * FROM Place")
     fun getAll() : Flowable<List<Place>>

     @Delete
     fun delete(place : Place) : Completable
}
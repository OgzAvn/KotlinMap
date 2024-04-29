package com.oguzavanoglu.kotlinmaps.view

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Place(

    //Bunların hepsi Column ismi olacak

    @ColumnInfo(name = "name")
    var name : String,
    @ColumnInfo(name = "latitude")
    var latitude : Double ,
    @ColumnInfo(name = "longitude")
    var longitude : Double

) : Serializable{

    @PrimaryKey(autoGenerate = true) //İd yi kendi halletsin bizden istmesin diye böyle yaptık
    var id = 0

}
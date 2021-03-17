package com.magda.mamasbiz.main.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "User_Table")
@Parcelize
data class User (
    @PrimaryKey
    val userId: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val dateCreated: String
):Parcelable{}



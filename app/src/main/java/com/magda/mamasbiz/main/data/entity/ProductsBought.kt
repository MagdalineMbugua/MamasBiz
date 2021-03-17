package com.magda.mamasbiz.main.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Products_Bought_Table")
@Parcelize
data class ProductsBought(
    @PrimaryKey(autoGenerate = true)
    val productId :Int?,
    val meatPrice : String,
    val meatQty : String,
    val meatAmt : String,
    val intestinePrice : String,
    val intestineQty : String,
    val intestineAmt : String,
    val africanSausagePrice : String,
    val africanSausageQty : String,
    val africanSausageAmt : String,
    val headAndLegsPrice : String,
    val headAndLegsQty : String,
    val headAndLegsAmt : String,
    val liverPrice : String,
    val liverQty : String,
    val liverAmt : String,
    val filletPrice : String,
    val filletQty : String,
    val filletAmt : String,
    val skinPrice : String,
    val skinQty : String,
    val skinAmt : String


):Parcelable

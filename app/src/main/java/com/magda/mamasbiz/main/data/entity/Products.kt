package com.magda.mamasbiz.main.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
@Parcelize
data class Products(
    var productId :String? = null,
    val meatPrice : String? = null,
    val meatQty : String? = null,
    val meatAmt : String? = null,
    val intestinePrice : String? = null,
    val intestineQty : String? = null,
    val intestineAmt : String? = null,
    val africanSausagePrice : String? = null,
    val africanSausageQty : String? = null,
    val africanSausageAmt : String? = null,
    val headAndLegsPrice : String? = null,
    val headAndLegsQty : String? = null,
    val headAndLegsAmt : String? = null,
    val liverPrice : String? = null,
    val liverQty : String? = null,
    val liverAmt : String? = null,
    val filletPrice : String? = null,
    val filletQty : String? = null,
    val filletAmt : String? = null,
    val skinPrice : String? = null,
    val skinQty : String? = null,
    val skinAmt : String? = null


):Parcelable{}

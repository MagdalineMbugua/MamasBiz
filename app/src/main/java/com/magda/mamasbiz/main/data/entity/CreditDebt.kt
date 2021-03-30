package com.magda.mamasbiz.main.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CreditDebt (
    var creditDebtId: String? = null,
    var userId: String? = null,
    var type : String? = null,
    var name :String? = null,
    var phoneNumber: String? = null,
    var status: String? = null,
    var paymentDate : String? = null,
    var dateCreated : String? = null,
    var totalAmount : String? = null,
    var productId: String ? = null,
    var totalPaid: String? = null,
    var totalBalance: String? = null,

):Parcelable{}

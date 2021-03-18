package com.magda.mamasbiz.main.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Credit_Debt_Table")
@Parcelize
data class CreditDebt(
    @PrimaryKey(autoGenerate = true)
    val creditDebtId: Int,
    val userId: String,
    val type : String,
    val name :String,
    val phoneNumber: String,
    val status: String,
    val paymentDate : String,
    val dateCreated : String,
    val totalAmount : String,
    val productId: String

):Parcelable{}

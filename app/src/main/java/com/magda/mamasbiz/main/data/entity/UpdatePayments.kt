package com.magda.mamasbiz.main.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdatePayments(
    var updateId: String? = null,
    var paymentId : String? = null,
    var amountPaid : String? = null,
    var updateDate : String? = null,
    var updateBalance: String? = null,
    var userId : String? = null

):Parcelable

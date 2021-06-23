package com.magda.mamasbiz.main.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CreditDebt(
    var creditDebtId: String? = null,
    var userId: String? = null,
    var type: String? = null,
    var name: String? = null,
    var phoneNumber: String? = null,
    var status: String? = null,
    var paymentDate: String? = null,
    var dateCreated: String? = null,
    var totalAllAmount: String? = null,
    var totalAllPaid: String? = null,
    var totalAllBalance: String? = null,
    var productId: String? = null,
    var productAmount: String? = null,
    var productPaid: String? = null,
    var productBalance: String? = null,
    var cattleBoughtAmount: String? = null,
    var cattleBoughtPaid: String? = null,
    var cattleBoughtBalance: String? = null,
    var cattleBoughtQty: String? = null
) : Parcelable

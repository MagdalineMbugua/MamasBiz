package com.magda.mamasbiz.main.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Metadata(
    var totalMoneySentPaid : Int = 0,
    var totalMoneySentAmt: Int = 0,
    var totalMoneySentBalance : Int = 0,
    var totalMoneyReceivedPaid : Int = 0,
    var totalMoneyReceivedAmt : Int = 0,
    var totalMoneyReceivedBalance : Int = 0
): Parcelable

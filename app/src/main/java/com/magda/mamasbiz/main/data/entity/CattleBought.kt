package com.magda.mamasbiz.main.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CattleBought(
    private var creditDebtId : String? = null,
    private var cattleBoughtId : String? = null,
    private var cattlePrice : String? = null,
    private var cattleQty : String? = null,
    private var cattleAmt : String? = null


):Parcelable

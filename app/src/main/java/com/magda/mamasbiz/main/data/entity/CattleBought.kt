package com.magda.mamasbiz.main.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CattleBought(
     var creditDebtId : String? = null,
     var cattleBoughtId : String? = null,
     var cattleBoughtType : String? = null,
     var cattlePrice : String? = null,
     var cattleQty : String? = null,
     var cattleAmt : String? = null


):Parcelable

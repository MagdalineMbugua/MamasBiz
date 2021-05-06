package com.magda.mamasbiz.main.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class User (
    var userId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var password: String? = null,
    var dateCreated: String? = null
):Parcelable



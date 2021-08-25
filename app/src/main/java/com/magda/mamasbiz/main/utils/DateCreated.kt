package com.magda.mamasbiz.main.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateCreated {
    companion object{
        fun getDateCreated():String{
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val currentDateTime= LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")
                currentDateTime.format(formatter)


            } else {
                val currentDateTime= Date()
                val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                formatter.format(currentDateTime)

            }
        }
    }

}
package com.example.holapdf

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

object Methods {

    fun formatTimestamp(timestamp : Long ):String{
        val calender = Calendar.getInstance(Locale.ENGLISH)
        calender.timeInMillis = timestamp

        return DateFormat.format("dd/MM/yyyy",calender).toString()
    }
}
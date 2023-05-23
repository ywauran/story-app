package com.ywauran.storyapp.helper

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun changeFormatDate(date: String): String {
    var orderDate: Date? = null
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val dateFormat1 = SimpleDateFormat("dd MMM yyyy")
    val dateFormat2 = SimpleDateFormat("HH:mm")

    try {
        orderDate = sdf.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    val dateString1 = dateFormat1.format(orderDate)
    val dateString2 = dateFormat2.format(orderDate)
    val dateString = "$dateString1  |  $dateString2"

    return dateString
}

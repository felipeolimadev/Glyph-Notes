package com.felipeserver.site.glyphnotes.ui.viewmodel.ui

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date


fun dateFormatter(date: Long): String{
    val instant = Instant.ofEpochMilli(date)
    val zoneDateTime = instant.atZone(ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    val time = formatter.format(zoneDateTime)
    return time
}

fun dateFormatter(date: Date): String {
    return dateFormatter(date.time)
}

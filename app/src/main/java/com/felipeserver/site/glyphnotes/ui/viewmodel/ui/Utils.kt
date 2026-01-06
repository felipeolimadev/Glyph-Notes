package com.felipeserver.site.glyphnotes.ui.viewmodel.ui

import android.text.format.DateUtils
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date


fun dateFormatterShort(date: Long): String {
    val instant = Instant.ofEpochMilli(date)
    val zoneDateTime = instant.atZone(ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    val time = formatter.format(zoneDateTime)
    return time
}

fun dateFormatterRelative(date: Long): String {
    val now = System.currentTimeMillis()
    val relativeDate = DateUtils.getRelativeTimeSpanString(
        date,
        now,
        DateUtils.MINUTE_IN_MILLIS
    ).toString()
    return relativeDate
}

fun dateFormatterShort(date: Date): String {
    return dateFormatterShort(date.time)
}

package com.felipeserver.site.glyphnotes.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val tags: List<String>,
    val category: String, // Added category
    val isPinned: Boolean,
    val creationDate: Date,
    val lastEditDate: Date
)
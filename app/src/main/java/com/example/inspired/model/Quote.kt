package com.example.inspired.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quote")
data class Quote (
    @PrimaryKey val _id: String, @ColumnInfo(name = "quoteText") val quoteText: String?,
    @ColumnInfo (name = "quoteAuthor") val quoteAuthor: String?
    )
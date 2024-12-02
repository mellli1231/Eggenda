package com.example.eggenda.ui.database.entryDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quest_table")
data class QuestEntry(
    @PrimaryKey
    @ColumnInfo(name = "quest_title") // Explicitly specify the column name
    val questTitle: String,

    @ColumnInfo(name = "due_date") // Explicitly specify the column name
    val dueDate: String
)

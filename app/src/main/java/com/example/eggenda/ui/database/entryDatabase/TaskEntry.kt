package com.example.eggenda.ui.database.entryDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_table",
//    foreignKeys = [
//        ForeignKey(
//            entity = QuestEntry::class,
//            parentColumns = ["quest_title"], // Match the column name in QuestEntry
//            childColumns = ["quest_title"], // Match the column name in this table
//            onDelete = ForeignKey.CASCADE // Cascade delete tasks when a quest is deleted
//        )
//    ]
)
data class TaskEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "task_title") // Explicitly specify the column name
    val title: String,

    @ColumnInfo(name = "quest_title", index = true) // Foreign key column
    val questTitle: String = "",

    @ColumnInfo(name = "time_limit") // Explicitly specify the column name
    val timeLimit: String = "",

    @ColumnInfo(name = "details") // Explicitly specify the column name
    val details: String = "",

    @ColumnInfo(name = "attachment_path") // Explicitly specify the column name
    val attachmentPath: String = "",

    @ColumnInfo(name = "end_time") // Explicitly specify the column name
    var endTime: Long = 0L,

    @ColumnInfo(name = "remaining_time") // Explicitly specify the column name
    var remainingTime: Long = 0L,

    @ColumnInfo(name = "is_checked") // Explicitly specify the column name
    var isChecked: Boolean = false
)

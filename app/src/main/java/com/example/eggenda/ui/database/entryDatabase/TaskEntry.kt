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
    var id: Long = 0L, // Primary Key

    @ColumnInfo(name = "quest_title")
    var questTitle: String = "", // New quest title field

    @ColumnInfo(name = "due_date")
    var dueDate: String = "", // New due date field

    @ColumnInfo(name = "timer_started")
    var timerStarted: Boolean = false,  // Task timer started flag

    @ColumnInfo(name = "task_title")
    var title: String = "", // Task title (max 50 characters)

    @ColumnInfo(name = "time_limit")
    var timeLimit: String = "", // Time limit in format HH:MM:SS

    @ColumnInfo(name = "details")
    var details: String = "", // Task details (max 300 characters)

    @ColumnInfo(name = "attachment_path")
    var attachmentPath: String = "", // File path for attachments (e.g., mp4, png, pdf)

    @ColumnInfo(name = "end_time")
    var endTime: Long = 0L,

    @ColumnInfo(name = "remaining_time")
    var remainingTime: Long = 0L, // Remaining time when paused

    @ColumnInfo(name = "is_checked")
    var isChecked: Boolean = false  // Task checkbox state
)

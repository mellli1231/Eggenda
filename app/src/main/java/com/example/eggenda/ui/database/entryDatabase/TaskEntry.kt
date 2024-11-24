package com.example.eggenda.ui.database.entryDatabase

import androidx.room.*
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

// Code from XD's Week 7 Lecture 10: SQLite Database
@TypeConverters(MyConverters::class)
@Entity(tableName = "task_table")
data class TaskEntry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L, // Primary Key

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
    var isChecked: Boolean = false
)

/**
 * Setting up Type converter for ArrayList<LatLng>
 *
 * This class is used to convert the ArrayList<LatLng> to a JSON string and vice versa
 * Used in the EntryInfo class to store the locations of the user in MyRuns4
 */
class MyConverters {
    @TypeConverter
    fun toArrayList(json: String): ArrayList<LatLng> {
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        val array: ArrayList<LatLng> = gson.fromJson(json, listType)
        return array
    }

    @TypeConverter
    fun fromArrayList(array: ArrayList<LatLng>): String {
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        val json: String = gson.toJson(array, listType)
        return json
    }
}
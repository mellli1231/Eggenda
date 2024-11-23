package com.example.eggenda.ui.database.userDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name="username")
    var username: String,

    @ColumnInfo(name="password")
    var password: String,

    @ColumnInfo(name="points")
    var points: Int = 0
)

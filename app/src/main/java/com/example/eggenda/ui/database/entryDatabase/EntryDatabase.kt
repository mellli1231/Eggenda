package com.example.eggenda.ui.database.entryDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskEntry::class], version = 1, exportSchema = false)
abstract class EntryDatabase : RoomDatabase() {
    abstract val entryDatabaseDao: EntryDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: EntryDatabase? = null

        fun getInstance(context: Context): EntryDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        EntryDatabase::class.java,
                        "task_table"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

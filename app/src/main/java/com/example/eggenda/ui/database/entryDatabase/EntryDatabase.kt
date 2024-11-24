package com.example.eggenda.ui.database.entryDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TaskEntry::class], version = 2, exportSchema = false)
abstract class EntryDatabase : RoomDatabase() {
    abstract val entryDatabaseDao: EntryDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: EntryDatabase? = null

        // Migration variable
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add the new column `is_checked` with default value `false`
                db.execSQL("ALTER TABLE task_table ADD COLUMN is_checked INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context): EntryDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        EntryDatabase::class.java,
                        "task_table"
                    ).addMigrations(MIGRATION_1_2).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

package com.example.eggenda.ui.database.entryDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [QuestEntry::class, TaskEntry::class], version = 3, exportSchema = false)
abstract class EntryDatabase : RoomDatabase() {
    abstract val entryDatabaseDao: EntryDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: EntryDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create new quest_table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS quest_table (
                        questTitle TEXT NOT NULL PRIMARY KEY,
                        dueDate TEXT NOT NULL
                    )
                """)

                // Update task_table with questTitle foreign key
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS task_table_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        questTitle TEXT,
                        timeLimit TEXT,
                        details TEXT,
                        isChecked INTEGER NOT NULL DEFAULT 0,
                        timerStarted INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (questTitle) REFERENCES quest_table(questTitle) ON DELETE CASCADE
                    )
                """)
                db.execSQL("INSERT INTO task_table_new SELECT * FROM task_table")
                db.execSQL("DROP TABLE task_table")
                db.execSQL("ALTER TABLE task_table_new RENAME TO task_table")
            }
        }

        fun getInstance(context: Context): EntryDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        EntryDatabase::class.java,
                        "entry_database"
                    ).addMigrations(MIGRATION_2_3).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

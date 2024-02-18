package com.project.expirytracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters


@Database(entities = [DatabaseModel::class],version=5, exportSchema = false)
@TypeConverters(Converters::class)
public abstract class AppDatabase:RoomDatabase() {

    abstract fun databaseDao(): DatabaseDao
    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            INSTANCE?.let {
                return it
            }

            return synchronized(AppDatabase::class.java){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expiry_app_db"
                ).build()
                INSTANCE = instance
                instance
            }

        }
    }
}
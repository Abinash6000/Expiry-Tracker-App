package com.project.expirytracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(databaseModel: DatabaseModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(databaseModelList: List<DatabaseModel>)
    @Query("SELECT * FROM databaseModel")
    suspend fun itemData():List<DatabaseModel>
}
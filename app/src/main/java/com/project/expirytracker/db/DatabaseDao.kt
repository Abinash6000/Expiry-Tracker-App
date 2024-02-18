package com.project.expirytracker.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(databaseModel: DatabaseModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(databaseModelList: List<DatabaseModel>)

    @Query("SELECT * FROM databasemodel WHERE name LIKE :search||'%'")
    suspend fun searchItem(search:String?):List<DatabaseModel>

    @Update
    suspend fun up(item: DatabaseModel)

    @Query("DELETE FROM DatabaseModel WHERE id = :id")
    suspend fun delete(id:Int)

    @Query("SELECT * FROM databaseModel")
    suspend fun itemData():List<DatabaseModel>
}
package com.kekouke.tfsspring.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kekouke.tfsspring.data.local.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAllUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUsers(users: List<UserEntity>)
}
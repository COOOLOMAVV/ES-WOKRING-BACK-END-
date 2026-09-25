package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.UserSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSessionDao {

  @Query("SELECT * FROM user_sessions WHERE isActive = 1 LIMIT 1")
  fun getActiveSession(): Flow<UserSessionEntity?>

  @Query("SELECT * FROM user_sessions WHERE isActive = 1 LIMIT 1")
  suspend fun findActiveSession(): UserSessionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: UserSessionEntity)

  @Query("UPDATE user_sessions SET isActive = 0")
  suspend fun clearActiveSessions()

  @Query("DELETE FROM user_sessions")
  suspend fun clearAll()
}

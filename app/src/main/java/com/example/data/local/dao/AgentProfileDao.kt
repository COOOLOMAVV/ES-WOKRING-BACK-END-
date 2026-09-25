package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AgentProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentProfileDao {

  @Query("SELECT * FROM agent_profile WHERE id = 1 LIMIT 1")
  fun getProfile(): Flow<AgentProfileEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: AgentProfileEntity)

  @Query("SELECT COUNT(*) FROM agent_profile")
  suspend fun getCount(): Int
}

package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

  @Query("SELECT * FROM properties ORDER BY createdAt DESC")
  fun getAllProperties(): Flow<List<PropertyEntity>>

  @Query("SELECT * FROM properties WHERE id = :id LIMIT 1")
  fun getPropertyById(id: String): Flow<PropertyEntity?>

  @Query("SELECT * FROM properties WHERE id = :id LIMIT 1")
  suspend fun findPropertyById(id: String): PropertyEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProperties(properties: List<PropertyEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProperty(property: PropertyEntity)

  @Query("DELETE FROM properties WHERE id = :id")
  suspend fun deletePropertyById(id: String)

  @Query("UPDATE properties SET isFavorite = :isFavorite WHERE id = :id")
  suspend fun updateFavorite(id: String, isFavorite: Boolean)

  @Query("SELECT COUNT(*) FROM properties")
  suspend fun getCount(): Int

  @Query("DELETE FROM properties")
  suspend fun clearAll()
}

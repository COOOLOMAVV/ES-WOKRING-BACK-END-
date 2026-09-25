package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.InquiryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InquiryDao {

  @Query("SELECT * FROM inquiries ORDER BY updatedAt DESC")
  fun getAllInquiries(): Flow<List<InquiryEntity>>

  @Query("SELECT * FROM inquiries WHERE id = :id LIMIT 1")
  suspend fun getInquiryById(id: String): InquiryEntity?

  @Query("SELECT * FROM inquiries WHERE propertyId = :propertyId AND senderName = :senderName LIMIT 1")
  suspend fun findInquiryByPropertyAndSender(propertyId: String, senderName: String): InquiryEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertInquiries(inquiries: List<InquiryEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertInquiry(inquiry: InquiryEntity)

  @Query("UPDATE inquiries SET isUnread = 0 WHERE id = :id")
  suspend fun markAsRead(id: String)

  @Query("SELECT COUNT(*) FROM inquiries")
  suspend fun getCount(): Int

  @Query("DELETE FROM inquiries")
  suspend fun clearAll()
}

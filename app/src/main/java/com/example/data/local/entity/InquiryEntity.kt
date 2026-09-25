package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ChatMessage
import com.example.data.model.Inquiry

@Entity(tableName = "inquiries")
data class InquiryEntity(
  @PrimaryKey val id: String,
  val propertyId: String,
  val propertyTitle: String,
  val propertyAddress: String,
  val senderName: String,
  val senderEmail: String,
  val senderPhone: String,
  val message: String,
  val timeAgo: String,
  val isUnread: Boolean = true,
  val messages: List<ChatMessage> = emptyList(),
  val updatedAt: Long = System.currentTimeMillis()
) {
  fun toDomain(): Inquiry {
    return Inquiry(
      id = id,
      propertyId = propertyId,
      propertyTitle = propertyTitle,
      propertyAddress = propertyAddress,
      senderName = senderName,
      senderEmail = senderEmail,
      senderPhone = senderPhone,
      message = message,
      timeAgo = timeAgo,
      isUnread = isUnread,
      messages = messages
    )
  }

  companion object {
    fun fromDomain(inq: Inquiry, updatedAt: Long = System.currentTimeMillis()): InquiryEntity {
      return InquiryEntity(
        id = inq.id,
        propertyId = inq.propertyId,
        propertyTitle = inq.propertyTitle,
        propertyAddress = inq.propertyAddress,
        senderName = inq.senderName,
        senderEmail = inq.senderEmail,
        senderPhone = inq.senderPhone,
        message = inq.message,
        timeAgo = inq.timeAgo,
        isUnread = inq.isUnread,
        messages = inq.messages,
        updatedAt = updatedAt
      )
    }
  }
}

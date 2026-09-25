package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType

@Entity(tableName = "notifications")
data class NotificationEntity(
  @PrimaryKey val id: String,
  val title: String,
  val message: String,
  val timestampFormatted: String,
  val type: NotificationType,
  val isRead: Boolean = false,
  val targetId: String? = null,
  val timestamp: Long = System.currentTimeMillis()
) {
  fun toDomain(): NotificationItem {
    return NotificationItem(
      id = id,
      title = title,
      message = message,
      timestampFormatted = timestampFormatted,
      type = type,
      isRead = isRead,
      targetId = targetId
    )
  }

  companion object {
    fun fromDomain(item: NotificationItem, timestamp: Long = System.currentTimeMillis()): NotificationEntity {
      return NotificationEntity(
        id = item.id,
        title = item.title,
        message = item.message,
        timestampFormatted = item.timestampFormatted,
        type = item.type,
        isRead = item.isRead,
        targetId = item.targetId,
        timestamp = timestamp
      )
    }
  }
}

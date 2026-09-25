package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AppointmentStatus
import com.example.data.model.ChatMessage
import com.example.data.model.NotificationType
import org.json.JSONArray
import org.json.JSONObject

class Converters {

  @TypeConverter
  fun fromStringList(list: List<String>?): String {
    if (list == null) return "[]"
    val jsonArray = JSONArray()
    for (item in list) {
      jsonArray.put(item)
    }
    return jsonArray.toString()
  }

  @TypeConverter
  fun toStringList(data: String?): List<String> {
    if (data.isNullOrBlank()) return emptyList()
    val list = mutableListOf<String>()
    try {
      val jsonArray = JSONArray(data)
      for (i in 0 until jsonArray.length()) {
        list.add(jsonArray.getString(i))
      }
    } catch (_: Exception) {}
    return list
  }

  @TypeConverter
  fun fromAppointmentStatus(status: AppointmentStatus?): String {
    return status?.name ?: AppointmentStatus.UPCOMING.name
  }

  @TypeConverter
  fun toAppointmentStatus(value: String?): AppointmentStatus {
    return try {
      AppointmentStatus.valueOf(value ?: AppointmentStatus.UPCOMING.name)
    } catch (_: Exception) {
      AppointmentStatus.UPCOMING
    }
  }

  @TypeConverter
  fun fromNotificationType(type: NotificationType?): String {
    return type?.name ?: NotificationType.VIEWING_BOOKED.name
  }

  @TypeConverter
  fun toNotificationType(value: String?): NotificationType {
    return try {
      NotificationType.valueOf(value ?: NotificationType.VIEWING_BOOKED.name)
    } catch (_: Exception) {
      NotificationType.VIEWING_BOOKED
    }
  }

  @TypeConverter
  fun fromChatMessageList(messages: List<ChatMessage>?): String {
    if (messages == null) return "[]"
    val jsonArray = JSONArray()
    for (msg in messages) {
      val obj = JSONObject()
      obj.put("id", msg.id)
      obj.put("sender", msg.sender)
      obj.put("text", msg.text)
      obj.put("time", msg.time)
      obj.put("isFromMe", msg.isFromMe)
      jsonArray.put(obj)
    }
    return jsonArray.toString()
  }

  @TypeConverter
  fun toChatMessageList(data: String?): List<ChatMessage> {
    if (data.isNullOrBlank()) return emptyList()
    val list = mutableListOf<ChatMessage>()
    try {
      val jsonArray = JSONArray(data)
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          ChatMessage(
            id = obj.optString("id"),
            sender = obj.optString("sender"),
            text = obj.optString("text"),
            time = obj.optString("time"),
            isFromMe = obj.optBoolean("isFromMe")
          )
        )
      }
    } catch (_: Exception) {}
    return list
  }
}

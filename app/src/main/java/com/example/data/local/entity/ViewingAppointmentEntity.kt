package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AppointmentStatus
import com.example.data.model.ViewingAppointment

@Entity(tableName = "appointments")
data class ViewingAppointmentEntity(
  @PrimaryKey val id: String,
  val propertyId: String,
  val propertyTitle: String,
  val propertyAddress: String,
  val clientName: String,
  val clientPhone: String,
  val clientEmail: String,
  val date: String,
  val timeSlot: String,
  val type: String = "Viewing",
  val status: AppointmentStatus = AppointmentStatus.UPCOMING,
  val notes: String,
  val timestamp: Long = System.currentTimeMillis()
) {
  fun toDomain(): ViewingAppointment {
    return ViewingAppointment(
      id = id,
      propertyId = propertyId,
      propertyTitle = propertyTitle,
      propertyAddress = propertyAddress,
      clientName = clientName,
      clientPhone = clientPhone,
      clientEmail = clientEmail,
      date = date,
      timeSlot = timeSlot,
      type = type,
      status = status,
      notes = notes,
      timestamp = timestamp
    )
  }

  companion object {
    fun fromDomain(appt: ViewingAppointment): ViewingAppointmentEntity {
      return ViewingAppointmentEntity(
        id = appt.id,
        propertyId = appt.propertyId,
        propertyTitle = appt.propertyTitle,
        propertyAddress = appt.propertyAddress,
        clientName = appt.clientName,
        clientPhone = appt.clientPhone,
        clientEmail = appt.clientEmail,
        date = appt.date,
        timeSlot = appt.timeSlot,
        type = appt.type,
        status = appt.status,
        notes = appt.notes,
        timestamp = appt.timestamp
      )
    }
  }
}

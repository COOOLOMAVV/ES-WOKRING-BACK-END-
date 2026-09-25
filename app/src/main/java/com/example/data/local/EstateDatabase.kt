package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.AgentProfileDao
import com.example.data.local.dao.AppointmentDao
import com.example.data.local.dao.InquiryDao
import com.example.data.local.dao.NotificationDao
import com.example.data.local.dao.PropertyDao
import com.example.data.local.dao.UserSessionDao
import com.example.data.local.entity.AgentProfileEntity
import com.example.data.local.entity.InquiryEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.UserSessionEntity
import com.example.data.local.entity.ViewingAppointmentEntity

@Database(
  entities = [
    PropertyEntity::class,
    ViewingAppointmentEntity::class,
    InquiryEntity::class,
    NotificationEntity::class,
    AgentProfileEntity::class,
    UserSessionEntity::class
  ],
  version = 4,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EstateDatabase : RoomDatabase() {

  abstract fun propertyDao(): PropertyDao
  abstract fun appointmentDao(): AppointmentDao
  abstract fun inquiryDao(): InquiryDao
  abstract fun notificationDao(): NotificationDao
  abstract fun agentProfileDao(): AgentProfileDao
  abstract fun userSessionDao(): UserSessionDao

  companion object {
    @Volatile
    private var INSTANCE: EstateDatabase? = null

    fun getDatabase(context: Context): EstateDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          EstateDatabase::class.java,
          "estateflow.db"
        )
          .fallbackToDestructiveMigration(dropAllTables = true)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}

package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.UserAccount
import com.example.data.model.UserRole

@Entity(tableName = "user_sessions")
data class UserSessionEntity(
  @PrimaryKey
  val id: String,
  val name: String,
  val email: String,
  val phone: String,
  val role: String, // "BUYER" or "AGENT"
  val licenseNo: String = "",
  val agency: String = "",
  val professionalRole: String = "Licensed Agent",
  val isActive: Boolean = true
) {
  fun toDomain(): UserAccount {
    return UserAccount(
      id = id,
      name = name,
      email = email,
      phone = phone,
      role = if (role.equals("AGENT", ignoreCase = true)) UserRole.AGENT else UserRole.BUYER,
      licenseNo = licenseNo,
      agency = agency,
      professionalRole = professionalRole
    )
  }

  companion object {
    fun fromDomain(account: UserAccount): UserSessionEntity {
      return UserSessionEntity(
        id = account.id,
        name = account.name,
        email = account.email,
        phone = account.phone,
        role = account.role.name,
        licenseNo = account.licenseNo,
        agency = account.agency,
        professionalRole = account.professionalRole,
        isActive = true
      )
    }
  }
}

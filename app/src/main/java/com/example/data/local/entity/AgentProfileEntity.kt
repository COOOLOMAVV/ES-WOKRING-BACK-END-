package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AgentProfile

@Entity(tableName = "agent_profile")
data class AgentProfileEntity(
  @PrimaryKey val id: Int = 1,
  val name: String = "Sarah Jenkins",
  val title: String = "Premier Real Estate Broker • Cebu City Properties",
  val licenseNo: String = "PRC-REB-0094821",
  val phone: String = "+63 917 555 0192",
  val email: String = "sarah.jenkins@estateflow.com",
  val rating: Double = 4.9,
  val reviewsCount: Int = 124,
  val totalSalesVolume: String = "₱48.2M",
  val activeListingsCount: Int = 0,
  val bio: String = "Specializing in premier residential condominiums, townhomes, and luxury family estates across Cebu City, Cebu IT Park, and Cebu Business Park."
) {
  fun toDomain(): AgentProfile {
    return AgentProfile(
      name = name,
      title = title,
      licenseNo = licenseNo,
      phone = phone,
      email = email,
      rating = rating,
      reviewsCount = reviewsCount,
      totalSalesVolume = totalSalesVolume,
      activeListingsCount = activeListingsCount,
      bio = bio
    )
  }

  companion object {
    fun fromDomain(profile: AgentProfile, id: Int = 1): AgentProfileEntity {
      return AgentProfileEntity(
        id = id,
        name = profile.name,
        title = profile.title,
        licenseNo = profile.licenseNo,
        phone = profile.phone,
        email = profile.email,
        rating = profile.rating,
        reviewsCount = profile.reviewsCount,
        totalSalesVolume = profile.totalSalesVolume,
        activeListingsCount = profile.activeListingsCount,
        bio = profile.bio
      )
    }
  }
}

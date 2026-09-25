package com.example.data.model

enum class AppointmentStatus {
  UPCOMING,
  COMPLETED,
  PENDING,
  CANCELLED
}

enum class NotificationType {
  VIEWING_BOOKED,
  NEW_INQUIRY,
  PRICE_DROP,
  LISTING_UPDATE,
  SECURITY_ALERT
}

data class Property(
  val id: String,
  val title: String,
  val address: String,
  val cityStateZip: String,
  val price: Long,
  val priceFormatted: String,
  val isRental: Boolean = false,
  val beds: Double,
  val baths: Double,
  val sqft: Int,
  val propertyType: String,
  val description: String,
  val imageResId: Int = 0,
  val mediaUris: List<String> = emptyList(),
  val isFavorite: Boolean = false,
  val status: String = "Active",
  val availableDates: List<String> = listOf("Mon, Oct 14", "Tue, Oct 15", "Wed, Oct 16", "Thu, Oct 17"),
  val availableTimeSlots: List<String> = listOf("10:00 AM", "1:30 PM", "4:00 PM", "5:30 PM"),
  val amenities: List<String> = listOf("Private Rooftop Deck", "Direct Garage Access", "Open-Plan Living", "Central A/C", "Pet Friendly", "Parks & Transit Nearby"),
  val yearBuilt: Int = 2021,
  val agentName: String = "Sarah Jenkins",
  val agentTitle: String = "Premier Real Estate Broker • Cebu City Properties",
  val agentPhone: String = "+63 917 555 0192",
  val agentEmail: String = "sarah.jenkins@estateflow.com"
) {
  fun hasVideo(): Boolean = mediaUris.any { isVideoUri(it) }
  
  companion object {
    fun isVideoUri(uri: String): Boolean {
      val lower = uri.lowercase()
      return lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".mkv") ||
          lower.endsWith(".webm") || lower.endsWith(".3gp") || lower.contains("video")
    }
  }
}

data class ViewingAppointment(
  val id: String,
  val propertyId: String,
  val propertyTitle: String,
  val propertyAddress: String,
  val clientName: String,
  val clientPhone: String = "+1 (555) 012-3456",
  val clientEmail: String = "client@example.com",
  val date: String,
  val timeSlot: String,
  val type: String = "Viewing",
  val status: AppointmentStatus = AppointmentStatus.UPCOMING,
  val notes: String = "Client requested in-person walkthrough and floor plans.",
  val timestamp: Long = System.currentTimeMillis()
)

data class ChatMessage(
  val id: String,
  val sender: String, // "user", "agent", "client"
  val text: String,
  val time: String,
  val isFromMe: Boolean
)

data class Inquiry(
  val id: String,
  val propertyId: String,
  val propertyTitle: String,
  val propertyAddress: String,
  val senderName: String,
  val senderEmail: String,
  val senderPhone: String,
  val message: String,
  val timeAgo: String,
  val isUnread: Boolean = true,
  val messages: List<ChatMessage> = emptyList()
)

data class NotificationItem(
  val id: String,
  val title: String,
  val message: String,
  val timestampFormatted: String,
  val type: NotificationType,
  val isRead: Boolean = false,
  val targetId: String? = null
)

data class AgentProfile(
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
)

enum class UserRole {
  BUYER,
  AGENT
}

data class UserAccount(
  val id: String,
  val name: String,
  val email: String,
  val phone: String = "",
  val role: UserRole = UserRole.BUYER,
  val licenseNo: String = "",
  val agency: String = "",
  val professionalRole: String = "Licensed Agent"
)


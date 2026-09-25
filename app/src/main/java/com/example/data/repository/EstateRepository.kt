package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.local.EstateDatabase
import com.example.data.local.entity.AgentProfileEntity
import com.example.data.local.entity.InquiryEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.UserSessionEntity
import com.example.data.local.entity.ViewingAppointmentEntity
import com.example.data.model.AgentProfile
import com.example.data.model.AppointmentStatus
import com.example.data.model.ChatMessage
import com.example.data.model.Inquiry
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.Property
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.model.ViewingAppointment
import com.example.data.remote.PostgresConfig
import com.example.data.remote.PostgresHealthResponse
import com.example.data.remote.PostgresPreferencesManager
import com.example.data.remote.PostgresSyncService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class EstateRepository private constructor(
  private val database: EstateDatabase,
  private val preferencesManager: PostgresPreferencesManager? = null,
  private val syncService: PostgresSyncService = PostgresSyncService(),
  private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {

  val activeSession: Flow<UserAccount?> = database.userSessionDao().getActiveSession().map { it?.toDomain() }

  val properties: Flow<List<Property>> = database.propertyDao().getAllProperties().map { list ->
    list.map { it.toDomain() }
  }

  val appointments: Flow<List<ViewingAppointment>> = database.appointmentDao().getAllAppointments().map { list ->
    list.map { it.toDomain() }
  }

  val inquiries: Flow<List<Inquiry>> = database.inquiryDao().getAllInquiries().map { list ->
    list.map { it.toDomain() }
  }

  val notifications: Flow<List<NotificationItem>> = database.notificationDao().getAllNotifications().map { list ->
    list.map { it.toDomain() }
  }

  val agentProfile: Flow<AgentProfile> = database.agentProfileDao().getProfile().map {
    it?.toDomain() ?: AgentProfile()
  }

  init {
    coroutineScope.launch {
      // Clear legacy demo properties, appointments, inquiries, notifications if present
      val legacy = database.propertyDao().findPropertyById("prop_townhouse")
      if (legacy != null) {
        database.propertyDao().clearAll()
        database.appointmentDao().clearAll()
        database.inquiryDao().clearAll()
        database.notificationDao().clearAll()
      }
      // Ensure the demo agent account profile is present
      if (database.agentProfileDao().getCount() == 0) {
        database.agentProfileDao().insertOrUpdateProfile(AgentProfileEntity.fromDomain(AgentProfile()))
      }
    }
  }

  fun toggleFavorite(propertyId: String) {
    coroutineScope.launch {
      val current = database.propertyDao().findPropertyById(propertyId)
      if (current != null) {
        database.propertyDao().updateFavorite(propertyId, !current.isFavorite)
      }
    }
  }

  suspend fun bookAppointment(
    propertyId: String,
    date: String,
    timeSlot: String,
    clientName: String,
    clientPhone: String,
    clientEmail: String,
    notes: String
  ): ViewingAppointment = withContext(Dispatchers.IO) {
    val property = database.propertyDao().findPropertyById(propertyId)
    val newAppointment = ViewingAppointment(
      id = UUID.randomUUID().toString(),
      propertyId = propertyId,
      propertyTitle = property?.title ?: "Property Viewing",
      propertyAddress = property?.address ?: "Address",
      clientName = clientName.ifBlank { "You (Verified Buyer)" },
      clientPhone = clientPhone.ifBlank { "+1 (555) 019-8234" },
      clientEmail = clientEmail.ifBlank { "buyer@estateflow.com" },
      date = date,
      timeSlot = timeSlot,
      type = "Viewing",
      status = AppointmentStatus.UPCOMING,
      notes = notes.ifBlank { "Scheduled via Estateflow instant booking." },
      timestamp = System.currentTimeMillis()
    )

    database.appointmentDao().insertAppointment(ViewingAppointmentEntity.fromDomain(newAppointment))

    val newNotification = NotificationItem(
      id = UUID.randomUUID().toString(),
      title = "Viewing Appointment Booked",
      message = "Viewing for ${newAppointment.propertyTitle} confirmed on $date at $timeSlot.",
      timestampFormatted = "Just now",
      type = NotificationType.VIEWING_BOOKED,
      isRead = false,
      targetId = newAppointment.id
    )
    database.notificationDao().insertNotification(NotificationEntity.fromDomain(newNotification))

    triggerBackgroundSync()

    newAppointment
  }

  fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus) {
    coroutineScope.launch {
      database.appointmentDao().updateStatus(appointmentId, newStatus)
    }
  }

  suspend fun sendInquiry(
    propertyId: String,
    messageText: String,
    senderName: String = "You (Prospective Buyer)",
    senderEmail: String = "buyer@estateflow.com",
    senderPhone: String = "+1 (555) 019-8234"
  ): Inquiry = withContext(Dispatchers.IO) {
    val property = database.propertyDao().findPropertyById(propertyId)
    val existingInquiry = database.inquiryDao().findInquiryByPropertyAndSender(propertyId, senderName)

    if (existingInquiry != null) {
      val updatedMessages = existingInquiry.messages + ChatMessage(
        id = UUID.randomUUID().toString(),
        sender = "user",
        text = messageText,
        time = "Just now",
        isFromMe = true
      )
      val updatedInquiry = existingInquiry.copy(
        message = messageText,
        timeAgo = "Just now",
        isUnread = true,
        messages = updatedMessages,
        updatedAt = System.currentTimeMillis()
      )
      database.inquiryDao().insertInquiry(updatedInquiry)
      updatedInquiry.toDomain()
    } else {
      val newInquiry = Inquiry(
        id = UUID.randomUUID().toString(),
        propertyId = propertyId,
        propertyTitle = property?.title ?: "Property Inquiry",
        propertyAddress = property?.address ?: "",
        senderName = senderName,
        senderEmail = senderEmail,
        senderPhone = senderPhone,
        message = messageText,
        timeAgo = "Just now",
        isUnread = true,
        messages = listOf(
          ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "user",
            text = messageText,
            time = "Just now",
            isFromMe = true
          )
        )
      )
      database.inquiryDao().insertInquiry(InquiryEntity.fromDomain(newInquiry))

      val notif = NotificationItem(
        id = UUID.randomUUID().toString(),
        title = "Inquiry Sent to ${property?.agentName ?: "Agent"}",
        message = "Your inquiry regarding ${property?.title ?: "property"} has been delivered.",
        timestampFormatted = "Just now",
        type = NotificationType.NEW_INQUIRY,
        isRead = false,
        targetId = newInquiry.id
      )
      database.notificationDao().insertNotification(NotificationEntity.fromDomain(notif))

      triggerBackgroundSync()

      newInquiry
    }
  }

  suspend fun replyToInquiry(inquiryId: String, replyText: String, isAgent: Boolean = true) = withContext(Dispatchers.IO) {
    val existing = database.inquiryDao().getInquiryById(inquiryId) ?: return@withContext
    val newMsg = ChatMessage(
      id = UUID.randomUUID().toString(),
      sender = if (isAgent) "agent" else "user",
      text = replyText,
      time = "Just now",
      isFromMe = isAgent
    )
    val updated = existing.copy(
      isUnread = !isAgent,
      message = replyText,
      timeAgo = "Just now",
      messages = existing.messages + newMsg,
      updatedAt = System.currentTimeMillis()
    )
    database.inquiryDao().insertInquiry(updated)
  }

  fun markInquiryAsRead(id: String) {
    coroutineScope.launch {
      database.inquiryDao().markAsRead(id)
    }
  }

  fun markNotificationAsRead(id: String) {
    coroutineScope.launch {
      database.notificationDao().markAsRead(id)
    }
  }

  fun markAllNotificationsAsRead() {
    coroutineScope.launch {
      database.notificationDao().markAllAsRead()
    }
  }

  suspend fun addProperty(
    title: String,
    address: String,
    cityStateZip: String,
    price: Long,
    isRental: Boolean,
    beds: Double,
    baths: Double,
    sqft: Int,
    propertyType: String,
    description: String,
    amenities: List<String>,
    mediaUris: List<String> = emptyList()
  ): Property = withContext(Dispatchers.IO) {
    val formattedPrice = if (isRental) "₱${String.format("%,d", price)} / mo" else "₱${String.format("%,d", price)}"
    val newProperty = Property(
      id = UUID.randomUUID().toString(),
      title = title,
      address = address,
      cityStateZip = cityStateZip,
      price = price,
      priceFormatted = formattedPrice,
      isRental = isRental,
      beds = beds,
      baths = baths,
      sqft = sqft,
      propertyType = propertyType,
      description = description,
      imageResId = 0,
      mediaUris = mediaUris,
      isFavorite = false,
      status = "New Listing",
      amenities = if (amenities.isEmpty()) listOf("Modern Design", "High Speed Internet", "Near Transit") else amenities
    )

    database.propertyDao().insertProperty(PropertyEntity.fromDomain(newProperty))

    val notif = NotificationItem(
      id = UUID.randomUUID().toString(),
      title = "New Property Published",
      message = "${newProperty.title} is now live on Estateflow with active viewing bookings.",
      timestampFormatted = "Just now",
      type = NotificationType.LISTING_UPDATE,
      isRead = false,
      targetId = newProperty.id
    )
    database.notificationDao().insertNotification(NotificationEntity.fromDomain(notif))

    triggerBackgroundSync()

    newProperty
  }

  suspend fun updateProperty(property: Property): Property = withContext(Dispatchers.IO) {
    val formattedPrice = if (property.isRental) "₱${String.format("%,d", property.price)} / mo" else "₱${String.format("%,d", property.price)}"
    val updatedProperty = property.copy(priceFormatted = formattedPrice)
    database.propertyDao().insertProperty(PropertyEntity.fromDomain(updatedProperty))

    val notif = NotificationItem(
      id = UUID.randomUUID().toString(),
      title = "Listing Updated",
      message = "${updatedProperty.title} details and media have been updated.",
      timestampFormatted = "Just now",
      type = NotificationType.LISTING_UPDATE,
      isRead = false,
      targetId = updatedProperty.id
    )
    database.notificationDao().insertNotification(NotificationEntity.fromDomain(notif))

    triggerBackgroundSync()

    updatedProperty
  }

  suspend fun deleteProperty(propertyId: String): Boolean = withContext(Dispatchers.IO) {
    val prop = database.propertyDao().findPropertyById(propertyId)
    database.propertyDao().deletePropertyById(propertyId)

    val notif = NotificationItem(
      id = UUID.randomUUID().toString(),
      title = "Listing Removed",
      message = "${prop?.title ?: "Property"} has been removed from your active listings.",
      timestampFormatted = "Just now",
      type = NotificationType.LISTING_UPDATE,
      isRead = false,
      targetId = propertyId
    )
    database.notificationDao().insertNotification(NotificationEntity.fromDomain(notif))

    triggerBackgroundSync()

    true
  }

  suspend fun loginClient(email: String, name: String = "", phone: String = ""): UserAccount = withContext(Dispatchers.IO) {
    database.userSessionDao().clearActiveSessions()
    val cleanEmail = email.trim()
    val clientName = when {
      name.isNotBlank() -> name.trim()
      cleanEmail.contains("@") -> cleanEmail.substringBefore("@").replace(".", " ").split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
      else -> "Verified Buyer"
    }
    val clientAccount = UserAccount(
      id = "client_${UUID.randomUUID().toString().take(8)}",
      name = clientName,
      email = cleanEmail.ifBlank { "buyer@estateflow.com" },
      phone = phone.ifBlank { "+1 (555) 019-8234" },
      role = UserRole.BUYER
    )
    database.userSessionDao().insertSession(UserSessionEntity.fromDomain(clientAccount))
    clientAccount
  }

  suspend fun signUpClient(name: String, email: String, phone: String): UserAccount = withContext(Dispatchers.IO) {
    loginClient(email = email, name = name, phone = phone)
  }

  suspend fun loginAgent(emailOrLicense: String, password: String): Result<UserAccount> = withContext(Dispatchers.IO) {
    val input = emailOrLicense.trim()
    if (input.isBlank()) {
      return@withContext Result.failure(Exception("Please enter your agent email or MLS license number."))
    }
    if (password.length < 4) {
      return@withContext Result.failure(Exception("Password must be at least 4 characters."))
    }

    val agent = database.agentProfileDao().getProfile().firstOrNull()?.toDomain() ?: AgentProfile()
    val agentName = if (input.contains("@")) agent.name else agent.name

    database.userSessionDao().clearActiveSessions()
    val agentAccount = UserAccount(
      id = "agent_verified_sarah",
      name = agentName,
      email = if (input.contains("@")) input else agent.email,
      phone = agent.phone,
      role = UserRole.AGENT,
      licenseNo = if (!input.contains("@") && input.isNotBlank()) input else agent.licenseNo,
      agency = "Estateflow Luxury Realty",
      professionalRole = "Licensed Real Estate Agent"
    )
    database.userSessionDao().insertSession(UserSessionEntity.fromDomain(agentAccount))
    Result.success(agentAccount)
  }

  suspend fun signUpAgent(
    name: String,
    email: String,
    licenseNo: String,
    agency: String,
    professionalRole: String,
    password: String
  ): Result<UserAccount> = withContext(Dispatchers.IO) {
    val cleanName = name.trim()
    val cleanEmail = email.trim()
    val cleanLicense = licenseNo.trim()
    val cleanAgency = agency.trim().ifBlank { "Estateflow Luxury Realty" }
    val cleanRole = professionalRole.trim().ifBlank { "Licensed Real Estate Agent" }

    if (cleanName.isBlank()) return@withContext Result.failure(Exception("Please enter your legal name."))
    if (cleanEmail.isBlank()) return@withContext Result.failure(Exception("Please enter your agent email."))
    if (cleanLicense.isBlank()) return@withContext Result.failure(Exception("Please enter your MLS / Real Estate License number."))
    if (password.length < 4) return@withContext Result.failure(Exception("Password must be at least 4 characters."))

    database.userSessionDao().clearActiveSessions()
    val agentAccount = UserAccount(
      id = "agent_${UUID.randomUUID().toString().take(8)}",
      name = cleanName,
      email = cleanEmail,
      phone = "+1 (604) 555-0192",
      role = UserRole.AGENT,
      licenseNo = cleanLicense,
      agency = cleanAgency,
      professionalRole = cleanRole
    )
    database.userSessionDao().insertSession(UserSessionEntity.fromDomain(agentAccount))

    val currentProfile = database.agentProfileDao().getProfile().firstOrNull()?.toDomain() ?: AgentProfile()
    val updatedProfile = currentProfile.copy(
      name = cleanName,
      email = cleanEmail,
      licenseNo = cleanLicense,
      title = "$cleanRole • $cleanAgency"
    )
    database.agentProfileDao().insertOrUpdateProfile(AgentProfileEntity.fromDomain(updatedProfile))

    Result.success(agentAccount)
  }

  suspend fun logout() = withContext(Dispatchers.IO) {
    database.userSessionDao().clearActiveSessions()
  }

  suspend fun resetDatabaseToDefaults() = withContext(Dispatchers.IO) {
    database.userSessionDao().clearAll()
    database.propertyDao().clearAll()
    database.appointmentDao().clearAll()
    database.inquiryDao().clearAll()
    database.notificationDao().clearAll()
    seedDatabase()
  }

  suspend fun addNotification(
    title: String,
    message: String,
    type: NotificationType,
    targetId: String? = null
  ) = withContext(Dispatchers.IO) {
    val notif = NotificationItem(
      id = UUID.randomUUID().toString(),
      title = title,
      message = message,
      timestampFormatted = "Just now",
      type = type,
      isRead = false,
      targetId = targetId
    )
    database.notificationDao().insertNotification(NotificationEntity.fromDomain(notif))
  }

  suspend fun getDatabaseStats(): DatabaseStats = withContext(Dispatchers.IO) {
    DatabaseStats(
      propertiesCount = database.propertyDao().getCount(),
      appointmentsCount = database.appointmentDao().getCount(),
      inquiriesCount = database.inquiryDao().getCount(),
      notificationsCount = database.notificationDao().getCount()
    )
  }

  private suspend fun seedDatabase() = withContext(Dispatchers.IO) {
    database.agentProfileDao().insertOrUpdateProfile(AgentProfileEntity.fromDomain(AgentProfile()))
  }

  fun getPostgresConfig(): PostgresConfig {
    return preferencesManager?.loadConfig() ?: PostgresConfig()
  }

  fun savePostgresConfig(config: PostgresConfig) {
    preferencesManager?.saveConfig(config)
  }

  fun setPresentationLocked(locked: Boolean) {
    preferencesManager?.setPresentationLocked(locked)
  }

  suspend fun testPostgresConnection(config: PostgresConfig): Result<PostgresHealthResponse> {
    return syncService.testConnection(config)
  }

  suspend fun syncPushToPostgres(config: PostgresConfig): Result<String> = withContext(Dispatchers.IO) {
    val props = database.propertyDao().getAllProperties().firstOrNull()?.map { it.toDomain() } ?: emptyList()
    val appts = database.appointmentDao().getAllAppointments().firstOrNull()?.map { it.toDomain() } ?: emptyList()
    val inqs = database.inquiryDao().getAllInquiries().firstOrNull()?.map { it.toDomain() } ?: emptyList()
    val notifs = database.notificationDao().getAllNotifications().firstOrNull()?.map { it.toDomain() } ?: emptyList()

    syncService.pushDataToPostgres(config, props, appts, inqs, notifs)
  }

  suspend fun syncPullFromPostgres(config: PostgresConfig): Result<Int> = withContext(Dispatchers.IO) {
    val pullResult = syncService.pullDataFromPostgres(config)
    if (pullResult.isFailure) {
      return@withContext Result.failure(pullResult.exceptionOrNull() ?: Exception("Unknown error pulling data"))
    }
    val snapshot = pullResult.getOrThrow()
    for (p in snapshot.properties) {
      database.propertyDao().insertProperty(PropertyEntity.fromDomain(p))
    }
    for (a in snapshot.appointments) {
      database.appointmentDao().insertAppointment(ViewingAppointmentEntity.fromDomain(a))
    }
    for (inq in snapshot.inquiries) {
      database.inquiryDao().insertInquiry(InquiryEntity.fromDomain(inq))
    }
    for (n in snapshot.notifications) {
      database.notificationDao().insertNotification(NotificationEntity.fromDomain(n))
    }
    val count = snapshot.properties.size + snapshot.appointments.size + snapshot.inquiries.size
    Result.success(count)
  }

  fun triggerBackgroundSync() {
    coroutineScope.launch {
      try {
        val config = getPostgresConfig()
        if (config.host.isNotBlank()) {
          syncPushToPostgres(config)
        }
      } catch (_: Exception) {
        // Non-blocking background sync
      }
    }
  }

  fun getSqlSchema(context: Context): String {
    return syncService.loadSqlSchemaAsset(context)
  }

  companion object {
    @Volatile
    private var INSTANCE: EstateRepository? = null

    fun getInstance(context: Context): EstateRepository {
      return INSTANCE ?: synchronized(this) {
        val database = EstateDatabase.getDatabase(context)
        val prefs = PostgresPreferencesManager(context.applicationContext)
        val instance = EstateRepository(database, prefs)
        INSTANCE = instance
        instance
      }
    }

    fun createForTesting(database: EstateDatabase, scope: CoroutineScope): EstateRepository {
      return EstateRepository(database, null, PostgresSyncService(), scope)
    }
  }
}

data class DatabaseStats(
  val propertiesCount: Int,
  val appointmentsCount: Int,
  val inquiriesCount: Int,
  val notificationsCount: Int
)

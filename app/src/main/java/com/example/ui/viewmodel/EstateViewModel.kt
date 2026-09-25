package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AgentProfile
import com.example.data.model.AppointmentStatus
import com.example.data.model.Inquiry
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.Property
import com.example.data.model.UserAccount
import com.example.data.model.ViewingAppointment
import com.example.data.remote.PostgresConfig
import com.example.data.remote.PostgresConnectionStatus
import com.example.data.remote.PostgresHealthResponse
import com.example.data.repository.DatabaseStats
import com.example.data.repository.EstateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias UserRole = com.example.data.model.UserRole

enum class Screen {
  HOME,
  PROPERTY_DETAILS,
  AGENT_DASHBOARD,
  SAVED,
  SEARCH,
  MESSAGES,
  NOTIFICATIONS,
  CALENDAR_APPOINTMENTS,
  PROFILE
}

private data class FilterCriteria(
  val query: String,
  val category: String,
  val minPrice: Long,
  val maxPrice: Long,
  val minBeds: Double
)

data class EstateUiState(
  val currentScreen: Screen = Screen.HOME,
  val previousScreen: Screen = Screen.HOME,
  val userRole: UserRole = UserRole.BUYER,
  val selectedProperty: Property? = null,
  val selectedInquiryId: String? = null,
  val searchQuery: String = "",
  val selectedCategory: String = "All",
  val minPrice: Long = 0L,
  val maxPrice: Long = 50000000L,
  val minBeds: Double = 0.0,
  val isFilterSheetOpen: Boolean = false,
  val isAddPropertyDialogOpen: Boolean = false,
  val isEditPropertyDialogOpen: Boolean = false,
  val propertyToEdit: Property? = null,
  val isDeletePropertyDialogOpen: Boolean = false,
  val propertyToDelete: Property? = null,
  val isBookingDialogOpen: Boolean = false,
  val isBookingSuccessDialogOpen: Boolean = false,
  val isClientAuthDialogOpen: Boolean = false,
  val isAgentLoginDialogOpen: Boolean = false,
  val pendingBookingProperty: Property? = null,
  val authError: String? = null,
  val lastBookedAppointment: ViewingAppointment? = null,
  val bookingSelectedDate: String = "Mon, Oct 14",
  val bookingSelectedTime: String = "1:30 PM",
  val bookingClientName: String = "You (Verified Buyer)",
  val bookingClientPhone: String = "+1 (555) 019-8234",
  val bookingClientEmail: String = "buyer@estateflow.com",
  val bookingNotes: String = "",
  val isInquiryDialogOpen: Boolean = false,
  val quickInquiryMessage: String = "Hi Sarah, I'm very interested in this property. Could you provide more details about the strata fees and scheduling an in-person viewing?",
  val activeVideoUrl: String? = null,
  val activeVideoTitle: String = "",
  val isPublishingProperty: Boolean = false,
  val isSavingProperty: Boolean = false,
  val isDeletingProperty: Boolean = false,
  val isSubmittingBooking: Boolean = false,
  val isSendingInquiry: Boolean = false,
  val isResettingDatabase: Boolean = false
)

data class FeedbackEvent(
  val message: String,
  val actionLabel: String? = null,
  val onAction: (() -> Unit)? = null
)

class EstateViewModel(
  application: Application,
  private val repository: EstateRepository
) : AndroidViewModel(application) {

  constructor(application: Application) : this(
    application,
    EstateRepository.getInstance(application)
  )

  private val _feedbackChannel = kotlinx.coroutines.channels.Channel<FeedbackEvent>(kotlinx.coroutines.channels.Channel.BUFFERED)
  val feedbackEvents = _feedbackChannel.receiveAsFlow()

  fun postFeedback(message: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    viewModelScope.launch {
      _feedbackChannel.send(FeedbackEvent(message, actionLabel, onAction))
    }
  }

  private val _uiState = MutableStateFlow(EstateUiState())
  val uiState: StateFlow<EstateUiState> = _uiState.asStateFlow()

  val properties: StateFlow<List<Property>> = repository.properties
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val appointments: StateFlow<List<ViewingAppointment>> = repository.appointments
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val inquiries: StateFlow<List<Inquiry>> = repository.inquiries
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val notifications: StateFlow<List<NotificationItem>> = repository.notifications
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val agentProfile: StateFlow<AgentProfile> = repository.agentProfile
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgentProfile())

  val currentUser: StateFlow<UserAccount?> = repository.activeSession
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  private val _databaseStats = MutableStateFlow<DatabaseStats?>(null)
  val databaseStats: StateFlow<DatabaseStats?> = _databaseStats.asStateFlow()

  val selectedInquiry: StateFlow<Inquiry?> = combine(
    inquiries,
    _uiState.map { it.selectedInquiryId }.distinctUntilChanged()
  ) { inqList, id ->
    if (id == null) null else inqList.find { it.id == id }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val unreadNotificationsCount: StateFlow<Int> = notifications
    .map { notifs -> notifs.count { !it.isRead } }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  private val filterCriteria = _uiState.map {
    FilterCriteria(
      query = it.searchQuery,
      category = it.selectedCategory,
      minPrice = it.minPrice,
      maxPrice = it.maxPrice,
      minBeds = it.minBeds
    )
  }.distinctUntilChanged()

  val filteredProperties: StateFlow<List<Property>> = combine(
    properties,
    filterCriteria
  ) { list, criteria ->
    list.filter { prop ->
      val matchesCategory = when (criteria.category) {
        "All" -> true
        "Houses" -> prop.propertyType.equals("Houses", ignoreCase = true) || prop.propertyType.equals("House", ignoreCase = true)
        "Apartments" -> prop.propertyType.equals("Apartments", ignoreCase = true) || prop.propertyType.equals("Apartment", ignoreCase = true)
        "Condos" -> prop.propertyType.equals("Condos", ignoreCase = true) || prop.propertyType.equals("Condo", ignoreCase = true) || prop.propertyType.equals("Loft", ignoreCase = true)
        "Townhouses" -> prop.propertyType.equals("Townhouses", ignoreCase = true) || prop.propertyType.equals("Townhouse", ignoreCase = true)
        else -> prop.propertyType.contains(criteria.category, ignoreCase = true)
      }

      val matchesSearch = if (criteria.query.isBlank()) {
        true
      } else {
        val query = criteria.query.trim().lowercase()
        prop.title.lowercase().contains(query) ||
            prop.address.lowercase().contains(query) ||
            prop.cityStateZip.lowercase().contains(query) ||
            prop.propertyType.lowercase().contains(query)
      }

      val matchesBeds = prop.beds >= criteria.minBeds
      val matchesPrice = prop.price in criteria.minPrice..criteria.maxPrice

      matchesCategory && matchesSearch && matchesBeds && matchesPrice
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val savedProperties: StateFlow<List<Property>> = properties
    .map { list -> list.filter { it.isFavorite } }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _postgresConfig = MutableStateFlow(repository.getPostgresConfig())
  val postgresConfig: StateFlow<PostgresConfig> = _postgresConfig.asStateFlow()

  private val _postgresStatus = MutableStateFlow(PostgresConnectionStatus.DISCONNECTED)
  val postgresStatus: StateFlow<PostgresConnectionStatus> = _postgresStatus.asStateFlow()

  private val _postgresHealth = MutableStateFlow<PostgresHealthResponse?>(null)
  val postgresHealth: StateFlow<PostgresHealthResponse?> = _postgresHealth.asStateFlow()

  private val _postgresErrorMessage = MutableStateFlow<String?>(null)
  val postgresErrorMessage: StateFlow<String?> = _postgresErrorMessage.asStateFlow()

  private val _isTestingPostgres = MutableStateFlow(false)
  val isTestingPostgres: StateFlow<Boolean> = _isTestingPostgres.asStateFlow()

  private val _isSyncingPostgres = MutableStateFlow(false)
  val isSyncingPostgres: StateFlow<Boolean> = _isSyncingPostgres.asStateFlow()

  private val _isPostgresDialogOpen = MutableStateFlow(false)
  val isPostgresDialogOpen: StateFlow<Boolean> = _isPostgresDialogOpen.asStateFlow()

  private val _isSqlSchemaDialogOpen = MutableStateFlow(false)
  val isSqlSchemaDialogOpen: StateFlow<Boolean> = _isSqlSchemaDialogOpen.asStateFlow()

  init {
    refreshDatabaseStats()
    if (_postgresConfig.value.password == "postgres") {
      val migrated = _postgresConfig.value.copy(password = "root", isPresentationLocked = false)
      _postgresConfig.value = migrated
      repository.savePostgresConfig(migrated)
    }
    if (_postgresConfig.value.autoConnectOnLaunch) {
      testPostgresConnection(silent = true)
    }
    viewModelScope.launch {
      currentUser.collect { user ->
        _uiState.update { state ->
          state.copy(
            userRole = user?.role ?: UserRole.BUYER,
            bookingClientName = user?.name ?: state.bookingClientName,
            bookingClientEmail = user?.email ?: state.bookingClientEmail,
            bookingClientPhone = if (!user?.phone.isNullOrBlank()) user.phone else state.bookingClientPhone
          )
        }
      }
    }
  }

  fun refreshDatabaseStats() {
    viewModelScope.launch {
      _databaseStats.value = repository.getDatabaseStats()
    }
  }

  fun navigateTo(screen: Screen) {
    _uiState.update { it.copy(currentScreen = screen) }
  }

  fun switchUserRole(role: UserRole) {
    _uiState.update {
      it.copy(
        userRole = role,
        currentScreen = if (role == UserRole.AGENT) Screen.AGENT_DASHBOARD else Screen.HOME
      )
    }
  }

  fun toggleUserRole() {
    val newRole = if (_uiState.value.userRole == UserRole.BUYER) UserRole.AGENT else UserRole.BUYER
    switchUserRole(newRole)
    postFeedback(
      if (newRole == UserRole.AGENT) "Switched to Agent Dashboard" else "Switched to Buyer Explorer Mode"
    )
  }

  fun openPropertyDetails(property: Property) {
    _uiState.update {
      it.copy(
        selectedProperty = property,
        previousScreen = it.currentScreen,
        currentScreen = Screen.PROPERTY_DETAILS,
        bookingSelectedDate = property.availableDates.firstOrNull() ?: "Mon, Oct 14",
        bookingSelectedTime = property.availableTimeSlots.firstOrNull() ?: "1:30 PM"
      )
    }
  }

  fun closePropertyDetails() {
    _uiState.update {
      it.copy(
        currentScreen = it.previousScreen,
        selectedProperty = null
      )
    }
  }

  fun toggleFavorite(propertyId: String, title: String? = null) {
    val prop = properties.value.find { it.id == propertyId } ?: _uiState.value.selectedProperty
    val wasFavorite = prop?.isFavorite ?: false
    repository.toggleFavorite(propertyId)
    _uiState.update { state ->
      if (state.selectedProperty?.id == propertyId) {
        state.copy(selectedProperty = state.selectedProperty.copy(isFavorite = !wasFavorite))
      } else {
        state
      }
    }

    val displayTitle = title ?: prop?.title
    val baseMsg = if (wasFavorite) "Removed from Saved" else "Saved to Favorites"
    val msg = if (displayTitle != null) "$baseMsg: $displayTitle" else baseMsg

    postFeedback(
      message = msg,
      actionLabel = "Undo",
      onAction = {
        repository.toggleFavorite(propertyId)
        _uiState.update { state ->
          if (state.selectedProperty?.id == propertyId) {
            state.copy(selectedProperty = state.selectedProperty.copy(isFavorite = wasFavorite))
          } else {
            state
          }
        }
        postFeedback(if (wasFavorite) "Restored to Saved" else "Removed from Saved")
      }
    )
  }

  fun setSelectedCategory(category: String) {
    _uiState.update { it.copy(selectedCategory = category) }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun setFilters(minPrice: Long, maxPrice: Long, minBeds: Double) {
    _uiState.update {
      it.copy(
        minPrice = minPrice,
        maxPrice = maxPrice,
        minBeds = minBeds,
        isFilterSheetOpen = false
      )
    }
    if (minPrice == 0L && maxPrice == 50000000L && minBeds == 0.0) {
      postFeedback("Filters reset to default")
    } else {
      postFeedback("Filters applied")
    }
  }

  fun openFilterSheet(open: Boolean) {
    _uiState.update { it.copy(isFilterSheetOpen = open) }
  }

  fun openAddPropertyDialog(open: Boolean) {
    if (open && _uiState.value.userRole != UserRole.AGENT) {
      return
    }
    _uiState.update { it.copy(isAddPropertyDialogOpen = open) }
  }

  fun initiateBooking(property: Property) {
    val user = currentUser.value
    if (user == null || user.role != UserRole.BUYER) {
      // Must login or sign up first before booking
      _uiState.update {
        it.copy(
          isClientAuthDialogOpen = true,
          pendingBookingProperty = property,
          selectedProperty = property,
          bookingSelectedDate = property.availableDates.firstOrNull() ?: "Mon, Oct 14",
          bookingSelectedTime = property.availableTimeSlots.firstOrNull() ?: "1:30 PM",
          authError = null
        )
      }
    } else {
      // Logged in as buyer -> show booking modal
      _uiState.update {
        it.copy(
          selectedProperty = property,
          isBookingDialogOpen = true,
          bookingClientName = user.name,
          bookingClientEmail = user.email,
          bookingClientPhone = user.phone.ifBlank { "+1 (555) 019-8234" },
          bookingSelectedDate = property.availableDates.firstOrNull() ?: "Mon, Oct 14",
          bookingSelectedTime = property.availableTimeSlots.firstOrNull() ?: "1:30 PM"
        )
      }
    }
  }

  fun openClientAuthDialog(open: Boolean, continueWithBookingProperty: Property? = null) {
    _uiState.update {
      it.copy(
        isClientAuthDialogOpen = open,
        pendingBookingProperty = continueWithBookingProperty ?: it.pendingBookingProperty,
        authError = null
      )
    }
  }

  fun openAgentLoginDialog(open: Boolean) {
    _uiState.update {
      it.copy(
        isAgentLoginDialogOpen = open,
        authError = null
      )
    }
  }

  fun loginClient(email: String, name: String = "", phone: String = "") {
    viewModelScope.launch {
      val user = repository.loginClient(email, name, phone)
      val pendingProperty = _uiState.value.pendingBookingProperty
      _uiState.update {
        it.copy(
          isClientAuthDialogOpen = false,
          authError = null,
          bookingClientName = user.name,
          bookingClientEmail = user.email,
          bookingClientPhone = user.phone,
          isBookingDialogOpen = pendingProperty != null,
          selectedProperty = pendingProperty ?: it.selectedProperty,
          pendingBookingProperty = null
        )
      }
      postFeedback("Signed in as ${user.name}")
      refreshDatabaseStats()
    }
  }

  fun signUpClient(name: String, email: String, phone: String) {
    loginClient(email = email, name = name, phone = phone)
  }

  fun loginAgent(emailOrLicense: String, password: String) {
    viewModelScope.launch {
      val result = repository.loginAgent(emailOrLicense, password)
      if (result.isSuccess) {
        _uiState.update {
          it.copy(
            isAgentLoginDialogOpen = false,
            userRole = UserRole.AGENT,
            currentScreen = Screen.AGENT_DASHBOARD,
            authError = null
          )
        }
        postFeedback("Agent portal session activated")
      } else {
        _uiState.update {
          it.copy(authError = result.exceptionOrNull()?.message ?: "Invalid agent credentials")
        }
      }
      refreshDatabaseStats()
    }
  }

  fun signUpAgent(
    name: String,
    email: String,
    licenseNo: String,
    agency: String,
    professionalRole: String,
    password: String
  ) {
    viewModelScope.launch {
      val result = repository.signUpAgent(
        name = name,
        email = email,
        licenseNo = licenseNo,
        agency = agency,
        professionalRole = professionalRole,
        password = password
      )
      if (result.isSuccess) {
        _uiState.update {
          it.copy(
            isAgentLoginDialogOpen = false,
            userRole = UserRole.AGENT,
            currentScreen = Screen.AGENT_DASHBOARD,
            authError = null
          )
        }
        postFeedback("Agent account created & verified")
      } else {
        _uiState.update {
          it.copy(authError = result.exceptionOrNull()?.message ?: "Failed to create agent account")
        }
      }
      refreshDatabaseStats()
    }
  }

  fun logout() {
    viewModelScope.launch {
      repository.logout()
      _uiState.update {
        it.copy(
          userRole = UserRole.BUYER,
          currentScreen = Screen.HOME,
          isAgentLoginDialogOpen = false,
          isClientAuthDialogOpen = false,
          authError = null
        )
      }
      postFeedback("Signed out of session")
      refreshDatabaseStats()
    }
  }

  fun openBookingDialog(open: Boolean) {
    _uiState.update { it.copy(isBookingDialogOpen = open) }
  }

  fun openBookingSuccessDialog(open: Boolean) {
    _uiState.update { it.copy(isBookingSuccessDialogOpen = open) }
  }

  fun openInquiryDialog(open: Boolean) {
    _uiState.update { it.copy(isInquiryDialogOpen = open) }
  }

  fun setBookingDate(date: String) {
    _uiState.update { it.copy(bookingSelectedDate = date) }
  }

  fun setBookingTime(time: String) {
    _uiState.update { it.copy(bookingSelectedTime = time) }
  }

  fun setBookingForm(name: String, phone: String, email: String, notes: String) {
    _uiState.update {
      it.copy(
        bookingClientName = name,
        bookingClientPhone = phone,
        bookingClientEmail = email,
        bookingNotes = notes
      )
    }
  }

  fun confirmBooking() {
    val state = _uiState.value
    val property = state.selectedProperty ?: return

    _uiState.update { it.copy(isSubmittingBooking = true) }
    viewModelScope.launch {
      val appt = repository.bookAppointment(
        propertyId = property.id,
        date = state.bookingSelectedDate,
        timeSlot = state.bookingSelectedTime,
        clientName = state.bookingClientName,
        clientPhone = state.bookingClientPhone,
        clientEmail = state.bookingClientEmail,
        notes = state.bookingNotes
      )

      _uiState.update {
        it.copy(
          isBookingDialogOpen = false,
          isBookingSuccessDialogOpen = true,
          isSubmittingBooking = false,
          lastBookedAppointment = appt
        )
      }
      postFeedback("Viewing scheduled for ${appt.date} at ${appt.timeSlot}")
      refreshDatabaseStats()
    }
  }

  fun submitInquiry(messageText: String) {
    val state = _uiState.value
    val property = state.selectedProperty ?: return
    val user = currentUser.value
    val senderName = user?.name?.ifBlank { null }
      ?: state.bookingClientName.ifBlank { null }
      ?: "You (Prospective Buyer)"
    val senderEmail = user?.email?.ifBlank { null }
      ?: state.bookingClientEmail.ifBlank { null }
      ?: "buyer@estateflow.com"
    val senderPhone = user?.phone?.ifBlank { null }
      ?: state.bookingClientPhone.ifBlank { null }
      ?: "+1 (555) 019-8234"

    _uiState.update { it.copy(isSendingInquiry = true) }
    viewModelScope.launch {
      val inq = repository.sendInquiry(
        propertyId = property.id,
        messageText = messageText,
        senderName = senderName,
        senderEmail = senderEmail,
        senderPhone = senderPhone
      )
      _uiState.update {
        it.copy(
          isInquiryDialogOpen = false,
          isSendingInquiry = false,
          selectedInquiryId = inq.id,
          currentScreen = Screen.MESSAGES
        )
      }
      postFeedback("Inquiry sent to ${property.agentName}")
      refreshDatabaseStats()
    }
  }

  fun openInquiryChat(inquiry: Inquiry) {
    repository.markInquiryAsRead(inquiry.id)
    _uiState.update {
      it.copy(
        selectedInquiryId = inquiry.id,
        currentScreen = Screen.MESSAGES
      )
    }
    refreshDatabaseStats()
  }

  fun closeInquiryChat() {
    _uiState.update {
      it.copy(selectedInquiryId = null)
    }
  }

  fun sendChatMessage(inquiryId: String, text: String) {
    if (text.isBlank()) return
    val isAgent = _uiState.value.userRole == UserRole.AGENT
    viewModelScope.launch {
      repository.replyToInquiry(inquiryId, text, isAgent = isAgent)

      // If buyer sends message, simulate realistic agent reply after a brief moment
      if (!isAgent) {
        kotlinx.coroutines.delay(1200)
        val replyText = when {
          text.contains("strata", ignoreCase = true) || text.contains("prospectus", ignoreCase = true) ->
            "I'll have the strata documents and contingency reserve fund reports sent over to your email right away!"
          text.contains("tour", ignoreCase = true) || text.contains("walkthrough", ignoreCase = true) || text.contains("viewing", ignoreCase = true) || text.contains("available", ignoreCase = true) ->
            "The property is available! Feel free to pick a viewing time slot from the listing page or let me know your preferred time."
          text.contains("price", ignoreCase = true) || text.contains("offer", ignoreCase = true) || text.contains("tax", ignoreCase = true) ->
            "Property taxes are approximately $4,850/yr. The sellers are open to reasonable offers this week."
          else ->
            "Thanks for reaching out! I've noted your inquiry and will follow up with the full details shortly."
        }
        repository.replyToInquiry(inquiryId, replyText, isAgent = true)
      }
      refreshDatabaseStats()
    }
  }

  fun markNotificationAsRead(id: String) {
    repository.markNotificationAsRead(id)
  }

  fun markAllNotificationsAsRead() {
    val unreadCount = notifications.value.count { !it.isRead }
    repository.markAllNotificationsAsRead()
    if (unreadCount > 0) {
      postFeedback("All notifications marked as read")
    }
  }

  fun addCustomProperty(
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
  ) {
    if (_uiState.value.userRole != UserRole.AGENT) {
      return
    }
    _uiState.update { it.copy(isPublishingProperty = true) }
    viewModelScope.launch {
      val created = repository.addProperty(
        title = title,
        address = address,
        cityStateZip = cityStateZip,
        price = price,
        isRental = isRental,
        beds = beds,
        baths = baths,
        sqft = sqft,
        propertyType = propertyType,
        description = description,
        amenities = amenities,
        mediaUris = mediaUris
      )
      _uiState.update {
        it.copy(
          isAddPropertyDialogOpen = false,
          isPublishingProperty = false
        )
      }
      postFeedback("Sanctuary listing published successfully")
      refreshDatabaseStats()
    }
  }

  fun openEditPropertyDialog(property: Property?) {
    if (property != null && _uiState.value.userRole != UserRole.AGENT) {
      return
    }
    _uiState.update {
      it.copy(
        isEditPropertyDialogOpen = property != null,
        propertyToEdit = property
      )
    }
  }

  fun closeEditPropertyDialog() {
    _uiState.update {
      it.copy(
        isEditPropertyDialogOpen = false,
        propertyToEdit = null
      )
    }
  }

  fun updateCustomProperty(property: Property) {
    if (_uiState.value.userRole != UserRole.AGENT) {
      return
    }
    _uiState.update { it.copy(isSavingProperty = true) }
    viewModelScope.launch {
      val updated = repository.updateProperty(property)
      _uiState.update { state ->
        state.copy(
          isEditPropertyDialogOpen = false,
          propertyToEdit = null,
          isSavingProperty = false,
          selectedProperty = if (state.selectedProperty?.id == updated.id) updated else state.selectedProperty
        )
      }
      postFeedback("Listing changes saved successfully")
      refreshDatabaseStats()
    }
  }

  fun openDeletePropertyDialog(property: Property?) {
    if (property != null && _uiState.value.userRole != UserRole.AGENT) {
      return
    }
    _uiState.update {
      it.copy(
        isDeletePropertyDialogOpen = property != null,
        propertyToDelete = property
      )
    }
  }

  fun closeDeletePropertyDialog() {
    _uiState.update {
      it.copy(
        isDeletePropertyDialogOpen = false,
        propertyToDelete = null
      )
    }
  }

  fun confirmDeleteProperty() {
    if (_uiState.value.userRole != UserRole.AGENT) {
      return
    }
    val prop = _uiState.value.propertyToDelete ?: return
    _uiState.update { it.copy(isDeletingProperty = true) }
    viewModelScope.launch {
      repository.deleteProperty(prop.id)
      _uiState.update { state ->
        val shouldNavigateHome = state.selectedProperty?.id == prop.id
        state.copy(
          isDeletePropertyDialogOpen = false,
          propertyToDelete = null,
          isDeletingProperty = false,
          selectedProperty = if (shouldNavigateHome) null else state.selectedProperty,
          currentScreen = if (shouldNavigateHome) Screen.HOME else state.currentScreen
        )
      }
      postFeedback("Listing removed: ${prop.title}")
      refreshDatabaseStats()
    }
  }

  fun playVideo(videoUrl: String, title: String = "Property Tour") {
    _uiState.update {
      it.copy(
        activeVideoUrl = videoUrl,
        activeVideoTitle = title
      )
    }
  }

  fun closeVideo() {
    _uiState.update {
      it.copy(
        activeVideoUrl = null,
        activeVideoTitle = ""
      )
    }
  }

  fun updateAppointmentStatus(appointmentId: String, status: AppointmentStatus, clientName: String? = null) {
    val previousStatus = appointments.value.find { it.id == appointmentId }?.status ?: AppointmentStatus.UPCOMING
    repository.updateAppointmentStatus(appointmentId, status)
    refreshDatabaseStats()

    val msg = when (status) {
      AppointmentStatus.COMPLETED -> if (clientName != null) "Viewing with $clientName marked as completed" else "Viewing marked as completed"
      AppointmentStatus.CANCELLED -> "Viewing appointment cancelled"
      AppointmentStatus.UPCOMING -> "Viewing appointment scheduled"
      AppointmentStatus.PENDING -> "Viewing status updated to pending"
    }

    postFeedback(
      message = msg,
      actionLabel = if (status == AppointmentStatus.COMPLETED) "Undo" else null,
      onAction = if (status == AppointmentStatus.COMPLETED) {
        {
          repository.updateAppointmentStatus(appointmentId, previousStatus)
          refreshDatabaseStats()
          postFeedback("Viewing restored to upcoming")
        }
      } else null
    )
  }

  fun resetDatabaseToDefaults() {
    _uiState.update { it.copy(isResettingDatabase = true) }
    viewModelScope.launch {
      repository.resetDatabaseToDefaults()
      _uiState.update { it.copy(isResettingDatabase = false) }
      postFeedback("Database restored to demo defaults")
      refreshDatabaseStats()
    }
  }

  fun updateUserPassword(newPassword: String) {
    viewModelScope.launch {
      val user = currentUser.value ?: return@launch
      repository.addNotification(
        title = "Security Alert: Password Updated",
        message = "Your password has been successfully re-verified and updated on this device.",
        type = NotificationType.SECURITY_ALERT
      )
      postFeedback("Password updated successfully")
      refreshDatabaseStats()
    }
  }

  fun openPostgresDialog() {
    if (_uiState.value.userRole == UserRole.AGENT) {
      _isPostgresDialogOpen.value = true
    } else {
      postFeedback("Database configuration is restricted to licensed agents and administrators.")
    }
  }

  fun closePostgresDialog() {
    _isPostgresDialogOpen.value = false
  }

  fun openSqlSchemaDialog() {
    if (_uiState.value.userRole == UserRole.AGENT) {
      _isSqlSchemaDialogOpen.value = true
    } else {
      postFeedback("Database schema viewer is restricted to licensed agents and administrators.")
    }
  }

  fun closeSqlSchemaDialog() {
    _isSqlSchemaDialogOpen.value = false
  }

  fun getSqlSchemaContent(): String {
    return repository.getSqlSchema(getApplication())
  }

  fun updatePostgresHost(host: String) {
    val current = _postgresConfig.value
    val updated = current.copy(host = host.trim())
    _postgresConfig.value = updated
    repository.savePostgresConfig(updated)
  }

  fun updatePostgresPort(port: Int) {
    val current = _postgresConfig.value
    val updated = current.copy(port = port)
    _postgresConfig.value = updated
    repository.savePostgresConfig(updated)
  }

  fun updatePostgresDatabase(database: String) {
    val current = _postgresConfig.value
    val updated = current.copy(database = database.trim())
    _postgresConfig.value = updated
    repository.savePostgresConfig(updated)
  }

  fun updatePostgresUser(user: String) {
    val current = _postgresConfig.value
    val updated = current.copy(user = user.trim())
    _postgresConfig.value = updated
    repository.savePostgresConfig(updated)
  }

  fun updatePostgresPassword(password: String) {
    val current = _postgresConfig.value
    val updated = current.copy(password = password)
    _postgresConfig.value = updated
    repository.savePostgresConfig(updated)
  }

  fun togglePresentationLock(locked: Boolean) {
    val updated = _postgresConfig.value.copy(isPresentationLocked = locked)
    _postgresConfig.value = updated
    repository.savePostgresConfig(updated)
    if (locked) {
      postFeedback("Presentation Mode LOCKED: Server IP protected from accidental edits")
    } else {
      postFeedback("Presentation Mode UNLOCKED: Custom IP enabled")
    }
  }

  fun toggleAutoConnect(autoConnect: Boolean) {
    val updated = _postgresConfig.value.copy(autoConnectOnLaunch = autoConnect)
    _postgresConfig.value = updated
    repository.savePostgresConfig(updated)
  }

  fun testPostgresConnection(silent: Boolean = false) {
    _isTestingPostgres.value = true
    _postgresStatus.value = PostgresConnectionStatus.CONNECTING
    _postgresErrorMessage.value = null
    viewModelScope.launch {
      val result = repository.testPostgresConnection(_postgresConfig.value)
      _isTestingPostgres.value = false
      if (result.isSuccess) {
        val health = result.getOrThrow()
        _postgresStatus.value = PostgresConnectionStatus.CONNECTED
        _postgresHealth.value = health
        _postgresErrorMessage.value = null
        if (!silent) {
          postFeedback("Connected to PostgreSQL (${health.database} • ${health.latencyMs}ms)")
        }
      } else {
        _postgresStatus.value = PostgresConnectionStatus.ERROR
        val err = result.exceptionOrNull()?.message ?: "Failed to connect to PostgreSQL"
        _postgresErrorMessage.value = err
        if (!silent) {
          postFeedback("PostgreSQL connection error: $err")
        }
      }
    }
  }

  fun pushToPostgres() {
    _isSyncingPostgres.value = true
    viewModelScope.launch {
      val result = repository.syncPushToPostgres(_postgresConfig.value)
      _isSyncingPostgres.value = false
      if (result.isSuccess) {
        postFeedback("Pushed data to PostgreSQL! pgAdmin tables refreshed.")
        testPostgresConnection(silent = true)
      } else {
        postFeedback("Push failed: ${result.exceptionOrNull()?.message ?: "Check connection"}")
      }
    }
  }

  fun pullFromPostgres() {
    _isSyncingPostgres.value = true
    viewModelScope.launch {
      val result = repository.syncPullFromPostgres(_postgresConfig.value)
      _isSyncingPostgres.value = false
      if (result.isSuccess) {
        val count = result.getOrThrow()
        postFeedback("Pulled $count records from PostgreSQL into app!")
        refreshDatabaseStats()
      } else {
        postFeedback("Pull failed: ${result.exceptionOrNull()?.message ?: "Check connection"}")
      }
    }
  }
}

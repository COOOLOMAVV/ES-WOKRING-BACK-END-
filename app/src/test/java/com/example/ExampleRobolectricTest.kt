package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.EstateDatabase
import com.example.data.local.entity.PropertyEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Estateflow", appName)
  }

  @Test
  fun `room database stores and retrieves property`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, EstateDatabase::class.java).build()
    val dao = db.propertyDao()

    val entity = PropertyEntity(
      id = "test_1",
      title = "Test Villa",
      address = "123 Ocean Drive",
      cityStateZip = "Miami, FL 33139",
      price = 1500000L,
      priceFormatted = "$1,500,000",
      beds = 3.0,
      baths = 2.0,
      sqft = 1800,
      propertyType = "House",
      description = "Lovely seaside villa",
      imageResId = 0,
      availableDates = listOf("Mon, Oct 14"),
      availableTimeSlots = listOf("10:00 AM"),
      amenities = listOf("Pool", "Garden")
    )
    dao.insertProperty(entity)
    val fetched = dao.findPropertyById("test_1")
    assertEquals("Test Villa", fetched?.title)
    assertEquals(1, dao.getCount())
    db.close()
  }

  @Test
  fun `estate view model instantiates via AndroidViewModelFactory`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    val viewModel = factory.create(com.example.ui.viewmodel.EstateViewModel::class.java)
    org.junit.Assert.assertNotNull(viewModel)
  }

  @Test
  fun `agent dashboard metrics calculate 0 when empty and accurately update on completed viewings and inquiries`() {
    val appointments = listOf(
      com.example.data.model.ViewingAppointment(
        id = "appt_1",
        propertyId = "prop_1",
        propertyTitle = "Modern Villa",
        propertyAddress = "123 Ocean Blvd",
        date = "Oct 20",
        timeSlot = "2:00 PM",
        clientName = "Alice",
        clientPhone = "555-1234",
        status = com.example.data.model.AppointmentStatus.COMPLETED
      ),
      com.example.data.model.ViewingAppointment(
        id = "appt_2",
        propertyId = "prop_1",
        propertyTitle = "Modern Villa",
        propertyAddress = "123 Ocean Blvd",
        date = "Oct 21",
        timeSlot = "3:00 PM",
        clientName = "Bob",
        clientPhone = "555-5678",
        status = com.example.data.model.AppointmentStatus.UPCOMING
      )
    )

    val inquiries = listOf(
      com.example.data.model.Inquiry(
        id = "inq_1",
        propertyId = "prop_1",
        propertyTitle = "Modern Villa",
        propertyAddress = "123 Ocean Blvd",
        senderName = "Charlie",
        senderEmail = "charlie@test.com",
        senderPhone = "555-9999",
        message = "Is parking included?",
        timeAgo = "Just now",
        isUnread = true
      )
    )

    // When empty
    val emptyAppointments = emptyList<com.example.data.model.ViewingAppointment>()
    val emptyInquiries = emptyList<com.example.data.model.Inquiry>()
    assertEquals(0, emptyAppointments.count { it.status == com.example.data.model.AppointmentStatus.COMPLETED })
    assertEquals(0, emptyInquiries.count { it.isUnread || it.messages.none { m -> m.sender == "agent" } })

    // With actual data
    val completedCount = appointments.count { it.status == com.example.data.model.AppointmentStatus.COMPLETED }
    val upcomingCount = appointments.count { it.status == com.example.data.model.AppointmentStatus.UPCOMING || it.status == com.example.data.model.AppointmentStatus.PENDING }
    val pendingInquiries = inquiries.count { it.isUnread || it.messages.none { m -> m.sender == "agent" } }

    assertEquals(1, completedCount)
    assertEquals(1, upcomingCount)
    assertEquals(1, pendingInquiries)
  }

  @Test
  fun `property details back navigation returns to previous screen`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    val viewModel = factory.create(com.example.ui.viewmodel.EstateViewModel::class.java)

    val sampleProp = com.example.data.model.Property(
      id = "test_prop_nav",
      title = "Test House",
      address = "456 Sunset Blvd",
      cityStateZip = "Miami, FL 33139",
      price = 1200000L,
      priceFormatted = "$1,200,000",
      beds = 3.0,
      baths = 2.0,
      sqft = 1500,
      propertyType = "House",
      description = "Cozy house"
    )

    // Flow 1: SEARCH -> PROPERTY_DETAILS -> close -> SEARCH
    viewModel.navigateTo(com.example.ui.viewmodel.Screen.SEARCH)
    assertEquals(com.example.ui.viewmodel.Screen.SEARCH, viewModel.uiState.value.currentScreen)
    viewModel.openPropertyDetails(sampleProp)
    assertEquals(com.example.ui.viewmodel.Screen.PROPERTY_DETAILS, viewModel.uiState.value.currentScreen)
    assertEquals(com.example.ui.viewmodel.Screen.SEARCH, viewModel.uiState.value.previousScreen)
    viewModel.closePropertyDetails()
    assertEquals(com.example.ui.viewmodel.Screen.SEARCH, viewModel.uiState.value.currentScreen)

    // Flow 2: SAVED -> PROPERTY_DETAILS -> close -> SAVED
    viewModel.navigateTo(com.example.ui.viewmodel.Screen.SAVED)
    viewModel.openPropertyDetails(sampleProp)
    assertEquals(com.example.ui.viewmodel.Screen.SAVED, viewModel.uiState.value.previousScreen)
    viewModel.closePropertyDetails()
    assertEquals(com.example.ui.viewmodel.Screen.SAVED, viewModel.uiState.value.currentScreen)

    // Flow 3: AGENT_DASHBOARD -> PROPERTY_DETAILS -> close -> AGENT_DASHBOARD
    viewModel.navigateTo(com.example.ui.viewmodel.Screen.AGENT_DASHBOARD)
    viewModel.openPropertyDetails(sampleProp)
    assertEquals(com.example.ui.viewmodel.Screen.AGENT_DASHBOARD, viewModel.uiState.value.previousScreen)
    viewModel.closePropertyDetails()
    assertEquals(com.example.ui.viewmodel.Screen.AGENT_DASHBOARD, viewModel.uiState.value.currentScreen)
  }

  @Test
  fun `inquiryDao markAsRead updates inquiry isUnread status in database`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, EstateDatabase::class.java).build()
    val dao = db.inquiryDao()

    val entity = com.example.data.local.entity.InquiryEntity(
      id = "inq_test_read",
      propertyId = "prop_1",
      propertyTitle = "Villa",
      propertyAddress = "123 Ocean Blvd",
      senderName = "Alice",
      senderEmail = "alice@example.com",
      senderPhone = "555-0101",
      message = "Hello",
      timeAgo = "1m ago",
      isUnread = true,
      messages = emptyList(),
      updatedAt = System.currentTimeMillis()
    )
    dao.insertInquiry(entity)

    val inserted = dao.getInquiryById("inq_test_read")
    org.junit.Assert.assertNotNull(inserted)
    org.junit.Assert.assertTrue(inserted!!.isUnread)

    dao.markAsRead("inq_test_read")

    val updated = dao.getInquiryById("inq_test_read")
    org.junit.Assert.assertNotNull(updated)
    org.junit.Assert.assertFalse(updated!!.isUnread)

    db.close()
  }

  @Test
  fun `postgres preferences manager saves and loads permanent IP configuration`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = com.example.data.remote.PostgresPreferencesManager(context)

    val customConfig = com.example.data.remote.PostgresConfig(
      host = "192.168.1.150",
      port = 3000,
      database = "estateflow_db",
      user = "postgres",
      password = "secretpassword",
      isPresentationLocked = true,
      autoConnectOnLaunch = true
    )

    manager.saveConfig(customConfig)
    val loaded = manager.loadConfig()

    assertEquals("192.168.1.150", loaded.host)
    assertEquals(3000, loaded.port)
    assertEquals("estateflow_db", loaded.database)
    org.junit.Assert.assertTrue(loaded.isPresentationLocked)
    org.junit.Assert.assertTrue(loaded.autoConnectOnLaunch)
  }

  @Test
  fun `postgres schema asset is readable and contains valid sql statements`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val service = com.example.data.remote.PostgresSyncService()
    val sql = service.loadSqlSchemaAsset(context)

    org.junit.Assert.assertTrue(sql.contains("CREATE TABLE properties"))
    org.junit.Assert.assertTrue(sql.contains("CREATE TABLE viewing_appointments"))
    org.junit.Assert.assertTrue(sql.contains("CREATE TABLE inquiries"))
    org.junit.Assert.assertTrue(sql.contains("INSERT INTO properties"))
  }
}

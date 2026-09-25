package com.example.data.remote

import android.content.Context
import com.example.data.model.AppointmentStatus
import com.example.data.model.ChatMessage
import com.example.data.model.Inquiry
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.Property
import com.example.data.model.ViewingAppointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

data class RemoteSnapshot(
  val properties: List<Property>,
  val appointments: List<ViewingAppointment>,
  val inquiries: List<Inquiry>,
  val notifications: List<NotificationItem>
)

class PostgresSyncService {

  private val client: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(8, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .writeTimeout(10, TimeUnit.SECONDS)
    .build()

  private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

  suspend fun testConnection(config: PostgresConfig): Result<PostgresHealthResponse> = withContext(Dispatchers.IO) {
    val startTime = System.currentTimeMillis()
    val url = "${config.baseUrl}/api/health"
    try {
      val request = Request.Builder()
        .url(url)
        .get()
        .build()

      client.newCall(request).execute().use { response ->
        val latency = System.currentTimeMillis() - startTime
        val body = response.body?.string() ?: ""

        if (!response.isSuccessful) {
          val errorMsg = try {
            JSONObject(body).optString("message", "HTTP ${response.code}: ${response.message}")
          } catch (_: Exception) {
            "HTTP ${response.code}: ${response.message}"
          }
          return@withContext Result.failure(Exception("PostgreSQL Connection Error: $errorMsg"))
        }

        val json = JSONObject(body)
        val statsObj = json.optJSONObject("stats")
        val propertiesCount = statsObj?.optInt("properties", 0) ?: 0
        val appointmentsCount = statsObj?.optInt("appointments", 0) ?: 0
        val inquiriesCount = statsObj?.optInt("inquiries", 0) ?: 0

        val health = PostgresHealthResponse(
          status = json.optString("status", "CONNECTED"),
          database = json.optString("database", config.database),
          postgresVersion = json.optString("postgresVersion", "PostgreSQL (pgAdmin connected)"),
          latencyMs = latency,
          propertiesCount = propertiesCount,
          appointmentsCount = appointmentsCount,
          inquiriesCount = inquiriesCount,
          message = "Connected to ${config.database} on ${config.host}:${config.port}"
        )
        Result.success(health)
      }
    } catch (e: Exception) {
      val msg = when {
        e is java.net.ConnectException -> "Connection refused at ${config.host}:${config.port}. Ensure PostgreSQL service and FastAPI backend ('python run.py') are running on host machine."
        e is java.net.SocketTimeoutException -> "Connection timed out connecting to ${config.host}:${config.port}. Check firewall or ensure device is on the same Wi-Fi."
        e is java.net.UnknownHostException -> "Unknown host: ${config.host}. Please verify the IP address."
        else -> e.localizedMessage ?: "Failed to connect to PostgreSQL backend."
      }
      Result.failure(Exception(msg, e))
    }
  }

  suspend fun pushDataToPostgres(
    config: PostgresConfig,
    properties: List<Property>,
    appointments: List<ViewingAppointment>,
    inquiries: List<Inquiry>,
    notifications: List<NotificationItem>
  ): Result<String> = withContext(Dispatchers.IO) {
    val url = "${config.baseUrl}/api/sync/push"
    try {
      val rootJson = JSONObject()

      // Properties array
      val propArray = JSONArray()
      for (p in properties) {
        val obj = JSONObject()
        obj.put("id", p.id)
        obj.put("title", p.title)
        obj.put("address", p.address)
        obj.put("cityStateZip", p.cityStateZip)
        obj.put("price", p.price)
        obj.put("priceFormatted", p.priceFormatted)
        obj.put("isRental", p.isRental)
        obj.put("beds", p.beds)
        obj.put("baths", p.baths)
        obj.put("sqft", p.sqft)
        obj.put("propertyType", p.propertyType)
        obj.put("description", p.description)
        obj.put("isFavorite", p.isFavorite)
        obj.put("status", p.status)
        obj.put("mediaUris", JSONArray(p.mediaUris))
        obj.put("availableDates", JSONArray(p.availableDates))
        obj.put("availableTimeSlots", JSONArray(p.availableTimeSlots))
        obj.put("amenities", JSONArray(p.amenities))
        obj.put("yearBuilt", p.yearBuilt)
        obj.put("agentName", p.agentName)
        obj.put("agentPhone", p.agentPhone)
        propArray.put(obj)
      }
      rootJson.put("properties", propArray)

      // Appointments array
      val apptArray = JSONArray()
      for (a in appointments) {
        val obj = JSONObject()
        obj.put("id", a.id)
        obj.put("propertyId", a.propertyId)
        obj.put("propertyTitle", a.propertyTitle)
        obj.put("propertyAddress", a.propertyAddress)
        obj.put("clientName", a.clientName)
        obj.put("clientPhone", a.clientPhone)
        obj.put("clientEmail", a.clientEmail)
        obj.put("date", a.date)
        obj.put("timeSlot", a.timeSlot)
        obj.put("type", a.type)
        obj.put("status", a.status.name)
        obj.put("notes", a.notes)
        obj.put("timestamp", a.timestamp)
        apptArray.put(obj)
      }
      rootJson.put("appointments", apptArray)

      // Inquiries array
      val inqArray = JSONArray()
      for (inq in inquiries) {
        val obj = JSONObject()
        obj.put("id", inq.id)
        obj.put("propertyId", inq.propertyId)
        obj.put("propertyTitle", inq.propertyTitle)
        obj.put("propertyAddress", inq.propertyAddress)
        obj.put("senderName", inq.senderName)
        obj.put("senderEmail", inq.senderEmail)
        obj.put("senderPhone", inq.senderPhone)
        obj.put("message", inq.message)
        obj.put("timeAgo", inq.timeAgo)
        obj.put("isUnread", inq.isUnread)

        val msgs = JSONArray()
        for (m in inq.messages) {
          val mObj = JSONObject()
          mObj.put("id", m.id)
          mObj.put("sender", m.sender)
          mObj.put("text", m.text)
          mObj.put("time", m.time)
          mObj.put("isFromMe", m.isFromMe)
          msgs.put(mObj)
        }
        obj.put("messages", msgs)
        inqArray.put(obj)
      }
      rootJson.put("inquiries", inqArray)

      val request = Request.Builder()
        .url(url)
        .post(rootJson.toString().toRequestBody(jsonMediaType))
        .build()

      client.newCall(request).execute().use { response ->
        val body = response.body?.string() ?: ""
        if (!response.isSuccessful) {
          return@withContext Result.failure(Exception("Sync failed: HTTP ${response.code}"))
        }
        val json = JSONObject(body)
        val msg = json.optString("message", "Data synchronized with PostgreSQL successfully!")
        Result.success(msg)
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun pullDataFromPostgres(config: PostgresConfig): Result<RemoteSnapshot> = withContext(Dispatchers.IO) {
    val url = "${config.baseUrl}/api/sync/pull"
    try {
      val request = Request.Builder()
        .url(url)
        .get()
        .build()

      client.newCall(request).execute().use { response ->
        val body = response.body?.string() ?: ""
        if (!response.isSuccessful) {
          return@withContext Result.failure(Exception("Failed to fetch from PostgreSQL: HTTP ${response.code}"))
        }

        val json = JSONObject(body)
        val propArray = json.optJSONArray("properties") ?: JSONArray()
        val apptArray = json.optJSONArray("appointments") ?: JSONArray()
        val inqArray = json.optJSONArray("inquiries") ?: JSONArray()
        val notifArray = json.optJSONArray("notifications") ?: JSONArray()

        val parsedProperties = mutableListOf<Property>()
        for (i in 0 until propArray.length()) {
          val o = propArray.getJSONObject(i)
          parsedProperties.add(
            Property(
              id = o.optString("id"),
              title = o.optString("title"),
              address = o.optString("address"),
              cityStateZip = o.optString("city_state_zip", o.optString("cityStateZip")),
              price = o.optLong("price"),
              priceFormatted = o.optString("price_formatted", o.optString("priceFormatted")),
              isRental = o.optBoolean("is_rental", o.optBoolean("isRental")),
              beds = o.optDouble("beds", 3.0),
              baths = o.optDouble("baths", 2.0),
              sqft = o.optInt("sqft", 1500),
              propertyType = o.optString("property_type", o.optString("propertyType", "House")),
              description = o.optString("description"),
              imageResId = 0,
              mediaUris = o.optString("media_uris").split(",").filter { it.isNotBlank() },
              isFavorite = o.optBoolean("is_favorite", o.optBoolean("isFavorite")),
              status = o.optString("status", "Active")
            )
          )
        }

        val parsedAppointments = mutableListOf<ViewingAppointment>()
        for (i in 0 until apptArray.length()) {
          val o = apptArray.getJSONObject(i)
          val statusStr = o.optString("status", "UPCOMING")
          val status = try {
            AppointmentStatus.valueOf(statusStr.uppercase())
          } catch (_: Exception) {
            AppointmentStatus.UPCOMING
          }
          parsedAppointments.add(
            ViewingAppointment(
              id = o.optString("id"),
              propertyId = o.optString("property_id", o.optString("propertyId")),
              propertyTitle = o.optString("property_title", o.optString("propertyTitle")),
              propertyAddress = o.optString("property_address", o.optString("propertyAddress")),
              clientName = o.optString("client_name", o.optString("clientName")),
              clientPhone = o.optString("client_phone", o.optString("clientPhone")),
              clientEmail = o.optString("client_email", o.optString("clientEmail")),
              date = o.optString("appointment_date", o.optString("date")),
              timeSlot = o.optString("time_slot", o.optString("timeSlot")),
              type = o.optString("appointment_type", o.optString("type", "Viewing")),
              status = status,
              notes = o.optString("notes"),
              timestamp = o.optLong("timestamp_epoch", o.optLong("timestamp", System.currentTimeMillis()))
            )
          )
        }

        val parsedInquiries = mutableListOf<Inquiry>()
        for (i in 0 until inqArray.length()) {
          val o = inqArray.getJSONObject(i)
          val msgsArray = o.optJSONArray("messages") ?: JSONArray()
          val msgs = mutableListOf<ChatMessage>()
          for (j in 0 until msgsArray.length()) {
            val m = msgsArray.getJSONObject(j)
            msgs.add(
              ChatMessage(
                id = m.optString("id"),
                sender = m.optString("sender", "user"),
                text = m.optString("text"),
                time = m.optString("time", "Just now"),
                isFromMe = m.optBoolean("isFromMe", true)
              )
            )
          }

          parsedInquiries.add(
            Inquiry(
              id = o.optString("id"),
              propertyId = o.optString("property_id", o.optString("propertyId")),
              propertyTitle = o.optString("property_title", o.optString("propertyTitle")),
              propertyAddress = o.optString("property_address", o.optString("propertyAddress")),
              senderName = o.optString("sender_name", o.optString("senderName")),
              senderEmail = o.optString("sender_email", o.optString("senderEmail")),
              senderPhone = o.optString("sender_phone", o.optString("senderPhone")),
              message = o.optString("message"),
              timeAgo = o.optString("time_ago", o.optString("timeAgo", "Just now")),
              isUnread = o.optBoolean("is_unread", o.optBoolean("isUnread", true)),
              messages = msgs
            )
          )
        }

        val parsedNotifications = mutableListOf<NotificationItem>()
        for (i in 0 until notifArray.length()) {
          val o = notifArray.getJSONObject(i)
          val typeStr = o.optString("notification_type", o.optString("type", "LISTING_UPDATE"))
          val type = try {
            NotificationType.valueOf(typeStr.uppercase())
          } catch (_: Exception) {
            NotificationType.LISTING_UPDATE
          }
          parsedNotifications.add(
            NotificationItem(
              id = o.optString("id"),
              title = o.optString("title"),
              message = o.optString("message"),
              timestampFormatted = o.optString("timestamp_formatted", "Just now"),
              type = type,
              isRead = o.optBoolean("is_read", false),
              targetId = o.optString("target_id")
            )
          )
        }

        Result.success(RemoteSnapshot(parsedProperties, parsedAppointments, parsedInquiries, parsedNotifications))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  fun loadSqlSchemaAsset(context: Context): String {
    return try {
      context.assets.open("postgres_schema.sql").use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
          reader.readText()
        }
      }
    } catch (e: Exception) {
      "-- Error reading postgres_schema.sql asset: ${e.message}"
    }
  }
}

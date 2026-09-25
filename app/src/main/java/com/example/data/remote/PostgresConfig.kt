package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences

enum class PostgresConnectionStatus {
  DISCONNECTED,
  CONNECTING,
  CONNECTED,
  ERROR
}

data class PostgresConfig(
  val host: String = "10.0.2.2",
  val port: Int = 8000,
  val database: String = "real_estate",
  val user: String = "postgres",
  val password: String = "postgres",
  val isPresentationLocked: Boolean = false,
  val autoConnectOnLaunch: Boolean = true
) {
  val baseUrl: String
    get() {
      val raw = host.trim()
      if (raw.startsWith("http://") || raw.startsWith("https://")) {
        return if (raw.endsWith("/")) raw.dropLast(1) else raw
      }
      return if (raw.contains(":")) {
        "http://$raw"
      } else {
        "http://$raw:$port"
      }
    }
}

data class PostgresHealthResponse(
  val status: String,
  val database: String,
  val postgresVersion: String,
  val latencyMs: Long,
  val propertiesCount: Int,
  val appointmentsCount: Int,
  val inquiriesCount: Int,
  val message: String? = null
)

class PostgresPreferencesManager(context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  fun loadConfig(): PostgresConfig {
    return PostgresConfig(
      host = prefs.getString(KEY_HOST, "10.0.2.2") ?: "10.0.2.2",
      port = prefs.getInt(KEY_PORT, 8000),
      database = prefs.getString(KEY_DATABASE, "real_estate") ?: "real_estate",
      user = prefs.getString(KEY_USER, "postgres") ?: "postgres",
      password = prefs.getString(KEY_PASSWORD, "postgres") ?: "postgres",
      isPresentationLocked = prefs.getBoolean(KEY_IS_LOCKED, false),
      autoConnectOnLaunch = prefs.getBoolean(KEY_AUTO_CONNECT, true)
    )
  }

  fun saveConfig(config: PostgresConfig) {
    prefs.edit()
      .putString(KEY_HOST, config.host.trim())
      .putInt(KEY_PORT, config.port)
      .putString(KEY_DATABASE, config.database.trim())
      .putString(KEY_USER, config.user.trim())
      .putString(KEY_PASSWORD, config.password)
      .putBoolean(KEY_IS_LOCKED, config.isPresentationLocked)
      .putBoolean(KEY_AUTO_CONNECT, config.autoConnectOnLaunch)
      .apply()
  }

  fun setPresentationLocked(locked: Boolean) {
    prefs.edit().putBoolean(KEY_IS_LOCKED, locked).apply()
  }

  companion object {
    private const val PREFS_NAME = "estateflow_postgres_config"
    private const val KEY_HOST = "pg_host"
    private const val KEY_PORT = "pg_port"
    private const val KEY_DATABASE = "pg_database"
    private const val KEY_USER = "pg_user"
    private const val KEY_PASSWORD = "pg_password"
    private const val KEY_IS_LOCKED = "pg_is_presentation_locked"
    private const val KEY_AUTO_CONNECT = "pg_auto_connect"
  }
}

package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.remote.PostgresConfig
import com.example.data.remote.PostgresConnectionStatus
import com.example.data.remote.PostgresHealthResponse
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LimeAccent
import com.example.ui.theme.NaturalBg
import com.example.ui.theme.NaturalDark
import com.example.ui.theme.NaturalDarkAlt
import com.example.ui.theme.TaupeDark
import com.example.ui.theme.TaupeMuted
import com.example.ui.theme.WarmAmber

@Composable
fun PostgresConnectionDialog(
  isOpen: Boolean,
  config: PostgresConfig,
  status: PostgresConnectionStatus,
  healthResponse: PostgresHealthResponse?,
  errorMessage: String?,
  isTesting: Boolean,
  isSyncing: Boolean,
  onDismiss: () -> Unit,
  onUpdateHost: (String) -> Unit,
  onUpdatePort: (Int) -> Unit = {},
  onUpdateDatabase: (String) -> Unit = {},
  onUpdateUser: (String) -> Unit = {},
  onUpdatePassword: (String) -> Unit = {},
  onTogglePresentationLock: (Boolean) -> Unit = {},
  onToggleAutoConnect: (Boolean) -> Unit = {},
  onTestConnection: () -> Unit,
  onPushToPostgres: () -> Unit,
  onPullFromPostgres: () -> Unit,
  onOpenSqlSchema: () -> Unit
) {
  if (!isOpen) return

  val scrollState = rememberScrollState()
  val isDark = isSystemInDarkTheme()
  val dialogBg = if (isDark) DarkSurface else Color.White
  val primaryText = if (isDark) Color(0xFFF5F5F0) else NaturalDark
  val secondaryText = if (isDark) Color(0xFFD6D4CC) else TaupeDark
  val cardBg = if (isDark) NaturalDarkAlt else NaturalBg

  var showHelp by remember { mutableStateOf(false) }

  // Clean helper function to sanitize user IP input (strip trailing slashes, preserve https:// tunnel URLs)
  fun sanitizeIpInput(raw: String): String {
    val trimmed = raw.trim()
    return if (trimmed.endsWith("/")) trimmed.dropLast(1) else trimmed
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 20.dp),
      shape = RoundedCornerShape(24.dp),
      color = dialogBg,
      shadowElevation = 12.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // --------------------------------------------------------------------
        // TOP HEADER
        // --------------------------------------------------------------------
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .background(ForestGreen.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = null,
                tint = if (isDark) LimeAccent else ForestGreen,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Database Connection",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 18.sp,
                  color = primaryText
                )
              )
              Text(
                text = "FastAPI & PostgreSQL Bridge",
                style = MaterialTheme.typography.bodySmall.copy(color = secondaryText, fontSize = 12.sp)
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_pg_dialog")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = secondaryText)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --------------------------------------------------------------------
        // SCROLLABLE CONTENT BODY
        // --------------------------------------------------------------------
        Column(
          modifier = Modifier
            .weight(1f, fill = false)
            .verticalScroll(scrollState)
        ) {
          // ------------------------------------------------------------------
          // CONNECTION STATUS BANNER
          // ------------------------------------------------------------------
          when (status) {
            PostgresConnectionStatus.CONNECTED -> {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isDark) Color(0xFF1B2B1B) else Color(0xFFE8F5E9)
                ),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isDark) LimeAccent.copy(alpha = 0.5f) else Color(0xFF81C784)
                )
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isDark) LimeAccent else Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "Connected & Healthy",
                        style = MaterialTheme.typography.titleSmall.copy(
                          fontWeight = FontWeight.Bold,
                          color = if (isDark) Color(0xFFE8F5E9) else Color(0xFF1B5E20)
                        )
                      )
                    }

                    if (healthResponse != null) {
                      Box(
                        modifier = Modifier
                          .background(
                            (if (isDark) LimeAccent else ForestGreen).copy(alpha = 0.15f),
                            RoundedCornerShape(8.dp)
                          )
                          .padding(horizontal = 8.dp, vertical = 3.dp)
                      ) {
                        Text(
                          text = "${healthResponse.latencyMs} ms",
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) LimeAccent else ForestGreen
                          )
                        )
                      }
                    }
                  }

                  if (healthResponse != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      StatPill(label = "Properties", value = healthResponse.propertiesCount.toString(), isDark = isDark, modifier = Modifier.weight(1f))
                      StatPill(label = "Tours", value = healthResponse.appointmentsCount.toString(), isDark = isDark, modifier = Modifier.weight(1f))
                      StatPill(label = "Inquiries", value = healthResponse.inquiriesCount.toString(), isDark = isDark, modifier = Modifier.weight(1f))
                    }
                  }
                }
              }
            }

            PostgresConnectionStatus.CONNECTING -> {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = if (isDark) LimeAccent else ForestGreen
                  )
                  Spacer(modifier = Modifier.width(12.dp))
                  Text(
                    text = "Testing connection to ${config.host}...",
                    style = MaterialTheme.typography.bodyMedium.copy(color = primaryText, fontWeight = FontWeight.Medium)
                  )
                }
              }
            }

            PostgresConnectionStatus.ERROR -> {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isDark) Color(0xFF2C2220) else Color(0xFFFFF3F0)
                ),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isDark) Color(0xFF8D4F46) else Color(0xFFFFCCBC)
                )
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFFFF8A80) else Color(0xFFD32F2F),
                    modifier = Modifier.size(24.dp)
                  )
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text(
                      text = "Server Unreachable at ${config.host}",
                      style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFFFCDD2) else Color(0xFFC62828)
                      )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = "Make sure FastAPI ('python run.py') is running on your computer.",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = secondaryText,
                        fontSize = 11.5.sp
                      )
                    )
                  }
                }
              }
            }

            PostgresConnectionStatus.DISCONNECTED -> {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Dns,
                    contentDescription = null,
                    tint = secondaryText,
                    modifier = Modifier.size(22.dp)
                  )
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text(
                      text = "Ready to Connect",
                      style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                      )
                    )
                    Text(
                      text = "Enter your computer's IP address below and tap Connect.",
                      style = MaterialTheme.typography.bodySmall.copy(color = secondaryText, fontSize = 11.5.sp)
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // ------------------------------------------------------------------
          // SINGLE IP ADDRESS FIELD
          // ------------------------------------------------------------------
          Text(
            text = "SERVER IP ADDRESS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = if (isDark) LimeAccent else ForestGreen,
              letterSpacing = 1.sp
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = config.host,
            onValueChange = { onUpdateHost(sanitizeIpInput(it)) },
            placeholder = { Text("e.g. 10.0.2.2 or 192.168.1.50") },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
              fontWeight = FontWeight.SemiBold,
              color = primaryText
            ),
            singleLine = true,
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = null,
                tint = if (isDark) LimeAccent else ForestGreen
              )
            },
            trailingIcon = {
              if (config.host.isNotBlank()) {
                IconButton(onClick = { onUpdateHost("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Clear", tint = secondaryText)
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("pg_host_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = primaryText,
              unfocusedTextColor = primaryText,
              focusedContainerColor = if (isDark) Color(0xFF232920) else Color(0xFFFFFFFF),
              unfocusedContainerColor = if (isDark) Color(0xFF1E241B) else Color(0xFFFBFBFA),
              focusedBorderColor = if (isDark) LimeAccent else ForestGreen,
              unfocusedBorderColor = if (isDark) Color(0xFF5E6758) else Color(0xFFC5C1B6),
              cursorColor = if (isDark) LimeAccent else ForestGreen
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Quick 1-Tap Presets
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            CleanChip(
              label = "10.0.2.2 (Emulator)",
              isSelected = config.host == "10.0.2.2",
              onClick = { onUpdateHost("10.0.2.2") },
              modifier = Modifier.weight(1f)
            )
            CleanChip(
              label = "127.0.0.1 (Localhost)",
              isSelected = config.host == "127.0.0.1",
              onClick = { onUpdateHost("127.0.0.1") },
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // ------------------------------------------------------------------
          // TWO-WAY DATABASE SYNCHRONIZATION (Push & Pull)
          // ------------------------------------------------------------------
          if (status == PostgresConnectionStatus.CONNECTED) {
            Text(
              text = "DATABASE SYNCHRONIZATION",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isDark) LimeAccent else ForestGreen,
                letterSpacing = 1.sp
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedButton(
                onClick = onPushToPostgres,
                enabled = !isSyncing,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(
                  Icons.Default.CloudUpload,
                  contentDescription = null,
                  tint = if (isDark) LimeAccent else ForestGreen,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Push to DB", color = primaryText, fontWeight = FontWeight.SemiBold)
              }

              OutlinedButton(
                onClick = onPullFromPostgres,
                enabled = !isSyncing,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(
                  Icons.Default.CloudDownload,
                  contentDescription = null,
                  tint = if (isDark) LimeAccent else ForestGreen,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pull from DB", color = primaryText, fontWeight = FontWeight.SemiBold)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
          }

          // ------------------------------------------------------------------
          // EXPANDABLE TROUBLESHOOTING HELP
          // ------------------------------------------------------------------
          Surface(
            onClick = { showHelp = !showHelp },
            shape = RoundedCornerShape(12.dp),
            color = cardBg,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Phone can't connect? Tap for 3-step fix",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = if (isDark) LimeAccent else ForestGreen,
                  fontSize = 12.sp
                )
              )
              Text(
                text = if (showHelp) "▲ Hide" else "▼ Show",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = secondaryText,
                  fontSize = 11.sp
                )
              )
            }
          }

          AnimatedVisibility(visible = showHelp) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(cardBg, RoundedCornerShape(12.dp))
                .padding(12.dp)
            ) {
              Text(
                text = "1. Unblock Windows Firewall (Most Common):",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = primaryText)
              )
              Text(
                text = "Windows blocks incoming connections to port 8000 by default. Run this in PowerShell (Admin):\nnetsh advfirewall firewall add rule name=\"Estateflow\" dir=in action=allow protocol=TCP localport=8000",
                style = MaterialTheme.typography.bodySmall.copy(color = secondaryText, fontSize = 11.sp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "2. Zero-Firewall Public Tunnel (Works 100%):",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = primaryText)
              )
              Text(
                text = "In your PC terminal run:\nnpx localtunnel --port 8000\nThen copy the 'https://xxx.loca.lt' link and paste it into the IP box above!",
                style = MaterialTheme.typography.bodySmall.copy(color = secondaryText, fontSize = 11.sp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "3. Wi-Fi Check:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = primaryText)
              )
              Text(
                text = "Ensure your phone is connected to the same Wi-Fi network as your PC (not cellular data).",
                style = MaterialTheme.typography.bodySmall.copy(color = secondaryText, fontSize = 11.sp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // ------------------------------------------------------------------
          // AUTO-CONNECT SWITCH
          // ------------------------------------------------------------------
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(cardBg, RoundedCornerShape(12.dp))
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Auto-Connect on Launch",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = primaryText
                )
              )
              Text(
                text = "Connects automatically to this IP whenever the app starts.",
                style = MaterialTheme.typography.bodySmall.copy(color = secondaryText, fontSize = 11.sp)
              )
            }
            Switch(
              checked = config.autoConnectOnLaunch,
              onCheckedChange = { onToggleAutoConnect(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = if (isDark) LimeAccent else ForestGreen
              )
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // View Setup SQL helper
          TextButton(
            onClick = onOpenSqlSchema,
            modifier = Modifier.align(Alignment.CenterHorizontally)
          ) {
            Icon(Icons.Default.DataObject, contentDescription = null, tint = secondaryText, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "View PostgreSQL Setup SQL Script",
              style = MaterialTheme.typography.bodySmall.copy(color = secondaryText, fontWeight = FontWeight.Medium)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --------------------------------------------------------------------
        // BOTTOM ACTION BUTTONS
        // --------------------------------------------------------------------
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text("Done", color = primaryText, fontWeight = FontWeight.Medium)
          }

          Button(
            onClick = onTestConnection,
            enabled = !isTesting,
            modifier = Modifier
              .weight(1.5f)
              .testTag("test_pg_connection_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(14.dp)
          ) {
            if (isTesting) {
              CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Connecting...")
            } else {
              Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (status == PostgresConnectionStatus.CONNECTED) "Refresh Connection" else "Connect",
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StatPill(
  label: String,
  value: String,
  isDark: Boolean,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .background(
        if (isDark) Color(0xFF253322) else Color(0xFFF1F8E9),
        RoundedCornerShape(10.dp)
      )
      .padding(vertical = 6.dp, horizontal = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = value,
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          color = if (isDark) LimeAccent else ForestGreen
        )
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 10.sp,
          color = if (isDark) Color(0xFFD6D4CC) else TaupeDark
        )
      )
    }
  }
}

@Composable
private fun CleanChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = isSystemInDarkTheme()
  Box(
    modifier = modifier
      .background(
        if (isSelected) ForestGreen.copy(alpha = 0.15f) else (if (isDark) NaturalDarkAlt else Color(0xFFFBFBFA)),
        RoundedCornerShape(10.dp)
      )
      .border(
        1.dp,
        if (isSelected) (if (isDark) LimeAccent else ForestGreen) else (if (isDark) Color(0xFF424A3E) else Color(0xFFDDD9CF)),
        RoundedCornerShape(10.dp)
      )
      .clickable { onClick() }
      .padding(vertical = 8.dp, horizontal = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = if (isSelected) (if (isDark) LimeAccent else ForestGreen) else (if (isDark) Color(0xFFD6D4CC) else NaturalDark),
        textAlign = TextAlign.Center
      )
    )
  }
}

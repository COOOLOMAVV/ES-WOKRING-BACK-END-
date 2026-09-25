package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AgentProfile
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.remote.PostgresConfig
import com.example.data.remote.PostgresConnectionStatus
import com.example.data.remote.PostgresHealthResponse
import com.example.data.repository.DatabaseStats
import com.example.ui.dialogs.ChangePasswordDialog
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
  userRole: UserRole,
  currentUser: UserAccount? = null,
  agentProfile: AgentProfile,
  savedPropertiesCount: Int,
  upcomingViewingsCount: Int,
  databaseStats: DatabaseStats? = null,
  postgresConfig: PostgresConfig = PostgresConfig(),
  postgresStatus: PostgresConnectionStatus = PostgresConnectionStatus.DISCONNECTED,
  postgresHealth: PostgresHealthResponse? = null,
  onOpenClientAuth: () -> Unit = {},
  onOpenAgentLogin: () -> Unit = {},
  onLogout: () -> Unit = {},
  onResetDatabase: () -> Unit = {},
  onToggleRole: () -> Unit,
  onOpenNotifications: () -> Unit,
  onUpdatePassword: (newPassword: String) -> Unit = {},
  onOpenPostgresManager: () -> Unit = {},
  onOpenSqlSchema: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  var isChangePasswordOpen by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NaturalBg)
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 12.dp)
  ) {
    Text(
      text = "Account & Profile",
      style = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        color = NaturalDark,
        fontSize = 22.sp
      )
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Profile Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (userRole == UserRole.AGENT) {
            Image(
              painter = painterResource(id = R.drawable.agent_sarah_avatar_1787131767100),
              contentDescription = agentProfile.name,
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(1.5.dp, ForestGreen, CircleShape),
              contentScale = ContentScale.Crop
            )
          } else {
            Box(
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(SandWarm)
                .border(1.5.dp, if (currentUser != null) ForestGreen else SandWarm, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Profile Avatar",
                tint = ForestGreen,
                modifier = Modifier.size(30.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            val displayName = when {
              userRole == UserRole.AGENT -> agentProfile.name
              currentUser != null -> currentUser.name
              else -> "Guest Explorer"
            }
            val displayRole = when {
              userRole == UserRole.AGENT -> "${currentUser?.professionalRole ?: "Licensed Agent"} • ${agentProfile.licenseNo}"
              currentUser != null -> "Verified Client • ${currentUser.email}"
              else -> "Not signed in"
            }

            Text(
              text = displayName,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NaturalDark
              )
            )
            Text(
              text = displayRole,
              style = MaterialTheme.typography.bodySmall.copy(
                color = TaupeDark,
                fontSize = 12.sp
              )
            )

            if (userRole == UserRole.AGENT) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = null,
                  tint = AmberPending,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "${agentProfile.rating} (${agentProfile.reviewsCount} reviews)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = NaturalDark,
                    fontSize = 11.sp
                  )
                )
              }
            }
          }
        }

        // Action Buttons inside user card
        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = SandWarm.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(10.dp))

        if (currentUser == null) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onOpenClientAuth,
              modifier = Modifier
                .weight(1f)
                .testTag("profile_client_login_button"),
              colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Client Sign In", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
              onClick = onOpenAgentLogin,
              modifier = Modifier
                .weight(1f)
                .testTag("profile_agent_login_button"),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Agent Login", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        } else {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (currentUser.role == UserRole.AGENT) "Agent Session Active" else "Signed in as Client",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = ForestGreen,
                  fontWeight = FontWeight.SemiBold
                )
              )
            }

            OutlinedButton(
              onClick = onLogout,
              modifier = Modifier.testTag("profile_logout_button"),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB00020))
            ) {
              Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Log Out", fontSize = 11.5.sp)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          HorizontalDivider(color = SandWarm.copy(alpha = 0.6f))
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Security & Re-verified Password",
                style = MaterialTheme.typography.bodySmall.copy(color = NaturalDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)
              )
            }
            OutlinedButton(
              onClick = { isChangePasswordOpen = true },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("profile_update_password_button")
            ) {
              Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Update Password", fontSize = 11.5.sp)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Dedicated Agent Portal Login Card (As requested: "For the agent, it is on the profile where you can select login agent")
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("agent_portal_section_card"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (userRole == UserRole.AGENT) ForestGreen.copy(alpha = 0.08f) else Color.White
      ),
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        if (userRole == UserRole.AGENT) ForestGreen.copy(alpha = 0.4f) else SandWarm
      )
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .background(ForestGreen, RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Badge,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Licensed Agent Portal",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = NaturalDark
                )
              )
              Text(
                text = if (userRole == UserRole.AGENT) "${currentUser?.professionalRole ?: "Licensed Agent"} • MLS: ${agentProfile.licenseNo}" else "Broker, Agent & Admin Access",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TaupeDark,
                  fontSize = 11.5.sp
                )
              )
            }
          }

          if (userRole == UserRole.AGENT) {
            Text(
              text = "CONNECTED",
              style = MaterialTheme.typography.labelSmall.copy(
                color = ForestGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (userRole == UserRole.AGENT) {
          Text(
            text = "You are currently logged in with verified privileges as ${currentUser?.professionalRole ?: "Licensed Agent"} to publish listings, manage tour schedules, and review buyer leads.",
            style = MaterialTheme.typography.bodySmall.copy(color = NaturalDark, fontSize = 12.sp)
          )
        } else {
          Text(
            text = "Are you a licensed real estate agent, broker, or agency admin? Sign in or register your role to access professional property management.",
            style = MaterialTheme.typography.bodySmall.copy(color = TaupeDark, fontSize = 12.sp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = onOpenAgentLogin,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("select_login_agent_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Select Login Agent / Join Portal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Quick Portal Mode Switch (for testing or toggling roles)
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .background(if (userRole == UserRole.AGENT) ForestGreen else SandMuted, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (userRole == UserRole.AGENT) Icons.Default.BusinessCenter else Icons.Default.Person,
              contentDescription = null,
              tint = if (userRole == UserRole.AGENT) Color.White else NaturalDark,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = if (userRole == UserRole.AGENT) "Agent Portal Active" else "Buyer Explorer Mode",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = NaturalDark
              )
            )
            Text(
              text = if (userRole == UserRole.AGENT) "Tap switch to view as client" else "Tap switch to preview agent view",
              style = MaterialTheme.typography.bodySmall.copy(
                color = TaupeDark,
                fontSize = 11.5.sp
              )
            )
          }
        }

        Switch(
          checked = userRole == UserRole.AGENT,
          onCheckedChange = { onToggleRole() },
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = ForestGreen
          ),
          modifier = Modifier.testTag("role_switch_toggle")
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Stats Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "$savedPropertiesCount",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              color = NaturalDark
            )
          )
          Text(
            text = "Saved Homes",
            style = MaterialTheme.typography.bodySmall.copy(color = TaupeDark)
          )
        }
      }

      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "$upcomingViewingsCount",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              color = NaturalDark
            )
          )
          Text(
            text = "Active Viewings",
            style = MaterialTheme.typography.bodySmall.copy(color = TaupeDark)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(22.dp))

    // Settings & Quick Links
    Text(
      text = "PREFERENCES & SUPPORT",
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        color = TaupeMuted,
        fontSize = 11.sp,
        letterSpacing = 1.sp
      )
    )

    Spacer(modifier = Modifier.height(8.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
    ) {
      Column {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenNotifications() }
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Notification Center & Alerts", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = NaturalDark))
          }
          Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = TaupeMuted, modifier = Modifier.size(13.dp))
        }

        HorizontalDivider(color = SandWarm, thickness = 1.dp)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Verified MLS® Integration", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = NaturalDark))
          }
          Text("Active", style = MaterialTheme.typography.labelSmall.copy(color = ForestGreen, fontWeight = FontWeight.Bold))
        }

        HorizontalDivider(color = SandWarm, thickness = 1.dp)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("App Version", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = NaturalDark))
          }
          Text("Estateflow v1.0", style = MaterialTheme.typography.labelSmall.copy(color = TaupeMuted))
        }
      }
    }

    // Restricted: Only licensed agents and administrators have access to database connections and persistence controls
    if (userRole == UserRole.AGENT) {
      Spacer(modifier = Modifier.height(20.dp))

      // Local Room Database Backend Section
      Text(
        text = "DATABASE & SYSTEM ADMINISTRATION",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          color = TaupeMuted,
          fontSize = 11.sp,
          letterSpacing = 1.sp
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Storage, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(22.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Room SQLite Database",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = NaturalDark
                  )
                )
                Text(
                  text = "Local offline persistence engine",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = TaupeDark,
                    fontSize = 11.5.sp
                  )
                )
              }
            }
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .background(ForestGreen.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Active", style = MaterialTheme.typography.labelSmall.copy(color = ForestGreen, fontWeight = FontWeight.Bold))
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Record counters
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(NaturalBg, RoundedCornerShape(10.dp))
              .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${databaseStats?.propertiesCount ?: 0}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ForestGreen)
              )
              Text("Properties", style = MaterialTheme.typography.labelSmall.copy(color = TaupeDark, fontSize = 10.sp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${databaseStats?.appointmentsCount ?: 0}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ForestGreen)
              )
              Text("Viewings", style = MaterialTheme.typography.labelSmall.copy(color = TaupeDark, fontSize = 10.sp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${databaseStats?.inquiriesCount ?: 0}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ForestGreen)
              )
              Text("Inquiries", style = MaterialTheme.typography.labelSmall.copy(color = TaupeDark, fontSize = 10.sp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${databaseStats?.notificationsCount ?: 0}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ForestGreen)
              )
              Text("Alerts", style = MaterialTheme.typography.labelSmall.copy(color = TaupeDark, fontSize = 10.sp))
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onResetDatabase() }
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = TaupeDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Clear Data & Reset to Clean Slate",
              style = MaterialTheme.typography.labelMedium.copy(color = TaupeDark, fontWeight = FontWeight.Medium)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // PostgreSQL & pgAdmin 4 Integration Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (postgresStatus == PostgresConnectionStatus.CONNECTED) ForestGreen.copy(alpha = 0.5f) else SandWarm
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .background(ForestGreen.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Storage, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(22.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "PostgreSQL & pgAdmin 4",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = NaturalDark
                  )
                )
                Text(
                  text = "Target: ${postgresConfig.host}:${postgresConfig.port} (${postgresConfig.database})",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = TaupeDark,
                    fontSize = 11.5.sp
                  )
                )
              }
            }

            // Live Status Chip
            Box(
              modifier = Modifier
                .background(
                  when (postgresStatus) {
                    PostgresConnectionStatus.CONNECTED -> ForestGreen.copy(alpha = 0.12f)
                    PostgresConnectionStatus.CONNECTING -> WarmAmber.copy(alpha = 0.12f)
                    PostgresConnectionStatus.ERROR -> Color(0xFFFFEBEE)
                    PostgresConnectionStatus.DISCONNECTED -> SandWarm
                  },
                  RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = when (postgresStatus) {
                  PostgresConnectionStatus.CONNECTED -> "CONNECTED"
                  PostgresConnectionStatus.CONNECTING -> "CONNECTING..."
                  PostgresConnectionStatus.ERROR -> "OFFLINE"
                  PostgresConnectionStatus.DISCONNECTED -> "STANDBY"
                },
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  color = when (postgresStatus) {
                    PostgresConnectionStatus.CONNECTED -> ForestGreen
                    PostgresConnectionStatus.CONNECTING -> WarmAmber
                    PostgresConnectionStatus.ERROR -> Color(0xFFD32F2F)
                    PostgresConnectionStatus.DISCONNECTED -> TaupeDark
                  }
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Presentation Lock Status Pill
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(NaturalBg, RoundedCornerShape(8.dp))
              .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (postgresConfig.isPresentationLocked) Icons.Default.Lock else Icons.Default.LockOpen,
              contentDescription = null,
              tint = if (postgresConfig.isPresentationLocked) WarmAmber else ForestGreen,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (postgresConfig.isPresentationLocked)
                "Presentation Mode: Host IP locked & permanent"
              else
                "Custom IP Mode: Custom device IP enabled",
              style = MaterialTheme.typography.bodySmall.copy(
                color = NaturalDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onOpenPostgresManager,
              modifier = Modifier
                .weight(1.3f)
                .testTag("open_postgres_manager_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Postgres & pgAdmin Hub", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = onOpenSqlSchema,
              modifier = Modifier
                .weight(1f)
                .testTag("open_sql_schema_btn"),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("SQL Schema", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NaturalDark)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }

  if (isChangePasswordOpen && currentUser != null) {
    ChangePasswordDialog(
      isOpen = isChangePasswordOpen,
      userEmail = currentUser.email,
      onDismiss = { isChangePasswordOpen = false },
      onPasswordChanged = { newPass ->
        onUpdatePassword(newPass)
        isChangePasswordOpen = false
      }
    )
  }
}

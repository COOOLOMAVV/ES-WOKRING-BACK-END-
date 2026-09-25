package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AgentProfile
import com.example.data.model.AppointmentStatus
import com.example.data.model.Inquiry
import com.example.data.model.Property
import com.example.data.model.UserAccount
import com.example.data.model.ViewingAppointment
import com.example.ui.components.PropertyCard
import com.example.ui.theme.*

@Composable
fun AgentDashboardScreen(
  agentProfile: AgentProfile,
  currentUser: UserAccount? = null,
  appointments: List<ViewingAppointment>,
  inquiries: List<Inquiry>,
  activeListingsCount: Int,
  properties: List<Property> = emptyList(),
  onAddPropertyClick: () -> Unit,
  onAvailabilityClick: () -> Unit,
  onInquiriesClick: () -> Unit,
  onEditListingsClick: () -> Unit,
  onAppointmentClick: (ViewingAppointment) -> Unit,
  onInquiryClick: (Inquiry) -> Unit,
  onPropertyClick: ((Property) -> Unit)? = null,
  onEditPropertyClick: ((Property) -> Unit)? = null,
  onDeletePropertyClick: ((Property) -> Unit)? = null,
  onPlayVideoClick: ((String) -> Unit)? = null,
  onOpenPostgresManager: () -> Unit = {},
  onOpenSqlSchema: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  var isAppointmentsExpanded by remember { mutableStateOf(false) }

  // Dynamically calculate metrics from actual system data associated with the agent
  val agentPropertyIds = remember(properties) { properties.map { it.id }.toSet() }

  val agentInquiries = remember(inquiries, agentPropertyIds) {
    if (agentPropertyIds.isEmpty()) inquiries
    else inquiries.filter { agentPropertyIds.contains(it.propertyId) }
  }

  val agentAppointments = remember(appointments, agentPropertyIds) {
    if (agentPropertyIds.isEmpty()) appointments
    else appointments.filter { agentPropertyIds.contains(it.propertyId) }
  }

  val upcomingAppointments = remember(agentAppointments) {
    agentAppointments.filter { it.status == AppointmentStatus.UPCOMING || it.status == AppointmentStatus.PENDING }
  }

  val completedViewingsCount = remember(agentAppointments) {
    agentAppointments.count { it.status == AppointmentStatus.COMPLETED }
  }

  val pendingInquiriesCount = remember(agentInquiries) {
    agentInquiries.count { it.isUnread || it.messages.none { msg -> msg.sender == "agent" } }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NaturalBg)
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 12.dp)
  ) {
    // Top Wireframe info matching image Screen 2 in Natural Tones
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "AGENT DASHBOARD",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          color = TaupeMuted,
          fontSize = 11.sp,
          letterSpacing = 1.sp
        )
      )
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(7.dp)
            .background(ForestGreen, CircleShape)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
          text = "Live Agent Portal",
          style = MaterialTheme.typography.labelSmall.copy(
            color = ForestGreen,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = "Overview & Leads",
      style = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = NaturalDark
      )
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Agent profile info row with dynamic role badge
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White)
        .border(1.dp, SandWarm, RoundedCornerShape(16.dp))
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        painter = painterResource(id = R.drawable.agent_sarah_avatar_1787131767100),
        contentDescription = agentProfile.name,
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .border(1.5.dp, SandWarm, CircleShape),
        contentScale = ContentScale.Crop
      )
      Spacer(modifier = Modifier.width(14.dp))
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = agentProfile.name,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = NaturalDark
            )
          )
          val activeRole = currentUser?.professionalRole ?: "Licensed Agent"
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = ForestGreen.copy(alpha = 0.12f)
          ) {
            Text(
              text = activeRole,
              style = MaterialTheme.typography.labelSmall.copy(
                color = ForestGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
        Text(
          text = "${agentProfile.title} • MLS: ${agentProfile.licenseNo}",
          style = MaterialTheme.typography.bodySmall.copy(
            color = TaupeDark,
            fontSize = 12.sp
          )
        )
      }
    }

    // Role-specific privilege banner
    val rolePrivilegeNote = when {
      currentUser?.professionalRole?.contains("Broker", ignoreCase = true) == true ->
        "Managing Broker Oversight: Full administrative approval for active agency listings, tour scheduling, and MLS agent compliance."
      currentUser?.professionalRole?.contains("Admin", ignoreCase = true) == true ->
        "Brokerage Administration: Lead routing, portal access governance, client inquiry dispatch, and analytics."
      currentUser?.professionalRole?.contains("Property Manager", ignoreCase = true) == true ->
        "Property & Leasing Management: Rental listings, tenant schedule coordination, and viewing management."
      else ->
        "Licensed Agent Privileges: Client consultation, property showcase management, and tour confirmations."
    }

    Surface(
      shape = RoundedCornerShape(12.dp),
      color = SandMuted,
      border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp)
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = null,
          tint = ForestGreen,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = rolePrivilegeNote,
          style = MaterialTheme.typography.bodySmall.copy(
            color = NaturalDark,
            fontSize = 11.5.sp,
            lineHeight = 16.sp
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // QUICK ACTIONS heading & 4 action buttons
    Text(
      text = "QUICK ACTIONS",
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        color = TaupeMuted,
        fontSize = 11.sp,
        letterSpacing = 1.sp
      )
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      QuickActionButton(
        icon = Icons.Default.Add,
        label = "+ Listing",
        onClick = onAddPropertyClick,
        modifier = Modifier.weight(1f),
        testTag = "quick_action_add_property"
      )
      QuickActionButton(
        icon = Icons.Default.CalendarMonth,
        label = "Schedule",
        onClick = onAvailabilityClick,
        modifier = Modifier.weight(1f),
        testTag = "quick_action_availability"
      )
      QuickActionButton(
        icon = Icons.Default.ChatBubbleOutline,
        label = "Inquiries",
        onClick = onInquiriesClick,
        modifier = Modifier.weight(1f),
        testTag = "quick_action_inquiries"
      )
      QuickActionButton(
        icon = Icons.Default.Edit,
        label = "Listings",
        onClick = onEditListingsClick,
        modifier = Modifier.weight(1f),
        testTag = "quick_action_edit_listings"
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // SUMMARY METRICS heading & 2x2 grid
    Text(
      text = "SUMMARY METRICS",
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        color = TaupeMuted,
        fontSize = 11.sp,
        letterSpacing = 1.sp
      )
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      MetricSummaryCard(
        number = "$activeListingsCount",
        label = "Active Listings",
        modifier = Modifier.weight(1f)
      )
      MetricSummaryCard(
        number = "${upcomingAppointments.size}",
        label = "Upcoming Viewings",
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      MetricSummaryCard(
        number = "$pendingInquiriesCount",
        label = "Pending Inquiries",
        modifier = Modifier.weight(1f)
      )
      MetricSummaryCard(
        number = "$completedViewingsCount",
        label = "Completed Viewings",
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // UPCOMING APPOINTMENTS Section
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "UPCOMING APPOINTMENTS",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = TaupeMuted,
            fontSize = 11.sp,
            letterSpacing = 1.sp
          )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
          modifier = Modifier
            .size(18.dp)
            .background(ForestGreen, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${upcomingAppointments.size}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }

      if (upcomingAppointments.size > 2) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = SandMuted,
          border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { isAppointmentsExpanded = !isAppointmentsExpanded }
            .testTag("toggle_upcoming_appointments_header")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (isAppointmentsExpanded) "Show Less" else "See More",
              style = MaterialTheme.typography.labelSmall.copy(
                color = ForestGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            )
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
              imageVector = if (isAppointmentsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
              contentDescription = null,
              tint = ForestGreen,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (upcomingAppointments.isEmpty()) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Box(
          modifier = Modifier.fillMaxWidth().padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No upcoming viewings scheduled.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TaupeMuted)
          )
        }
      }
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Always show the first 2 appointments
        upcomingAppointments.take(2).forEach { appt ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onAppointmentClick(appt) }
              .testTag("appointment_item_${appt.id}"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .background(SandMuted, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.EventNote,
                  contentDescription = null,
                  tint = ForestGreen,
                  modifier = Modifier.size(18.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "${appt.propertyAddress} - ${appt.type}",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NaturalDark
                  ),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "${appt.date} • ${appt.timeSlot} (${appt.clientName})",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = TaupeDark,
                    fontSize = 12.sp
                  ),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Details",
                tint = TaupeMuted,
                modifier = Modifier.size(13.dp)
              )
            }
          }
        }

        // Collapsible remaining appointments (shown when expanded)
        if (upcomingAppointments.size > 2) {
          AnimatedVisibility(
            visible = isAppointmentsExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              upcomingAppointments.drop(2).forEach { appt ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAppointmentClick(appt) }
                    .testTag("appointment_item_${appt.id}"),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = Color.White),
                  border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
                ) {
                  Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .background(SandMuted, RoundedCornerShape(10.dp)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(18.dp)
                      )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "${appt.propertyAddress} - ${appt.type}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                          fontWeight = FontWeight.Bold,
                          fontSize = 14.sp,
                          color = NaturalDark
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = "${appt.date} • ${appt.timeSlot} (${appt.clientName})",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = TaupeDark,
                          fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                    }

                    Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                      contentDescription = "Details",
                      tint = TaupeMuted,
                      modifier = Modifier.size(13.dp)
                    )
                  }
                }
              }
            }
          }

          // Dedicated "See More" / "Show Less" button tab below list
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable { isAppointmentsExpanded = !isAppointmentsExpanded }
              .testTag("see_more_appointments_button"),
            shape = RoundedCornerShape(12.dp),
            color = if (isAppointmentsExpanded) SandMuted else Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 14.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (isAppointmentsExpanded) "Show Less" else "See More (+${appointments.size - 2} more viewings)",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = ForestGreen,
                  fontSize = 13.sp
                )
              )
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = if (isAppointmentsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // RECENT INQUIRIES Section
    Text(
      text = "RECENT INQUIRIES",
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        color = TaupeMuted,
        fontSize = 11.sp,
        letterSpacing = 1.sp
      )
    )

    Spacer(modifier = Modifier.height(10.dp))

    if (agentInquiries.isEmpty()) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Box(
          modifier = Modifier.fillMaxWidth().padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No pending inquiries at this time.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TaupeMuted)
          )
        }
      }
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        agentInquiries.take(3).forEach { inq ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onInquiryClick(inq) }
              .testTag("inquiry_item_${inq.id}"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .background(SandMuted, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Chat,
                  contentDescription = null,
                  tint = ForestGreen,
                  modifier = Modifier.size(18.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = inq.senderName,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NaturalDark
                  )
                )
                Text(
                  text = inq.message,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = TaupeDark,
                    fontSize = 12.sp
                  ),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Open Chat",
                tint = TaupeMuted,
                modifier = Modifier.size(13.dp)
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // MANAGE SANCTUARIES & LISTINGS SECTION
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "SANCTUARIES & LISTINGS",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = TaupeMuted,
            fontSize = 11.sp,
            letterSpacing = 1.sp
          )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
          modifier = Modifier
            .size(18.dp)
            .background(ForestGreen, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${properties.size}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = SandMuted,
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable { onAddPropertyClick() }
          .testTag("dashboard_add_listing_header_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = ForestGreen,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Publish Sanctuary",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = ForestGreen,
              fontSize = 11.sp
            )
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (properties.isEmpty()) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "No Sanctuaries Published Yet",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = NaturalDark
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Create your first listing to showcase to clients, complete with imported photos and video walkthroughs.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = TaupeDark,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          )
          Spacer(modifier = Modifier.height(14.dp))
          androidx.compose.material3.Button(
            onClick = onAddPropertyClick,
            shape = RoundedCornerShape(10.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = ForestGreen),
            modifier = Modifier.testTag("dashboard_empty_publish_button")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Publish Sanctuary", fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        properties.forEach { property ->
          PropertyCard(
            property = property,
            onClick = { onPropertyClick?.invoke(property) },
            onFavoriteClick = null,
            onEditClick = { onEditPropertyClick?.invoke(property) },
            onDeleteClick = { onDeletePropertyClick?.invoke(property) },
            onPlayVideoClick = { videoUrl -> onPlayVideoClick?.invoke(videoUrl) },
            modifier = Modifier.testTag("agent_property_card_${property.id}")
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Dedicated Agent-Only Database Integration Card
    Text(
      text = "DATABASE & POSTGRESQL HUB",
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        color = TaupeMuted,
        fontSize = 11.sp,
        letterSpacing = 1.sp
      )
    )

    Spacer(modifier = Modifier.height(10.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .background(ForestGreen.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Storage,
              contentDescription = null,
              tint = ForestGreen,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "PostgreSQL & Database Hub",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = NaturalDark
              )
            )
            Text(
              text = "Agent-only database connection, sync & SQL schema management",
              style = MaterialTheme.typography.bodySmall.copy(
                color = TaupeDark,
                fontSize = 11.5.sp
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          androidx.compose.material3.Button(
            onClick = onOpenPostgresManager,
            modifier = Modifier
              .weight(1.3f)
              .testTag("agent_open_postgres_hub_btn"),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Database Hub", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          androidx.compose.material3.OutlinedButton(
            onClick = onOpenSqlSchema,
            modifier = Modifier
              .weight(1f)
              .testTag("agent_open_sql_schema_btn"),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("SQL Schema", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NaturalDark)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
private fun QuickActionButton(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = ""
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(Color.White)
      .border(1.dp, SandWarm, RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(vertical = 12.dp, horizontal = 4.dp)
      .testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = ForestGreen,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.5.sp,
        color = NaturalDark
      ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
private fun MetricSummaryCard(
  number: String,
  label: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Text(
        text = number,
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 26.sp,
          color = NaturalDark
        )
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall.copy(
          color = TaupeDark,
          fontSize = 12.sp
        )
      )
    }
  }
}

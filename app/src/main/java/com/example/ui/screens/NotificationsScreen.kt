package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
  notifications: List<NotificationItem>,
  onNotificationClick: (NotificationItem) -> Unit,
  onMarkAllAsRead: () -> Unit,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf("All") }
  val tabs = listOf("All", "Viewings", "Leads", "Updates")
  val unreadCount = notifications.count { !it.isRead }

  val filteredNotifications = remember(notifications, selectedTab) {
    when (selectedTab) {
      "Viewings" -> notifications.filter { it.type == NotificationType.VIEWING_BOOKED }
      "Leads" -> notifications.filter { it.type == NotificationType.NEW_INQUIRY }
      "Updates" -> notifications.filter { it.type == NotificationType.PRICE_DROP || it.type == NotificationType.LISTING_UPDATE }
      else -> notifications
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NaturalBg)
  ) {
    // Dedicated Top Notification Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier
            .size(38.dp)
            .background(Color.White, CircleShape)
            .border(1.dp, SandWarm, CircleShape)
            .testTag("notifications_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = NaturalDark,
            modifier = Modifier.size(18.dp)
          )
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "Notifications",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = NaturalDark,
                fontSize = 20.sp
              )
            )

            if (unreadCount > 0) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = ForestGreen
              ) {
                Text(
                  text = "$unreadCount New",
                  modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                )
              }
            }
          }
          Text(
            text = "Activity updates and appointment logs",
            style = MaterialTheme.typography.bodySmall.copy(
              color = TaupeDark,
              fontSize = 11.5.sp
            )
          )
        }
      }

      if (unreadCount > 0) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = SandMuted,
          border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onMarkAllAsRead() }
            .testTag("mark_all_read_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.DoneAll,
              contentDescription = null,
              tint = ForestGreen,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "Mark read",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                fontSize = 11.sp
              )
            )
          }
        }
      }
    }

    // Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      tabs.forEach { tab ->
        val isSelected = selectedTab == tab
        val countForTab = when (tab) {
          "Viewings" -> notifications.count { it.type == NotificationType.VIEWING_BOOKED && !it.isRead }
          "Leads" -> notifications.count { it.type == NotificationType.NEW_INQUIRY && !it.isRead }
          "Updates" -> notifications.count { (it.type == NotificationType.PRICE_DROP || it.type == NotificationType.LISTING_UPDATE) && !it.isRead }
          else -> unreadCount
        }

        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { selectedTab = tab }
            .testTag("notification_tab_$tab"),
          shape = RoundedCornerShape(14.dp),
          color = if (isSelected) ForestGreen else Color.White,
          border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) ForestGreen else SandWarm
          )
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = tab,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else NaturalDark,
                fontSize = 12.sp
              )
            )
            if (countForTab > 0) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .background(
                    if (isSelected) Color.White else RoseFavorite,
                    CircleShape
                  )
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(color = SandWarm, thickness = 1.dp)

    if (filteredNotifications.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .background(SandMuted, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Notifications,
              contentDescription = null,
              tint = TaupeMuted,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No notifications in this category",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = NaturalDark
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "You're all caught up on alerts and appointment logs.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = TaupeMuted,
              fontSize = 12.sp
            )
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredNotifications, key = { it.id }) { notif ->
          val icon = when (notif.type) {
            NotificationType.VIEWING_BOOKED -> Icons.Default.CalendarMonth
            NotificationType.NEW_INQUIRY -> Icons.Default.ChatBubbleOutline
            NotificationType.PRICE_DROP -> Icons.Default.LocalOffer
            NotificationType.LISTING_UPDATE -> Icons.Default.Home
            NotificationType.SECURITY_ALERT -> Icons.Default.Security
          }

          val iconTint = when (notif.type) {
            NotificationType.VIEWING_BOOKED -> ForestGreen
            NotificationType.NEW_INQUIRY -> ForestGreen
            NotificationType.PRICE_DROP -> RoseFavorite
            NotificationType.LISTING_UPDATE -> NaturalDark
            NotificationType.SECURITY_ALERT -> ForestGreen
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(if (!notif.isRead) SandWarm.copy(alpha = 0.35f) else Color.White)
              .border(1.dp, SandWarm, RoundedCornerShape(16.dp))
              .clickable { onNotificationClick(notif) }
              .padding(14.dp)
              .testTag("notification_item_${notif.id}"),
            verticalAlignment = Alignment.Top
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .background(SandMuted, CircleShape)
                .border(1.dp, SandWarm, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = notif.title,
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.SemiBold,
                    color = NaturalDark,
                    fontSize = 14.sp
                  )
                )
                Text(
                  text = notif.timestampFormatted,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = TaupeMuted,
                    fontSize = 10.5.sp
                  )
                )
              }

              Spacer(modifier = Modifier.height(3.dp))

              Text(
                text = notif.message,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = if (!notif.isRead) NaturalDark else TaupeDark,
                  fontSize = 12.5.sp,
                  lineHeight = 17.sp
                )
              )
            }

            if (!notif.isRead) {
              Spacer(modifier = Modifier.width(8.dp))
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(ForestGreen, CircleShape)
                  .align(Alignment.CenterVertically)
              )
            }
          }
        }
      }
    }
  }
}

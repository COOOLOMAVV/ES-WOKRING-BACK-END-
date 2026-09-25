package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.UserRole

@Composable
fun EstateBottomNav(
  currentScreen: Screen,
  userRole: UserRole,
  unreadMessagesCount: Int = 1,
  onNavigate: (Screen) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = Color.White
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
    ) {
      HorizontalDivider(color = SandWarm, thickness = 1.dp)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(64.dp)
          .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (userRole == UserRole.BUYER) {
          // Natural Tones Buyer Navigation: Explore, Saved, Search, Inbox, Profile
          NavItem(
            label = "Explore",
            icon = if (currentScreen == Screen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
            isSelected = currentScreen == Screen.HOME || currentScreen == Screen.PROPERTY_DETAILS,
            onClick = { onNavigate(Screen.HOME) },
            testTag = "nav_home"
          )
          NavItem(
            label = "Search",
            icon = Icons.Filled.Search,
            isSelected = currentScreen == Screen.SEARCH,
            onClick = { onNavigate(Screen.SEARCH) },
            testTag = "nav_search"
          )
          NavItem(
            label = "Saved",
            icon = if (currentScreen == Screen.SAVED) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            isSelected = currentScreen == Screen.SAVED,
            onClick = { onNavigate(Screen.SAVED) },
            testTag = "nav_saved"
          )
          NavItem(
            label = "Inbox",
            icon = if (currentScreen == Screen.MESSAGES) Icons.Filled.ChatBubble else Icons.Filled.ChatBubbleOutline,
            isSelected = currentScreen == Screen.MESSAGES,
            onClick = { onNavigate(Screen.MESSAGES) },
            badgeCount = unreadMessagesCount,
            testTag = "nav_messages"
          )
          NavItem(
            label = "Profile",
            icon = if (currentScreen == Screen.PROFILE) Icons.Filled.Person else Icons.Filled.PersonOutline,
            isSelected = currentScreen == Screen.PROFILE,
            onClick = { onNavigate(Screen.PROFILE) },
            testTag = "nav_profile"
          )
        } else {
          // Agent Navigation
          NavItem(
            label = "Dashboard",
            icon = Icons.Filled.GridView,
            isSelected = currentScreen == Screen.AGENT_DASHBOARD,
            onClick = { onNavigate(Screen.AGENT_DASHBOARD) },
            testTag = "nav_dashboard"
          )
          NavItem(
            label = "Listings",
            icon = if (currentScreen == Screen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
            isSelected = currentScreen == Screen.HOME,
            onClick = { onNavigate(Screen.HOME) },
            testTag = "nav_properties"
          )
          NavItem(
            label = "Agenda",
            icon = if (currentScreen == Screen.CALENDAR_APPOINTMENTS) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
            isSelected = currentScreen == Screen.CALENDAR_APPOINTMENTS,
            onClick = { onNavigate(Screen.CALENDAR_APPOINTMENTS) },
            testTag = "nav_calendar"
          )
          NavItem(
            label = "Inbox",
            icon = if (currentScreen == Screen.MESSAGES) Icons.Filled.ChatBubble else Icons.Filled.ChatBubbleOutline,
            isSelected = currentScreen == Screen.MESSAGES,
            onClick = { onNavigate(Screen.MESSAGES) },
            badgeCount = unreadMessagesCount,
            testTag = "nav_agent_messages"
          )
          NavItem(
            label = "Profile",
            icon = if (currentScreen == Screen.PROFILE) Icons.Filled.Person else Icons.Filled.PersonOutline,
            isSelected = currentScreen == Screen.PROFILE,
            onClick = { onNavigate(Screen.PROFILE) },
            testTag = "nav_agent_profile"
          )
        }
      }
    }
  }
}

@Composable
private fun NavItem(
  label: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  badgeCount: Int = 0,
  testTag: String = ""
) {
  val contentColor by animateColorAsState(
    targetValue = if (isSelected) ForestGreen else TaupeMuted,
    label = "nav_content_color_$label"
  )
  val pillBg by animateColorAsState(
    targetValue = if (isSelected) ForestGreenLight else Color.Transparent,
    label = "nav_pill_bg_$label"
  )

  Column(
    modifier = Modifier
      .defaultMinSize(minWidth = 52.dp, minHeight = 48.dp)
      .clip(RoundedCornerShape(14.dp))
      .clickable { onClick() }
      .padding(horizontal = 6.dp, vertical = 4.dp)
      .testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(pillBg)
        .padding(horizontal = 14.dp, vertical = 3.dp),
      contentAlignment = Alignment.Center
    ) {
      BadgedBox(
        badge = {
          if (badgeCount > 0) {
            Badge(containerColor = RoseFavorite, contentColor = Color.White) {
              Text("$badgeCount", fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      ) {
        Icon(
          imageVector = icon,
          contentDescription = label,
          tint = contentColor,
          modifier = Modifier.size(22.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.5.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = contentColor
      )
    )
  }
}

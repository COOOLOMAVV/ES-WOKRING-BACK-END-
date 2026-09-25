package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarData
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Property
import com.example.ui.theme.*
import com.example.ui.viewmodel.UserRole

@Composable
fun EstateTopBar(
  title: String = "Estateflow",
  subtitle: String? = null,
  userRole: UserRole,
  unreadCount: Int,
  onNotificationClick: () -> Unit,
  onFilterClick: () -> Unit,
  onToggleRoleClick: () -> Unit,
  onProfileClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(NaturalBg)
      .statusBarsPadding()
      .padding(horizontal = 20.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Left side: User Avatar + Location info in Natural Tones style
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(SandWarm)
          .border(2.dp, Color.White, CircleShape)
          .then(
            if (onProfileClick != null) {
              Modifier.clickable(onClick = onProfileClick)
            } else {
              Modifier
            }
          )
          .testTag("topbar_profile_avatar"),
        contentAlignment = Alignment.Center
      ) {
        if (userRole == UserRole.AGENT) {
          Image(
            painter = painterResource(id = R.drawable.agent_sarah_avatar_1787131767100),
            contentDescription = "Agent Avatar",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        } else {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "User Profile Avatar",
            tint = ForestGreen,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Column {
        Text(
          text = "LOCATION",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp,
            color = TaupeMuted,
            letterSpacing = 1.sp
          )
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = ForestGreen,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = "Cebu City",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = NaturalDark
            )
          )
        }
      }
    }

    // Right side: Role switch chip & Natural Tones notification button
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Role Switcher Chip with smooth animation
      val roleBg by animateColorAsState(
        targetValue = if (userRole == UserRole.AGENT) ForestGreen else SandMuted,
        label = "role_bg"
      )
      val roleTextColor by animateColorAsState(
        targetValue = if (userRole == UserRole.AGENT) Color.White else NaturalDark,
        label = "role_text_color"
      )

      Surface(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .clickable { onToggleRoleClick() }
          .testTag("role_switcher_button"),
        color = roleBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
        shape = RoundedCornerShape(20.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          Icon(
            imageVector = if (userRole == UserRole.AGENT) Icons.Default.BusinessCenter else Icons.Default.Person,
            contentDescription = "Switch Role",
            tint = roleTextColor,
            modifier = Modifier.size(13.dp)
          )
          Text(
            text = if (userRole == UserRole.AGENT) "Agent" else "Buyer",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = roleTextColor,
              fontSize = 11.5.sp
            )
          )
        }
      }

      // Notifications Bell button matching Natural Tones mockup
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(Color.White)
          .border(1.dp, SandWarm, CircleShape)
          .clickable { onNotificationClick() }
          .testTag("notification_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (unreadCount > 0) Icons.Default.Notifications else Icons.Default.NotificationsNone,
          contentDescription = "Notifications",
          tint = ForestGreen,
          modifier = Modifier.size(20.dp)
        )
        if (unreadCount > 0) {
          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(top = 5.dp, end = 5.dp)
              .size(15.dp)
              .background(RoseFavorite, CircleShape)
              .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (unreadCount > 9) "9+" else "$unreadCount",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }
      }
    }
  }
}

@Composable
fun PropertyCard(
  property: Property,
  onClick: () -> Unit,
  onFavoriteClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier,
  onEditClick: (() -> Unit)? = null,
  onDeleteClick: (() -> Unit)? = null,
  onPlayVideoClick: ((String) -> Unit)? = null
) {
  val context = LocalContext.current
  val heartScale by animateFloatAsState(
    targetValue = if (property.isFavorite) 1.15f else 1.0f,
    animationSpec = spring(dampingRatio = 0.45f, stiffness = 400f),
    label = "heart_scale"
  )

  val firstMedia = property.mediaUris.firstOrNull()
  val hasVideo = property.mediaUris.any { Property.isVideoUri(it) }
  val firstVideo = property.mediaUris.firstOrNull { Property.isVideoUri(it) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .clickable { onClick() }
      .testTag("property_card_${property.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
  ) {
    Column {
      // Property Image / Hero with gradient and floating chips
      val fallbackImageRes = if (property.imageResId != 0) property.imageResId else R.drawable.property_townhouse_1787131686098
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .background(SandMuted)
      ) {
        if (!firstMedia.isNullOrBlank()) {
          AsyncImage(
            model = ImageRequest.Builder(context)
              .data(firstMedia)
              .crossfade(true)
              .error(fallbackImageRes)
              .placeholder(fallbackImageRes)
              .build(),
            contentDescription = property.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        } else {
          Image(
            painter = painterResource(id = fallbackImageRes),
            contentDescription = property.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        }

        // Subtle gradient overlay at top & bottom
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Black.copy(alpha = 0.25f),
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.65f)
                )
              )
            )
        )

        // Status / Type badge
        Surface(
          modifier = Modifier
            .padding(12.dp)
            .align(Alignment.TopStart),
          shape = RoundedCornerShape(8.dp),
          color = NaturalDark.copy(alpha = 0.85f)
        ) {
          Text(
            text = "${property.propertyType} • ${if (property.isRental) "For Rent" else "For Sale"}",
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.sp
            )
          )
        }

        // Bookmark / Favorite icon button with spring scale (for client views)
        if (onFavoriteClick != null) {
          Box(
            modifier = Modifier
              .padding(10.dp)
              .align(Alignment.TopEnd)
              .size(38.dp)
              .scale(heartScale)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.92f))
              .clickable { onFavoriteClick() }
              .testTag("favorite_button_${property.id}"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (property.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "Favorite",
              tint = if (property.isFavorite) RoseFavorite else NaturalDark,
              modifier = Modifier.size(19.dp)
            )
          }
        }

        // Center play button if property has video tour
        if (hasVideo && firstVideo != null) {
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .size(46.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.65f))
              .border(1.5.dp, Color.White, CircleShape)
              .clickable {
                if (onPlayVideoClick != null) {
                  onPlayVideoClick(firstVideo)
                } else {
                  onClick()
                }
              }
              .testTag("play_video_card_${property.id}"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Play Video Tour",
              tint = Color.White,
              modifier = Modifier.size(28.dp)
            )
          }
        }

        // Media count indicator & Price badge embedded in image bottom
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .align(Alignment.BottomStart),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (property.mediaUris.isNotEmpty()) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color.Black.copy(alpha = 0.6f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = if (hasVideo) Icons.Default.Videocam else Icons.Default.BusinessCenter,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(12.dp)
                )
                Text(
                  text = "${property.mediaUris.size} Media",
                  style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 10.sp)
                )
              }
            }
          } else {
            Spacer(modifier = Modifier.width(1.dp))
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = ForestGreen
          ) {
            Text(
              text = property.priceFormatted,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            )
          }
        }
      }

      // Property Details
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = property.title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = NaturalDark
            ),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          // If edit or delete callbacks provided, show standardized overflow menu
          if (onEditClick != null || onDeleteClick != null) {
            var menuExpanded by remember { mutableStateOf(false) }
            Box {
              IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier
                  .size(36.dp)
                  .testTag("prop_menu_button_${property.id}")
              ) {
                Icon(
                  imageVector = Icons.Default.MoreVert,
                  contentDescription = "Listing options for ${property.title}",
                  tint = TaupeDark,
                  modifier = Modifier.size(20.dp)
                )
              }

              DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier
                  .background(Color.White)
                  .border(1.dp, SandWarm, RoundedCornerShape(12.dp))
              ) {
                if (onEditClick != null) {
                  DropdownMenuItem(
                    text = {
                      Text("Edit Sanctuary Details", color = NaturalDark, fontWeight = FontWeight.Medium, fontSize = 13.5.sp)
                    },
                    leadingIcon = {
                      Icon(Icons.Default.Edit, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                    },
                    onClick = {
                      menuExpanded = false
                      onEditClick()
                    },
                    modifier = Modifier.testTag("menu_edit_property_${property.id}")
                  )
                }

                if (onEditClick != null && onDeleteClick != null) {
                  HorizontalDivider(color = SandWarm.copy(alpha = 0.8f), modifier = Modifier.padding(vertical = 4.dp))
                }

                if (onDeleteClick != null) {
                  DropdownMenuItem(
                    text = {
                      Text("Remove Listing", color = RoseFavorite, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp)
                    },
                    leadingIcon = {
                      Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = RoseFavorite, modifier = Modifier.size(18.dp))
                    },
                    onClick = {
                      menuExpanded = false
                      onDeleteClick()
                    },
                    modifier = Modifier.testTag("menu_delete_property_${property.id}")
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Location Address row
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = TaupeMuted,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = property.address,
            style = MaterialTheme.typography.bodySmall.copy(
              color = TaupeDark,
              fontSize = 12.5.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Specs Pills Row in Natural Tones style
        val bedText = if (property.beds % 1.0 == 0.0) "${property.beds.toInt()} Beds" else "${property.beds} Beds"
        val bathText = if (property.baths % 1.0 == 0.0) "${property.baths.toInt()} Baths" else "${property.baths} Baths"
        val sqftText = "${String.format("%,d", property.sqft)} sqft"

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          SpecPill(icon = Icons.Default.Bed, label = bedText)
          SpecPill(icon = Icons.Default.Bathtub, label = bathText)
          SpecPill(icon = Icons.Default.SquareFoot, label = sqftText)
        }
      }
    }
  }
}

@Composable
private fun SpecPill(
  icon: ImageVector,
  label: String
) {
  Surface(
    shape = RoundedCornerShape(6.dp),
    color = SandMuted,
    border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = ForestGreen,
        modifier = Modifier.size(13.dp)
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          color = NaturalDark,
          fontWeight = FontWeight.Medium,
          fontSize = 11.5.sp
        )
      )
    }
  }
}

@Composable
fun MetricFeatureItem(
  icon: ImageVector,
  value: String,
  label: String,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(Color.White)
      .border(1.dp, SandWarm, RoundedCornerShape(14.dp))
      .padding(vertical = 12.dp, horizontal = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .background(SandMuted, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = ForestGreen,
        modifier = Modifier.size(18.dp)
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 12.5.sp,
        color = NaturalDark
      ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      textAlign = TextAlign.Center
    )

    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        color = TaupeMuted,
        fontSize = 10.sp
      ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun CustomEstateSnackbar(
  snackbarData: SnackbarData,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .padding(horizontal = 16.dp, vertical = 10.dp)
      .fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = NaturalDark),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(26.dp)
            .background(ForestGreenLight.copy(alpha = 0.25f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = LimeAccent,
            modifier = Modifier.size(15.dp)
          )
        }

        Text(
          text = snackbarData.visuals.message,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 13.5.sp
          )
        )
      }

      val actionLabel = snackbarData.visuals.actionLabel
      if (actionLabel != null) {
        Spacer(modifier = Modifier.width(10.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = LimeAccent.copy(alpha = 0.18f),
          border = androidx.compose.foundation.BorderStroke(1.dp, LimeAccent.copy(alpha = 0.45f)),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { snackbarData.performAction() }
            .testTag("snackbar_action_button")
        ) {
          Text(
            text = actionLabel,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium.copy(
              color = LimeAccent,
              fontWeight = FontWeight.Bold,
              fontSize = 12.5.sp
            )
          )
        }
      }
    }
  }
}


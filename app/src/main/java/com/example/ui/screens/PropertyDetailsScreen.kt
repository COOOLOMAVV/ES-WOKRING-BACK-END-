package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.Property
import com.example.ui.components.MetricFeatureItem
import com.example.ui.theme.*

@Composable
fun PropertyDetailsScreen(
  property: Property,
  selectedDate: String,
  selectedTimeSlot: String,
  onDateSelected: (String) -> Unit,
  onTimeSlotSelected: (String) -> Unit,
  onBackClick: () -> Unit,
  onFavoriteClick: () -> Unit,
  onBookAppointmentClick: () -> Unit,
  onInquiryClick: () -> Unit,
  onEditClick: (() -> Unit)? = null,
  onDeleteClick: (() -> Unit)? = null,
  onPlayVideoClick: ((String) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()
  var selectedMediaIndex by remember { mutableIntStateOf(0) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NaturalBg)
  ) {
    // Top navigation row: < Back to Listings, Share, Favorite
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable { onBackClick() }
          .padding(vertical = 6.dp, horizontal = 6.dp)
          .testTag("back_to_listings_button"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back to Listings",
          tint = NaturalDark,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Back to Listings",
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = NaturalDark,
            fontSize = 14.sp
          )
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        IconButton(
          onClick = {
            val sendIntent: Intent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(Intent.EXTRA_TEXT, "Check out this property on Estateflow: ${property.title} - ${property.priceFormatted} at ${property.address}")
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Share Property"))
          },
          modifier = Modifier.size(36.dp).testTag("share_button")
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            tint = TaupeDark,
            modifier = Modifier.size(20.dp)
          )
        }

        IconButton(
          onClick = onFavoriteClick,
          modifier = Modifier.size(36.dp).testTag("detail_favorite_button")
        ) {
          Icon(
            imageVector = if (property.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Bookmark",
            tint = if (property.isFavorite) RoseFavorite else TaupeDark,
            modifier = Modifier.size(20.dp)
          )
        }

        if (onEditClick != null) {
          IconButton(
            onClick = onEditClick,
            modifier = Modifier.size(36.dp).testTag("detail_edit_property_button")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Sanctuary",
              tint = ForestGreen,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        if (onDeleteClick != null) {
          IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(36.dp).testTag("detail_delete_property_button")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Delete Sanctuary",
              tint = RoseFavorite,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }

    // Header title and verified listing bar
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "PROPERTY LISTING",
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
            text = "Verified Listing",
            style = MaterialTheme.typography.labelSmall.copy(
              color = ForestGreen,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = property.title,
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 21.sp,
          color = NaturalDark
        )
      )
    }

    // Scrollable Property Content
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp)
    ) {
      Spacer(modifier = Modifier.height(8.dp))

      // Media Carousel / Hero with Photo & Video support
      val mediaList = property.mediaUris
      val currentMediaUri = mediaList.getOrNull(selectedMediaIndex) ?: mediaList.firstOrNull()
      val isCurrentVideo = currentMediaUri != null && Property.isVideoUri(currentMediaUri)
      val fallbackImageRes = if (property.imageResId != 0) property.imageResId else R.drawable.property_townhouse_1787131686098

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(230.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(SandMuted)
          .border(1.dp, SandWarm, RoundedCornerShape(20.dp))
      ) {
        if (!currentMediaUri.isNullOrBlank()) {
          AsyncImage(
            model = ImageRequest.Builder(context)
              .data(currentMediaUri)
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

        // Gradient overlay
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

        // Status tag
        Surface(
          modifier = Modifier
            .padding(12.dp)
            .align(Alignment.BottomStart),
          shape = RoundedCornerShape(8.dp),
          color = NaturalDark.copy(alpha = 0.85f)
        ) {
          Text(
            text = "${property.status} • For ${if (property.isRental) "Rent" else "Sale"}",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.sp
            )
          )
        }

        // Play button overlay if current media is a video
        if (isCurrentVideo && currentMediaUri != null) {
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .size(60.dp)
              .clip(CircleShape)
              .background(ForestGreen.copy(alpha = 0.92f))
              .border(2.dp, Color.White, CircleShape)
              .clickable { onPlayVideoClick?.invoke(currentMediaUri) }
              .testTag("play_hero_video_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Play Video Tour",
              tint = Color.White,
              modifier = Modifier.size(34.dp)
            )
          }
        }

        // Media counter chip at top right
        if (mediaList.isNotEmpty()) {
          Surface(
            modifier = Modifier
              .padding(12.dp)
              .align(Alignment.TopEnd),
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.65f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = if (isCurrentVideo) Icons.Default.Videocam else Icons.Default.PhotoLibrary,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
              )
              Text(
                text = "${selectedMediaIndex + 1} / ${mediaList.size}",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 11.sp)
              )
            }
          }
        }
      }

      // Thumbnail strip if there are multiple media items
      if (mediaList.size > 1) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          mediaList.forEachIndexed { index, uri ->
            val isSelected = index == selectedMediaIndex
            val isVid = Property.isVideoUri(uri)
            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                  width = if (isSelected) 2.dp else 1.dp,
                  color = if (isSelected) ForestGreen else SandWarm,
                  shape = RoundedCornerShape(10.dp)
                )
                .background(SandMuted)
                .clickable { selectedMediaIndex = index }
                .testTag("media_thumbnail_$index")
            ) {
              AsyncImage(
                model = ImageRequest.Builder(context)
                  .data(uri)
                  .crossfade(true)
                  .error(fallbackImageRes)
                  .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
              if (isVid) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Price & Address
      Text(
        text = property.priceFormatted,
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 26.sp,
          color = NaturalDark
        )
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = property.address,
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp,
          color = NaturalDark
        )
      )

      Text(
        text = property.cityStateZip,
        style = MaterialTheme.typography.bodySmall.copy(
          color = TaupeDark,
          fontSize = 13.sp
        )
      )

      Spacer(modifier = Modifier.height(18.dp))

      // 4 Metric Items Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val bedVal = if (property.beds % 1.0 == 0.0) "${property.beds.toInt()} Beds" else "${property.beds} Beds"
        val bathVal = if (property.baths % 1.0 == 0.0) "${property.baths.toInt()} Baths" else "${property.baths} Baths"

        MetricFeatureItem(
          icon = Icons.Default.Bed,
          value = bedVal,
          label = "Bedrooms",
          modifier = Modifier.weight(1f)
        )
        MetricFeatureItem(
          icon = Icons.Default.Bathtub,
          value = bathVal,
          label = "Bathrooms",
          modifier = Modifier.weight(1f)
        )
        MetricFeatureItem(
          icon = Icons.Default.SquareFoot,
          value = "${String.format("%,d", property.sqft)} sqft",
          label = "Total Area",
          modifier = Modifier.weight(1f)
        )
        MetricFeatureItem(
          icon = Icons.Default.Home,
          value = property.propertyType,
          label = "Property Type",
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Description Section
      Text(
        text = "DESCRIPTION",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          color = NaturalDark,
          fontSize = 12.sp,
          letterSpacing = 1.sp
        )
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = property.description,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = TaupeDark,
          fontSize = 13.5.sp,
          lineHeight = 20.sp
        )
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Key Amenities
      Text(
        text = "KEY AMENITIES",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          color = NaturalDark,
          fontSize = 12.sp,
          letterSpacing = 1.sp
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        property.amenities.forEach { amenity ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = amenity,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = NaturalDark,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Available Dates Section
      Text(
        text = "AVAILABLE DATES",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          color = NaturalDark,
          fontSize = 12.sp,
          letterSpacing = 1.sp
        )
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        property.availableDates.forEach { date ->
          val isSelected = date == selectedDate
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable { onDateSelected(date) }
              .testTag("date_chip_$date"),
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) ForestGreen else Color.White,
            border = androidx.compose.foundation.BorderStroke(
              width = 1.dp,
              color = if (isSelected) ForestGreen else SandWarm
            )
          ) {
            Text(
              text = date,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else NaturalDark,
                fontSize = 13.sp
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Available Time Slots Section
      Text(
        text = "AVAILABLE TIME SLOTS",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          color = NaturalDark,
          fontSize = 12.sp,
          letterSpacing = 1.sp
        )
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        property.availableTimeSlots.forEach { timeSlot ->
          val isSelected = timeSlot == selectedTimeSlot
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable { onTimeSlotSelected(timeSlot) }
              .testTag("time_chip_$timeSlot"),
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) ForestGreen else Color.White,
            border = androidx.compose.foundation.BorderStroke(
              width = 1.dp,
              color = if (isSelected) ForestGreen else SandWarm
            )
          ) {
            Text(
              text = timeSlot,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else NaturalDark,
                fontSize = 13.sp
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Agent Contact Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Image(
            painter = painterResource(id = R.drawable.agent_sarah_avatar_1787131767100),
            contentDescription = property.agentName,
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .border(1.dp, SandWarm, CircleShape),
            contentScale = ContentScale.Crop
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = property.agentName,
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = NaturalDark
              )
            )
            Text(
              text = property.agentTitle,
              style = MaterialTheme.typography.bodySmall.copy(
                color = TaupeDark,
                fontSize = 11.5.sp
              )
            )
          }
          IconButton(
            onClick = onInquiryClick,
            modifier = Modifier
              .size(38.dp)
              .background(SandMuted, CircleShape)
              .border(1.dp, SandWarm, CircleShape)
              .testTag("agent_chat_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Chat,
              contentDescription = "Message Agent",
              tint = ForestGreen,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Primary Book Viewing CTA Button in ForestGreen
      Button(
        onClick = onBookAppointmentClick,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("book_viewing_appointment_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
      ) {
        Text(
          text = "Book a Viewing Appointment",
          style = MaterialTheme.typography.titleMedium.copy(
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Secondary Send Inquiry Button
      OutlinedButton(
        onClick = onInquiryClick,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("send_inquiry_button"),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = NaturalDark)
      ) {
        Text(
          text = "Send Inquiry / Ask Question",
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
          )
        )
      }

      if (onEditClick != null || onDeleteClick != null) {
        Spacer(modifier = Modifier.height(16.dp))

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SandMuted),
          border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "SANCTUARY MANAGEMENT",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TaupeMuted,
                fontSize = 11.sp,
                letterSpacing = 1.sp
              )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              if (onEditClick != null) {
                OutlinedButton(
                  onClick = onEditClick,
                  modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("manage_edit_property_button"),
                  shape = RoundedCornerShape(10.dp),
                  border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen)
                ) {
                  Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Edit Listing", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
              }

              if (onDeleteClick != null) {
                OutlinedButton(
                  onClick = onDeleteClick,
                  modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("manage_delete_property_button"),
                  shape = RoundedCornerShape(10.dp),
                  border = androidx.compose.foundation.BorderStroke(1.dp, RoseFavorite),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseFavorite)
                ) {
                  Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Remove", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

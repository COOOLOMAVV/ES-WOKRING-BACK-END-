package com.example.ui.dialogs

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.Property
import com.example.data.model.ViewingAppointment
import com.example.ui.theme.*

@Composable
fun estateDialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedTextColor = NaturalDark,
  unfocusedTextColor = NaturalDark,
  disabledTextColor = NaturalDark.copy(alpha = 0.6f),
  focusedContainerColor = Color(0xFFFAF8F5),
  unfocusedContainerColor = Color.White,
  disabledContainerColor = Color(0xFFF3EFE6),
  focusedBorderColor = ForestGreen,
  unfocusedBorderColor = Color(0xFFC8C2B3),
  focusedLabelColor = ForestGreen,
  unfocusedLabelColor = TaupeDark,
  cursorColor = ForestGreen,
  focusedPlaceholderColor = TaupeMuted,
  unfocusedPlaceholderColor = TaupeMuted
)

@Composable
fun BookingDialog(
  property: Property,
  selectedDate: String,
  selectedTimeSlot: String,
  clientName: String,
  clientPhone: String,
  clientEmail: String,
  notes: String,
  onNameChange: (String) -> Unit,
  onPhoneChange: (String) -> Unit,
  onEmailChange: (String) -> Unit,
  onNotesChange: (String) -> Unit,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .padding(horizontal = 16.dp, vertical = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(min = 300.dp, max = 480.dp)
          .heightIn(max = 640.dp)
          .testTag("booking_confirmation_dialog"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Schedule Sanctuary Tour",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = NaturalDark,
              fontSize = 20.sp
            )
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TaupeMuted)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selected Time & Property Summary
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SandMuted,
          border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = property.title,
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = NaturalDark
              )
            )
            Text(
              text = property.address,
              style = MaterialTheme.typography.bodySmall.copy(color = TaupeDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "$selectedDate at $selectedTimeSlot",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = ForestGreen
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contact info fields
        Text(
          "YOUR CONTACT DETAILS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = TaupeMuted,
            letterSpacing = 1.sp
          )
        )
        Spacer(modifier = Modifier.height(8.dp))

        val inputColors = estateDialogTextFieldColors()

        OutlinedTextField(
          value = clientName,
          onValueChange = onNameChange,
          label = { Text("Full Name") },
          modifier = Modifier.fillMaxWidth().testTag("booking_name_input"),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = clientPhone,
          onValueChange = onPhoneChange,
          label = { Text("Phone Number") },
          modifier = Modifier.fillMaxWidth().testTag("booking_phone_input"),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = clientEmail,
          onValueChange = onEmailChange,
          label = { Text("Email Address") },
          modifier = Modifier.fillMaxWidth().testTag("booking_email_input"),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = notes,
          onValueChange = onNotesChange,
          label = { Text("Special Requests / Notes (Optional)") },
          modifier = Modifier.fillMaxWidth().testTag("booking_notes_input"),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          maxLines = 2
        )

        Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = onConfirm,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("confirm_booking_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Confirm Viewing Appointment", fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }
  }
}

@Composable
fun BookingSuccessDialog(
  appointment: ViewingAppointment?,
  onDismiss: () -> Unit
) {
  if (appointment == null) return

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(min = 280.dp, max = 420.dp)
          .testTag("booking_success_dialog"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Column(
          modifier = Modifier.padding(22.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .background(ForestGreenLight, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = ForestGreen,
            modifier = Modifier.size(38.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
          shape = RoundedCornerShape(20.dp),
          color = ForestGreen.copy(alpha = 0.12f),
          modifier = Modifier.padding(bottom = 4.dp)
        ) {
          Text(
            text = "TOUR RESERVED & CALENDAR SYNCED",
            style = MaterialTheme.typography.labelSmall.copy(
              color = ForestGreen,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp,
              letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Viewing Confirmed!",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = NaturalDark
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Your viewing appointment for ${appointment.propertyTitle} has been registered with Sarah Jenkins.",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = TaupeDark,
            fontSize = 13.sp
          ),
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SandMuted,
          border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Appointment Details",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TaupeMuted)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Date & Time: ${appointment.date} • ${appointment.timeSlot}",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = NaturalDark)
            )
            Text(
              text = "Location: ${appointment.propertyAddress}",
              style = MaterialTheme.typography.bodySmall.copy(color = TaupeDark)
            )
            Text(
              text = "Attendee: ${appointment.clientName}",
              style = MaterialTheme.typography.bodySmall.copy(color = TaupeDark)
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            modifier = Modifier.fillMaxWidth().testTag("close_success_dialog_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Done", fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }
  }
}

@Composable
fun PropertyMediaPickerCard(
  mediaUris: List<String>,
  onMediaAdded: (List<String>) -> Unit,
  onMediaRemoved: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var urlInput by remember { mutableStateOf("") }
  var showUrlInput by remember { mutableStateOf(false) }

  val mediaPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickMultipleVisualMedia(),
    onResult = { uris ->
      if (uris.isNotEmpty()) {
        onMediaAdded(uris.map { it.toString() })
      }
    }
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(SandMuted)
      .border(1.dp, SandWarm, RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AddPhotoAlternate,
          contentDescription = null,
          tint = ForestGreen,
          modifier = Modifier.size(20.dp)
        )
        Column {
          Text(
            text = "PROPERTY MEDIA",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = ForestGreen,
              letterSpacing = 1.sp
            )
          )
          Text(
            text = "Photos & Video Walkthrough (${mediaUris.size} attached)",
            style = MaterialTheme.typography.bodySmall.copy(
              color = TaupeDark,
              fontSize = 11.5.sp
            )
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Action buttons row: Device Import & URL Link
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = {
          mediaPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
          )
        },
        modifier = Modifier
          .weight(1.3f)
          .height(40.dp)
          .testTag("pick_device_media_button"),
        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
        shape = RoundedCornerShape(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AddPhotoAlternate,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Select Photos/Videos",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 11.5.sp
          )
        )
      }

      OutlinedButton(
        onClick = { showUrlInput = !showUrlInput },
        modifier = Modifier
          .weight(0.9f)
          .height(40.dp)
          .testTag("toggle_url_media_button"),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Link,
          contentDescription = null,
          tint = ForestGreen,
          modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (showUrlInput) "Hide Link" else "+ Link URL",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = ForestGreen,
            fontSize = 11.sp
          )
        )
      }
    }

    // Quick Sample Tour Presets button
    Spacer(modifier = Modifier.height(6.dp))
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(Color.White)
        .border(1.dp, SandWarm, RoundedCornerShape(8.dp))
        .clickable {
          val samples = listOf(
            "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1200&q=80",
            "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=1200&q=80",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
          )
          onMediaAdded(samples)
        }
        .padding(horizontal = 10.dp, vertical = 6.dp)
        .testTag("add_sample_tour_media_button"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = Icons.Default.VideoLibrary,
        contentDescription = null,
        tint = ForestGreen,
        modifier = Modifier.size(15.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "+ Add Sample Villa Photos & Video Tour",
        style = MaterialTheme.typography.labelSmall.copy(
          color = ForestGreen,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      )
    }

    if (showUrlInput) {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        OutlinedTextField(
          value = urlInput,
          onValueChange = { urlInput = it },
          label = { Text("Paste Image or Video URL", fontSize = 11.sp) },
          modifier = Modifier.weight(1f).testTag("url_media_input"),
          shape = RoundedCornerShape(8.dp),
          colors = estateDialogTextFieldColors(),
          singleLine = true
        )
        Button(
          onClick = {
            if (urlInput.isNotBlank()) {
              onMediaAdded(listOf(urlInput.trim()))
              urlInput = ""
              showUrlInput = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(48.dp).testTag("submit_url_media_button")
        ) {
          Text("Add", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Attached Media Thumbnails
    if (mediaUris.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(Color.White)
          .border(1.dp, SandWarm, RoundedCornerShape(10.dp))
          .padding(vertical = 18.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.PermMedia,
            contentDescription = null,
            tint = TaupeMuted,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "No images or videos attached yet",
            style = MaterialTheme.typography.bodySmall.copy(
              color = TaupeMuted,
              fontWeight = FontWeight.Medium,
              fontSize = 11.5.sp
            )
          )
          Text(
            text = "Import files or paste links to showcase your property",
            style = MaterialTheme.typography.labelSmall.copy(
              color = TaupeDark,
              fontSize = 10.5.sp
            )
          )
        }
      }
    } else {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        mediaUris.forEachIndexed { index, uri ->
          val isVideo = Property.isVideoUri(uri)
          Box(
            modifier = Modifier
              .size(90.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(NaturalDark)
              .border(1.5.dp, SandWarm, RoundedCornerShape(12.dp))
              .testTag("media_preview_item_$index")
          ) {
            if (isVideo) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(ForestGreenDark),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.PlayCircleFilled,
                  contentDescription = "Video",
                  tint = SandWarm,
                  modifier = Modifier.size(32.dp)
                )
              }
            } else {
              AsyncImage(
                model = uri,
                contentDescription = "Property media $index",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
            }

            // Tag badge
            Surface(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp),
              color = Color.Black.copy(alpha = 0.75f),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = if (isVideo) "VIDEO" else "PHOTO",
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color.White,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              )
            }

            // Delete X button
            Box(
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(RoseFavorite)
                .clickable { onMediaRemoved(uri) }
                .testTag("remove_media_button_$index"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove Media",
                tint = Color.White,
                modifier = Modifier.size(13.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun AddPropertyDialog(
  onDismiss: () -> Unit,
  onAddProperty: (
    title: String,
    address: String,
    cityStateZip: String,
    price: Long,
    isRental: Boolean,
    beds: Double,
    baths: Double,
    sqft: Int,
    propertyType: String,
    description: String,
    amenities: List<String>,
    mediaUris: List<String>
  ) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var cityStateZip by remember { mutableStateOf("Cebu City, Cebu • 6000") }
  var priceText by remember { mutableStateOf("") }
  var isRental by remember { mutableStateOf(false) }
  var bedsText by remember { mutableStateOf("3") }
  var bathsText by remember { mutableStateOf("2.5") }
  var sqftText by remember { mutableStateOf("1500") }
  var propertyType by remember { mutableStateOf("Townhouse") }
  var description by remember { mutableStateOf("") }
  var amenitiesText by remember { mutableStateOf("Rooftop Deck, Garage Parking, Central AC") }
  var mediaUris by remember { mutableStateOf<List<String>>(emptyList()) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val propertyTypes = listOf("Townhouse", "Houses", "Apartments", "Condos", "Loft")
  val inputColors = estateDialogTextFieldColors()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .padding(horizontal = 16.dp, vertical = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(min = 320.dp, max = 500.dp)
          .heightIn(max = 640.dp)
          .testTag("add_property_dialog"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Publish New Sanctuary",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = NaturalDark,
              fontSize = 20.sp
            )
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TaupeMuted)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it; errorMessage = null },
          label = { Text("Listing Title (e.g. Modern Coastal Villa)") },
          modifier = Modifier.fillMaxWidth().testTag("add_prop_title"),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Street Address (e.g. Cebu IT Park, Lahug)") },
          modifier = Modifier.fillMaxWidth().testTag("add_prop_address"),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = cityStateZip,
          onValueChange = { cityStateZip = it },
          label = { Text("City, Province/State & Postal/Zip") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = { Text(if (isRental) "Rent (₱/mo)" else "Price (₱)") },
            modifier = Modifier.weight(1f).testTag("add_prop_price"),
            shape = RoundedCornerShape(10.dp),
            colors = inputColors,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )

          Column(modifier = Modifier.weight(1f)) {
            Text("Listing Type", style = MaterialTheme.typography.labelSmall.copy(color = TaupeMuted))
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { isRental = false },
                color = if (!isRental) ForestGreen else SandMuted,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (!isRental) ForestGreen else SandWarm)
              ) {
                Text(
                  "Sale",
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  color = if (!isRental) Color.White else NaturalDark,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { isRental = true },
                color = if (isRental) ForestGreen else SandMuted,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isRental) ForestGreen else SandWarm)
              ) {
                Text(
                  "Rent",
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  color = if (isRental) Color.White else NaturalDark,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = bedsText,
            onValueChange = { bedsText = it },
            label = { Text("Beds") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = inputColors,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
          OutlinedTextField(
            value = bathsText,
            onValueChange = { bathsText = it },
            label = { Text("Baths") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = inputColors,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
          OutlinedTextField(
            value = sqftText,
            onValueChange = { sqftText = it },
            label = { Text("Sqft") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = inputColors,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("PROPERTY CATEGORY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TaupeMuted))
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          propertyTypes.forEach { type ->
            val isSelected = propertyType == type
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { propertyType = type },
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) ForestGreen else Color.White,
              border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ForestGreen else SandWarm)
            ) {
              Text(
                text = type,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                color = if (isSelected) Color.White else NaturalDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Property Media Section: User/Agent imports device photos/videos or provides URLs
        PropertyMediaPickerCard(
          mediaUris = mediaUris,
          onMediaAdded = { newItems ->
            mediaUris = (mediaUris + newItems).distinct()
            errorMessage = null
          },
          onMediaRemoved = { removedUri ->
            mediaUris = mediaUris.filter { it != removedUri }
          }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description & Selling Highlights") },
          modifier = Modifier.fillMaxWidth().testTag("add_prop_description"),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
          maxLines = 3
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = amenitiesText,
          onValueChange = { amenitiesText = it },
          label = { Text("Amenities (comma separated)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
          singleLine = true
        )

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = errorMessage ?: "",
            color = RoseFavorite,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = {
              if (mediaUris.isEmpty()) {
                errorMessage = "Please import or add at least one photo or video for this listing."
                return@Button
              }

              val price = priceText.toLongOrNull() ?: if (isRental) 65000L else 9500000L
              val beds = bedsText.toDoubleOrNull() ?: 3.0
              val baths = bathsText.toDoubleOrNull() ?: 2.0
              val sqft = sqftText.toIntOrNull() ?: 1500
              val amenitiesList = amenitiesText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

              onAddProperty(
                title.ifBlank { "Contemporary Villa & Residence" },
                address.ifBlank { "Salinas Drive, Lahug" },
                cityStateZip.ifBlank { "Cebu City, Cebu • 6000" },
                price,
                isRental,
                beds,
                baths,
                sqft,
                propertyType,
                description.ifBlank { "Contemporary architectural sanctuary with designer touches, high efficiency appliances, and prime central location in Cebu City." },
                amenitiesList,
                mediaUris
              )
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_add_property_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Publish Listing", fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }
  }
}

@Composable
fun EditPropertyDialog(
  property: Property,
  onDismiss: () -> Unit,
  onUpdateProperty: (Property) -> Unit,
  onDeleteProperty: () -> Unit
) {
  var title by remember { mutableStateOf(property.title) }
  var address by remember { mutableStateOf(property.address) }
  var cityStateZip by remember { mutableStateOf(property.cityStateZip) }
  var priceText by remember { mutableStateOf(property.price.toString()) }
  var isRental by remember { mutableStateOf(property.isRental) }
  var bedsText by remember { mutableStateOf(property.beds.toString()) }
  var bathsText by remember { mutableStateOf(property.baths.toString()) }
  var sqftText by remember { mutableStateOf(property.sqft.toString()) }
  var propertyType by remember { mutableStateOf(property.propertyType) }
  var status by remember { mutableStateOf(property.status) }
  var description by remember { mutableStateOf(property.description) }
  var amenitiesText by remember { mutableStateOf(property.amenities.joinToString(", ")) }
  var mediaUris by remember { mutableStateOf(property.mediaUris) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val propertyTypes = listOf("Townhouse", "Houses", "Apartments", "Condos", "Loft")
  val statusOptions = listOf("Active", "Under Offer", "Pending", "Sold")
  val inputColors = estateDialogTextFieldColors()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .padding(horizontal = 16.dp, vertical = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(min = 320.dp, max = 500.dp)
          .heightIn(max = 640.dp)
          .testTag("edit_property_dialog"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = null,
              tint = ForestGreen,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Edit Property Listing",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = NaturalDark,
                fontSize = 19.sp
              )
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TaupeMuted)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it; errorMessage = null },
          label = { Text("Listing Title") },
          modifier = Modifier.fillMaxWidth().testTag("edit_prop_title"),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Street Address") },
          modifier = Modifier.fillMaxWidth().testTag("edit_prop_address"),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = cityStateZip,
          onValueChange = { cityStateZip = it },
          label = { Text("City, Province/State & Postal/Zip") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Status Selector
        Text(
          text = "LISTING STATUS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = TaupeMuted,
            fontSize = 10.sp,
            letterSpacing = 1.sp
          )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          statusOptions.forEach { opt ->
            val isSelected = status == opt
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { status = opt },
              color = if (isSelected) ForestGreen else Color.White,
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ForestGreen else SandWarm)
            ) {
              Text(
                text = opt,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = if (isSelected) Color.White else NaturalDark
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // For Sale vs Rent toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .clickable { isRental = false },
            color = if (!isRental) ForestGreen else SandMuted,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (!isRental) ForestGreen else SandWarm)
          ) {
            Text(
              text = "For Sale",
              modifier = Modifier.padding(vertical = 10.dp),
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (!isRental) Color.White else NaturalDark
              )
            )
          }

          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .clickable { isRental = true },
            color = if (isRental) ForestGreen else SandMuted,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isRental) ForestGreen else SandWarm)
          ) {
            Text(
              text = "For Rent",
              modifier = Modifier.padding(vertical = 10.dp),
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isRental) Color.White else NaturalDark
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = priceText,
          onValueChange = { priceText = it },
          label = { Text(if (isRental) "Monthly Rent (₱)" else "Price (₱)") },
          modifier = Modifier.fillMaxWidth().testTag("edit_prop_price"),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = bedsText,
            onValueChange = { bedsText = it },
            label = { Text("Beds") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = inputColors,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
          OutlinedTextField(
            value = bathsText,
            onValueChange = { bathsText = it },
            label = { Text("Baths") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = inputColors,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
          OutlinedTextField(
            value = sqftText,
            onValueChange = { sqftText = it },
            label = { Text("Sqft") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = inputColors,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "PROPERTY TYPE",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = TaupeMuted,
            fontSize = 10.sp,
            letterSpacing = 1.sp
          )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          propertyTypes.forEach { type ->
            val isSelected = propertyType == type
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { propertyType = type },
              color = if (isSelected) ForestGreen else Color.White,
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ForestGreen else SandWarm)
            ) {
              Text(
                text = type,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = if (isSelected) Color.White else NaturalDark
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Media Picker Section (Manage & add photos/videos)
        PropertyMediaPickerCard(
          mediaUris = mediaUris,
          onMediaAdded = { newItems ->
            mediaUris = (mediaUris + newItems).distinct()
            errorMessage = null
          },
          onMediaRemoved = { removedUri ->
            mediaUris = mediaUris.filter { it != removedUri }
          }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description & Selling Highlights") },
          modifier = Modifier.fillMaxWidth().testTag("edit_prop_description"),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
          maxLines = 3
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = amenitiesText,
          onValueChange = { amenitiesText = it },
          label = { Text("Amenities (comma separated)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = inputColors,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = NaturalDark, fontSize = 14.sp),
          singleLine = true
        )

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = errorMessage ?: "",
            color = RoseFavorite,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons: Save & Delete
        Button(
          onClick = {
            val price = priceText.toLongOrNull() ?: property.price
            val beds = bedsText.toDoubleOrNull() ?: property.beds
            val baths = bathsText.toDoubleOrNull() ?: property.baths
            val sqft = sqftText.toIntOrNull() ?: property.sqft
            val amenitiesList = amenitiesText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            val updated = property.copy(
              title = title.ifBlank { property.title },
              address = address.ifBlank { property.address },
              cityStateZip = cityStateZip.ifBlank { property.cityStateZip },
              price = price,
              isRental = isRental,
              beds = beds,
              baths = baths,
              sqft = sqft,
              propertyType = propertyType,
              status = status,
              description = description.ifBlank { property.description },
              amenities = if (amenitiesList.isEmpty()) property.amenities else amenitiesList,
              mediaUris = mediaUris
            )
            onUpdateProperty(updated)
          },
          modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_property_changes_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Save Changes", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

          OutlinedButton(
            onClick = onDeleteProperty,
            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("delete_property_from_dialog_button"),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, RoseFavorite)
          ) {
            Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = RoseFavorite, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Delete This Listing", color = RoseFavorite, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}

@Composable
fun DeletePropertyConfirmationDialog(
  property: Property,
  onDismiss: () -> Unit,
  onConfirmDelete: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(min = 280.dp, max = 420.dp)
          .testTag("delete_property_dialog"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Box(
          modifier = Modifier
            .size(54.dp)
            .background(RoseFavorite.copy(alpha = 0.15f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = null,
            tint = RoseFavorite,
            modifier = Modifier.size(28.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Remove Property Listing?",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = NaturalDark,
            fontSize = 18.sp
          ),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Are you sure you want to permanently remove \"${property.title}\" from active listings? This will also remove any related scheduled viewing slots.",
          style = MaterialTheme.typography.bodySmall.copy(
            color = TaupeDark,
            fontSize = 13.sp,
            lineHeight = 18.sp
          ),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onDismiss,
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("cancel_delete_property_button"),
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
            ) {
              Text("Cancel", color = TaupeDark, fontWeight = FontWeight.SemiBold)
            }

            Button(
              onClick = onConfirmDelete,
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("confirm_delete_property_button"),
              colors = ButtonDefaults.buttonColors(containerColor = RoseFavorite),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Remove", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
fun PropertyVideoPlayerDialog(
  videoUri: String,
  title: String,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(min = 320.dp, max = 520.dp)
          .clip(RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
      ) {
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
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = Icons.Default.Videocam,
              contentDescription = null,
              tint = SandWarm,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = title,
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.DarkGray),
          contentAlignment = Alignment.Center
        ) {
          AndroidView(
            factory = { ctx ->
              android.widget.VideoView(ctx).apply {
                val uri = android.net.Uri.parse(videoUri)
                setVideoURI(uri)
                val mediaController = android.widget.MediaController(ctx)
                mediaController.setAnchorView(this)
                setMediaController(mediaController)
                setOnPreparedListener { mp ->
                  mp.isLooping = true
                  start()
                }
              }
            },
            modifier = Modifier.fillMaxSize()
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Interactive Video Tour",
              style = MaterialTheme.typography.bodySmall.copy(color = SandWarm, fontSize = 12.sp)
            )
            OutlinedButton(
              onClick = {
                try {
                  val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                    setDataAndType(android.net.Uri.parse(videoUri), "video/*")
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                  }
                  context.startActivity(intent)
                } catch (e: Exception) {
                  // fall through
                }
              },
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(Icons.Default.OpenInNew, contentDescription = null, tint = SandWarm, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("External Player", color = SandWarm, fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
fun FilterBottomSheet(
  minPrice: Long,
  maxPrice: Long,
  minBeds: Double,
  onApplyFilters: (Long, Long, Double) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedBeds by remember { mutableStateOf(minBeds) }
  var priceRange by remember {
    mutableStateOf(minPrice.toFloat().coerceAtLeast(0f)..maxPrice.toFloat().coerceAtMost(50000000f))
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .padding(horizontal = 16.dp, vertical = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(min = 300.dp, max = 460.dp)
          .heightIn(max = 620.dp)
          .testTag("filter_dialog"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Filter Properties",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = NaturalDark,
              fontSize = 20.sp
            )
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TaupeMuted)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "PRICE RANGE",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = TaupeMuted,
            letterSpacing = 1.sp
          )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "₱${String.format("%,d", priceRange.start.toLong())} - ₱${String.format("%,d", priceRange.endInclusive.toLong())}",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = NaturalDark
          )
        )

        RangeSlider(
          value = priceRange,
          onValueChange = { priceRange = it },
          valueRange = 0f..50000000f,
          steps = 49,
          colors = SliderDefaults.colors(
            thumbColor = ForestGreen,
            activeTrackColor = ForestGreen,
            inactiveTrackColor = SandWarm
          ),
          modifier = Modifier.testTag("price_range_slider")
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "MINIMUM BEDROOMS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = TaupeMuted,
            letterSpacing = 1.sp
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(0.0 to "Any", 1.0 to "1+", 2.0 to "2+", 3.0 to "3+", 4.0 to "4+").forEach { (beds, label) ->
            val isSelected = selectedBeds == beds
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable { selectedBeds = beds }
                .testTag("bed_filter_$label"),
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) ForestGreen else Color.White,
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) ForestGreen else SandWarm
              )
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else NaturalDark,
                    fontSize = 12.sp
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = {
                onApplyFilters(0L, 50_000_000L, 0.0)
              },
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
              modifier = Modifier.weight(1f)
            ) {
              Text("Reset", color = TaupeDark)
            }

            Button(
              onClick = {
                onApplyFilters(
                  priceRange.start.toLong(),
                  priceRange.endInclusive.toLong(),
                  selectedBeds
                )
              },
              colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1.5f).testTag("apply_filters_button")
            ) {
              Text("Apply Filters", fontWeight = FontWeight.Bold, color = Color.White)
            }
          }
        }
      }
    }
  }
}

@Composable
fun InquiryDialog(
  property: Property,
  initialMessage: String,
  onSendMessage: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var messageText by remember { mutableStateOf(initialMessage) }
  val quickQuestions = listOf(
    "Is this property still available for private tour?",
    "Can we book an in-person walkthrough this week?",
    "Could you send me the strata prospectus and floor plans?",
    "What are the estimated annual property taxes?"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .padding(horizontal = 16.dp, vertical = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(min = 300.dp, max = 480.dp)
          .heightIn(max = 640.dp)
          .testTag("inquiry_dialog"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Ask Agent a Question",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = NaturalDark,
              fontSize = 20.sp
            )
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TaupeMuted)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SandMuted,
          border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = property.title,
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = NaturalDark
                )
              )
              Text(
                text = "Contact: ${property.agentName} • ${property.priceFormatted}",
                style = MaterialTheme.typography.bodySmall.copy(color = TaupeDark)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "FREQUENT QUESTIONS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = TaupeMuted,
            letterSpacing = 1.sp
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          quickQuestions.forEach { question ->
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { messageText = question },
              shape = RoundedCornerShape(8.dp),
              color = SandMuted.copy(alpha = 0.6f),
              border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
            ) {
              Text(
                text = question,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                  color = NaturalDark,
                  fontSize = 12.sp
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "YOUR INQUIRY MESSAGE",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = TaupeMuted,
            letterSpacing = 1.sp
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = messageText,
          onValueChange = { messageText = it },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("inquiry_message_input"),
          shape = RoundedCornerShape(10.dp),
          colors = estateDialogTextFieldColors(),
          minLines = 3,
          maxLines = 5
        )

        Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = {
              if (messageText.isNotBlank()) {
                onSendMessage(messageText.trim())
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("send_inquiry_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Send Inquiry to Agent", fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }
  }
}


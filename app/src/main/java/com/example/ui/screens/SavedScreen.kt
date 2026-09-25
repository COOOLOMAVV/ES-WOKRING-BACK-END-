package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Property
import com.example.ui.components.PropertyCard
import com.example.ui.theme.*

@Composable
fun SavedScreen(
  savedProperties: List<Property>,
  onPropertyClick: (Property) -> Unit,
  onFavoriteClick: (String) -> Unit,
  onExploreClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NaturalBg)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      Text(
        text = "Saved Properties",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = NaturalDark,
          fontSize = 22.sp
        )
      )
      Text(
        text = "${savedProperties.size} bookmarked homes in your sanctuary",
        style = MaterialTheme.typography.bodySmall.copy(
          color = TaupeDark,
          fontSize = 12.sp
        )
      )
    }

    HorizontalDivider(color = SandWarm, thickness = 1.dp)

    if (savedProperties.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .background(SandMuted, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.FavoriteBorder,
              contentDescription = null,
              tint = TaupeMuted,
              modifier = Modifier.size(32.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "No saved properties yet",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = NaturalDark
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Tap the bookmark or favorite icon on any property to save it here.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TaupeMuted),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(18.dp))
          Button(
            onClick = onExploreClick,
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("explore_listings_button")
          ) {
            Text("Explore Listings", fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        items(
          items = savedProperties,
          key = { it.id },
          contentType = { "property_card" }
        ) { property ->
          PropertyCard(
            property = property,
            onClick = { onPropertyClick(property) },
            onFavoriteClick = { onFavoriteClick(property.id) }
          )
        }
      }
    }
  }
}

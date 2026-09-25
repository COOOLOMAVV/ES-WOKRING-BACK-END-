package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Property
import com.example.ui.components.PropertyCard
import com.example.ui.theme.*

@Composable
fun SearchScreen(
  properties: List<Property>,
  searchQuery: String,
  selectedCategory: String,
  onSearchQueryChange: (String) -> Unit,
  onCategorySelect: (String) -> Unit,
  onPropertyClick: (Property) -> Unit,
  onFavoriteClick: (String) -> Unit,
  onOpenFilterSheet: () -> Unit,
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current
  val quickTags = listOf("Cebu IT Park", "Lahug", "Cebu Business Park", "Mabolo", "Banilad", "Kasambagan", "Guadalupe", "Sea View", "Balcony")

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NaturalBg)
  ) {
    // Search Header
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      Text(
        text = "Find Your Sanctuary",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = NaturalDark,
          fontSize = 22.sp
        )
      )
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White)
          .border(1.dp, SandWarm, RoundedCornerShape(16.dp))
          .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = TaupeMuted,
          modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChange,
          modifier = Modifier
            .weight(1f)
            .testTag("search_screen_input"),
          placeholder = {
            Text(
              text = "Cebu City, IT Park, or features...",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = TaupeMuted,
                fontSize = 14.sp
              )
            )
          },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = NaturalDark,
            unfocusedTextColor = NaturalDark,
            cursorColor = ForestGreen,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
          ),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
          keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
        )

        if (searchQuery.isNotEmpty()) {
          IconButton(
            onClick = { onSearchQueryChange("") },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Clear,
              contentDescription = "Clear",
              tint = TaupeMuted,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(ForestGreen)
            .clickable { onOpenFilterSheet() }
            .testTag("search_filter_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Filters",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // Quick neighborhood / feature tags
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      quickTags.forEach { tag ->
        val isActive = searchQuery.equals(tag, ignoreCase = true)
        val tagBg by animateColorAsState(
          targetValue = if (isActive) ForestGreen else Color.White,
          label = "tag_bg_$tag"
        )
        val tagTextColor by animateColorAsState(
          targetValue = if (isActive) Color.White else NaturalDark,
          label = "tag_text_$tag"
        )
        val tagBorderColor by animateColorAsState(
          targetValue = if (isActive) ForestGreen else SandWarm,
          label = "tag_border_$tag"
        )

        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable {
              if (isActive) onSearchQueryChange("") else onSearchQueryChange(tag)
            },
          shape = RoundedCornerShape(14.dp),
          color = tagBg,
          border = androidx.compose.foundation.BorderStroke(1.dp, tagBorderColor)
        ) {
          Text(
            text = tag,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              color = tagTextColor,
              fontSize = 11.5.sp,
              fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
            )
          )
        }
      }
    }

    HorizontalDivider(color = SandWarm, thickness = 1.dp, modifier = Modifier.padding(top = 4.dp))

    // Results count
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "${properties.size} Properties Available",
        style = MaterialTheme.typography.bodySmall.copy(
          fontWeight = FontWeight.SemiBold,
          color = TaupeDark
        )
      )
    }

    // Results list
    if (properties.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "No properties match your criteria",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = NaturalDark
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Try clearing filters or searching for another city.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TaupeMuted)
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        items(
          items = properties,
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

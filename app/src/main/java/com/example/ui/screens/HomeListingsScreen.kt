package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Property
import com.example.ui.components.PropertyCard
import com.example.ui.theme.*

@Composable
fun HomeListingsScreen(
  properties: List<Property>,
  searchQuery: String,
  selectedCategory: String,
  onSearchQueryChange: (String) -> Unit,
  onCategorySelect: (String) -> Unit,
  onPropertyClick: (Property) -> Unit,
  onFavoriteClick: (String) -> Unit,
  onOpenFilterSheet: () -> Unit,
  onAddPropertyClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current
  val categories = listOf("All", "Houses", "Apartments", "Condos", "Townhouses")

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NaturalBg)
  ) {
    // Natural Tones Search Bar Container
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
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
            .testTag("search_text_input"),
          placeholder = {
            Text(
              text = "Search Cebu City homes, condos...",
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

        // Green Tune Filter Icon Box matching Natural Tones design
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(ForestGreen)
            .clickable { onOpenFilterSheet() }
            .testTag("filter_icon_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Filter",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // Category Tabs row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      categories.forEach { category ->
        val isSelected = selectedCategory.equals(category, ignoreCase = true)
        val tabBgColor by animateColorAsState(
          targetValue = if (isSelected) ForestGreen else Color.White,
          label = "tab_bg_$category"
        )
        val tabTextColor by animateColorAsState(
          targetValue = if (isSelected) Color.White else NaturalDark,
          label = "tab_text_$category"
        )
        val tabBorderColor by animateColorAsState(
          targetValue = if (isSelected) ForestGreen else SandWarm,
          label = "tab_border_$category"
        )

        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCategorySelect(category) }
            .testTag("category_tab_$category"),
          shape = RoundedCornerShape(12.dp),
          color = tabBgColor,
          border = androidx.compose.foundation.BorderStroke(1.dp, tabBorderColor)
        ) {
          Text(
            text = category,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = tabTextColor,
              fontSize = 12.5.sp
            )
          )
        }
      }
    }

    // Section header: "Popular Properties" & "View All"
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Popular Properties",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = NaturalDark
        )
      )
      Text(
        text = "View All",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = ForestGreen
        ),
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .clickable { onCategorySelect("All") }
          .padding(horizontal = 8.dp, vertical = 4.dp)
      )
    }

    // Listings list
    if (properties.isEmpty()) {
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
              .size(56.dp)
              .background(SandMuted, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Home,
              contentDescription = null,
              tint = TaupeMuted,
              modifier = Modifier.size(28.dp)
            )
          }
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = if (searchQuery.isNotEmpty()) "No matching properties" else "No listings yet",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = NaturalDark
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = if (searchQuery.isNotEmpty()) {
              "Try adjusting your search terms or filters."
            } else if (onAddPropertyClick != null) {
              "The catalog is currently empty. Use the button below to add your first property listing."
            } else {
              "No sanctuaries are currently available. Please check back soon for newly published listings."
            },
            style = MaterialTheme.typography.bodyMedium.copy(color = TaupeMuted),
            textAlign = TextAlign.Center
          )
          if (onAddPropertyClick != null && searchQuery.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = onAddPropertyClick,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
              modifier = Modifier.testTag("empty_home_add_property_button")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Add New Listing", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp),
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

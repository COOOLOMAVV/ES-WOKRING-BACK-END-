package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppointmentStatus
import com.example.data.model.ViewingAppointment
import com.example.ui.theme.*
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

private fun matchesAppointmentDate(apptDate: String, targetYear: Int, targetMonth0Indexed: Int, targetDay: Int): Boolean {
  val targetMonth1Indexed = targetMonth0Indexed + 1
  val trimmed = apptDate.trim()

  // 1. Try LocalDate parsing (ISO-8601: YYYY-MM-DD or standard formats)
  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
    try {
      val parsed = LocalDate.parse(trimmed)
      return parsed.year == targetYear && parsed.monthValue == targetMonth1Indexed && parsed.dayOfMonth == targetDay
    } catch (_: Exception) {}
  }

  // 2. Try ISO-8601 regex: YYYY-MM-DD
  val isoMatch = Regex("""^(\d{4})-(\d{1,2})-(\d{1,2})""").find(trimmed)
  if (isoMatch != null) {
    val (y, m, d) = isoMatch.destructured
    val yearVal = y.toIntOrNull() ?: return false
    val monthVal = m.toIntOrNull() ?: return false
    val dayVal = d.toIntOrNull() ?: return false
    return yearVal == targetYear && monthVal == targetMonth1Indexed && dayVal == targetDay
  }

  // 3. Try standard textual date formats:
  // e.g. "Mon, Oct 14", "Oct 14", "Oct 14, 2026", "October 14", "Wed, October 14, 2026"
  val textMatch = Regex("""(?:[a-zA-Z]+,\s*)?([a-zA-Z]+)\s+(\d{1,2})(?:st|nd|rd|th)?(?:,?\s*(\d{4}))?""").find(trimmed)
  if (textMatch != null) {
    val monthStr = textMatch.groupValues[1].lowercase(Locale.ROOT)
    val dayVal = textMatch.groupValues[2].toIntOrNull() ?: return false
    val yearVal = textMatch.groupValues[3].toIntOrNull()

    val monthVal = when {
      monthStr.startsWith("jan") -> 1
      monthStr.startsWith("feb") -> 2
      monthStr.startsWith("mar") -> 3
      monthStr.startsWith("apr") -> 4
      monthStr.startsWith("may") -> 5
      monthStr.startsWith("jun") -> 6
      monthStr.startsWith("jul") -> 7
      monthStr.startsWith("aug") -> 8
      monthStr.startsWith("sep") -> 9
      monthStr.startsWith("oct") -> 10
      monthStr.startsWith("nov") -> 11
      monthStr.startsWith("dec") -> 12
      else -> return false
    }

    if (dayVal != targetDay || monthVal != targetMonth1Indexed) {
      return false
    }
    if (yearVal != null && yearVal != targetYear) {
      return false
    }
    return true
  }

  return false
}

enum class CalendarViewMode {
  MONTH_GRID,
  WEEK_STRIP,
  AGENDA_LIST
}

@Composable
fun CalendarAppointmentsScreen(
  appointments: List<ViewingAppointment>,
  onUpdateStatus: (String, AppointmentStatus) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Date State - Defaults to October 2026 to match mock data, or current calendar
  var currentYear by remember { mutableIntStateOf(2026) }
  var currentMonth by remember { mutableIntStateOf(9) } // 0-indexed: 9 = October
  var selectedDay by remember { mutableStateOf<Int?>(14) } // Default selected Oct 14
  var viewMode by remember { mutableStateOf(CalendarViewMode.MONTH_GRID) }
  var selectedStatusFilter by remember { mutableStateOf("All") } // "All", "Upcoming", "Completed"

  val monthNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
  )

  val currentMonthName = monthNames.getOrElse(currentMonth) { "October" }

  // Precompute appointments by day in a map for O(1) instant rendering
  val appointmentsByDay = remember(appointments, currentMonth, currentYear) {
    val map = mutableMapOf<Int, MutableList<ViewingAppointment>>()
    for (day in 1..31) {
      val matching = appointments.filter { appt ->
        matchesAppointmentDate(appt.date, currentYear, currentMonth, day)
      }
      if (matching.isNotEmpty()) {
        map[day] = matching.toMutableList()
      }
    }
    map
  }

  fun getAppointmentsForDay(day: Int): List<ViewingAppointment> = appointmentsByDay[day] ?: emptyList()

  // Filtered appointments to display in the list below calendar
  val displayAppointments = remember(appointments, selectedDay, appointmentsByDay, selectedStatusFilter) {
    val baseList = if (selectedDay != null) {
      getAppointmentsForDay(selectedDay!!)
    } else {
      appointments
    }

    when (selectedStatusFilter) {
      "Upcoming" -> baseList.filter { it.status == AppointmentStatus.UPCOMING || it.status == AppointmentStatus.PENDING }
      "Completed" -> baseList.filter { it.status == AppointmentStatus.COMPLETED }
      else -> baseList
    }
  }

  // Days in month calculation
  val calendar = remember(currentYear, currentMonth) {
    Calendar.getInstance().apply {
      set(Calendar.YEAR, currentYear)
      set(Calendar.MONTH, currentMonth)
      set(Calendar.DAY_OF_MONTH, 1)
    }
  }

  val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
  // Calendar.DAY_OF_WEEK: Sunday=1, Monday=2, ..., Saturday=7
  // We align starting Monday (0 = Monday, ..., 6 = Sunday)
  val firstDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NaturalBg)
  ) {
    // Top Title & Controls Header
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Viewing Calendar",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              color = NaturalDark,
              fontSize = 22.sp
            )
          )
          Text(
            text = "Interactive walkthrough schedule & client bookings",
            style = MaterialTheme.typography.bodySmall.copy(
              color = TaupeDark,
              fontSize = 12.sp
            )
          )
        }

        // View Mode Toggle (Month vs Agenda List)
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SandMuted)
            .border(1.dp, SandWarm, RoundedCornerShape(12.dp))
            .padding(2.dp),
          horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          IconButton(
            onClick = { viewMode = CalendarViewMode.MONTH_GRID },
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(if (viewMode == CalendarViewMode.MONTH_GRID) ForestGreen else Color.Transparent)
              .testTag("month_view_mode_button")
          ) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = "Month Grid",
              tint = if (viewMode == CalendarViewMode.MONTH_GRID) Color.White else TaupeDark,
              modifier = Modifier.size(16.dp)
            )
          }

          IconButton(
            onClick = { viewMode = CalendarViewMode.WEEK_STRIP },
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(if (viewMode == CalendarViewMode.WEEK_STRIP) ForestGreen else Color.Transparent)
              .testTag("week_view_mode_button")
          ) {
            Icon(
              imageVector = Icons.Default.Today,
              contentDescription = "Week Carousel",
              tint = if (viewMode == CalendarViewMode.WEEK_STRIP) Color.White else TaupeDark,
              modifier = Modifier.size(16.dp)
            )
          }

          IconButton(
            onClick = {
              viewMode = CalendarViewMode.AGENDA_LIST
              selectedDay = null // View all in list mode
            },
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(if (viewMode == CalendarViewMode.AGENDA_LIST) ForestGreen else Color.Transparent)
              .testTag("list_view_mode_button")
          ) {
            Icon(
              imageVector = Icons.Default.FormatListBulleted,
              contentDescription = "Agenda List",
              tint = if (viewMode == CalendarViewMode.AGENDA_LIST) Color.White else TaupeDark,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }

    // Scrollable Screen Content
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {
      // 1. Calendar Header Bar with Month Switcher
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Month Navigation Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "$currentMonthName $currentYear",
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = NaturalDark,
                    fontSize = 19.sp
                  )
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Quick Jump to Oct 2026 / Today
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = SandMuted,
                  border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                      currentYear = 2026
                      currentMonth = 9
                      selectedDay = 14
                    }
                ) {
                  Text(
                    text = "Today",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = ForestGreen,
                      fontSize = 11.sp
                    )
                  )
                }
              }

              // Month Navigation Arrows
              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                  onClick = {
                    if (currentMonth == 0) {
                      currentMonth = 11
                      currentYear -= 1
                    } else {
                      currentMonth -= 1
                    }
                  },
                  modifier = Modifier
                    .size(34.dp)
                    .background(SandMuted, CircleShape)
                    .border(1.dp, SandWarm, CircleShape)
                    .testTag("prev_month_button")
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Month",
                    tint = NaturalDark,
                    modifier = Modifier.size(16.dp)
                  )
                }

                IconButton(
                  onClick = {
                    if (currentMonth == 11) {
                      currentMonth = 0
                      currentYear += 1
                    } else {
                      currentMonth += 1
                    }
                  },
                  modifier = Modifier
                    .size(34.dp)
                    .background(SandMuted, CircleShape)
                    .border(1.dp, SandWarm, CircleShape)
                    .testTag("next_month_button")
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Month",
                    tint = NaturalDark,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Render either Month Grid or Week Strip based on view mode
            if (viewMode == CalendarViewMode.MONTH_GRID) {
              // Weekday Headers (Mon - Sun)
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
              ) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { dayName ->
                  Text(
                    text = dayName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = TaupeMuted,
                      fontSize = 11.sp
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              HorizontalDivider(color = SandWarm.copy(alpha = 0.5f), thickness = 1.dp)
              Spacer(modifier = Modifier.height(8.dp))

              // Calendar Grid (7 columns x 5-6 rows)
              val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7

              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (row in 0 until (totalCells / 7)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                  ) {
                    for (col in 0..6) {
                      val cellIndex = row * 7 + col
                      val dayNumber = cellIndex - firstDayOfWeek + 1

                      if (dayNumber in 1..daysInMonth) {
                        val isSelected = selectedDay == dayNumber
                        val dayAppts = getAppointmentsForDay(dayNumber)
                        val hasUpcoming = dayAppts.any { it.status == AppointmentStatus.UPCOMING || it.status == AppointmentStatus.PENDING }
                        val hasCompleted = dayAppts.any { it.status == AppointmentStatus.COMPLETED }
                        val isToday = dayNumber == 14 && currentMonth == 9 && currentYear == 2026

                        Box(
                          modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                              when {
                                isSelected -> ForestGreen
                                isToday -> SandWarm.copy(alpha = 0.5f)
                                dayAppts.isNotEmpty() -> SandMuted
                                else -> Color.Transparent
                              }
                            )
                            .border(
                              width = if (isToday && !isSelected) 1.5.dp else if (isSelected) 0.dp else 0.dp,
                              color = if (isToday && !isSelected) ForestGreen else Color.Transparent,
                              shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                              selectedDay = if (selectedDay == dayNumber) null else dayNumber
                            }
                            .testTag("calendar_day_$dayNumber"),
                          contentAlignment = Alignment.Center
                        ) {
                          Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                          ) {
                            Text(
                              text = dayNumber.toString(),
                              style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else NaturalDark,
                                fontSize = 13.sp
                              )
                            )

                            // Event Dot indicators
                            if (dayAppts.isNotEmpty()) {
                              Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.padding(top = 2.dp)
                              ) {
                                if (hasUpcoming) {
                                  Box(
                                    modifier = Modifier
                                      .size(4.dp)
                                      .background(
                                        color = if (isSelected) Color.White else ForestGreen,
                                        shape = CircleShape
                                      )
                                  )
                                }
                                if (hasCompleted && !hasUpcoming) {
                                  Box(
                                    modifier = Modifier
                                      .size(4.dp)
                                      .background(
                                        color = if (isSelected) Color.White.copy(alpha = 0.7f) else TaupeDark,
                                        shape = CircleShape
                                      )
                                  )
                                }
                              }
                            }
                          }
                        }
                      } else {
                        // Empty cell for offset days
                        Box(
                          modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                        )
                      }
                    }
                  }
                }
              }
            } else if (viewMode == CalendarViewMode.WEEK_STRIP) {
              // Smooth Week Carousel (Day chips)
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                for (day in 1..daysInMonth) {
                  val isSelected = selectedDay == day
                  val dayAppts = getAppointmentsForDay(day)
                  val dayCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, currentYear)
                    set(Calendar.MONTH, currentMonth)
                    set(Calendar.DAY_OF_MONTH, day)
                  }
                  val dayOfWeekStr = dayCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ROOT) ?: "Day"

                  Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) ForestGreen else if (dayAppts.isNotEmpty()) SandMuted else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                      width = 1.dp,
                      color = if (isSelected) ForestGreen else SandWarm
                    ),
                    modifier = Modifier
                      .clip(RoundedCornerShape(14.dp))
                      .clickable { selectedDay = if (selectedDay == day) null else day }
                      .testTag("week_day_$day")
                  ) {
                    Column(
                      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                      horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                      Text(
                        text = dayOfWeekStr.take(3).uppercase(Locale.ROOT),
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontWeight = FontWeight.Bold,
                          color = if (isSelected) Color.White.copy(alpha = 0.8f) else TaupeMuted,
                          fontSize = 10.sp
                        )
                      )
                      Spacer(modifier = Modifier.height(3.dp))
                      Text(
                        text = "$day",
                        style = MaterialTheme.typography.titleMedium.copy(
                          fontWeight = FontWeight.Bold,
                          color = if (isSelected) Color.White else NaturalDark,
                          fontSize = 16.sp
                        )
                      )
                      if (dayAppts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                          modifier = Modifier
                            .size(5.dp)
                            .background(
                              if (isSelected) Color.White else ForestGreen,
                              CircleShape
                            )
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      // 2. Schedule Agenda Header & Filter Chips
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (selectedDay != null) {
                  "Agenda for ${currentMonthName.take(3)} $selectedDay, $currentYear"
                } else {
                  "All Month Appointments"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = NaturalDark,
                  fontSize = 16.5.sp
                )
              )
              Text(
                text = "${displayAppointments.size} viewing(s) scheduled",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TaupeDark,
                  fontSize = 12.sp
                )
              )
            }

            if (selectedDay != null) {
              OutlinedButton(
                onClick = { selectedDay = null },
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen),
                modifier = Modifier.height(34.dp).testTag("view_all_month_button")
              ) {
                Text("Show All", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Status Filter Tabs (All, Upcoming, Completed)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("All", "Upcoming", "Completed").forEach { filter ->
              val isSelected = selectedStatusFilter == filter
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .clickable { selectedStatusFilter = filter }
                  .testTag("status_filter_$filter"),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) ForestGreen else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                  width = 1.dp,
                  color = if (isSelected) ForestGreen else SandWarm
                )
              ) {
                Text(
                  text = filter,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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
      }

      // 3. Appointments List
      if (displayAppointments.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
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
                  imageVector = Icons.Default.EventNote,
                  contentDescription = null,
                  tint = TaupeMuted,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = if (selectedDay != null) "No viewings on ${currentMonthName.take(3)} $selectedDay" else "No appointments found",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = NaturalDark
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Tap on dates with green indicator dots above to review active tours or select another filter.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TaupeDark,
                  textAlign = TextAlign.Center,
                  fontSize = 12.sp
                )
              )
              if (selectedDay != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                  onClick = { selectedDay = null },
                  colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.height(36.dp)
                ) {
                  Text("View All Dates", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      } else {
        items(
          items = displayAppointments,
          key = { it.id },
          contentType = { "appointment_item" }
        ) { appt ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 6.dp)
              .testTag("agenda_item_${appt.id}"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              // Status Badge & Date Row
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SandMuted,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(13.dp)
                      )
                      Spacer(modifier = Modifier.width(5.dp))
                      Text(
                        text = "${appt.date} • ${appt.timeSlot}",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontWeight = FontWeight.Bold,
                          color = NaturalDark,
                          fontSize = 12.sp
                        )
                      )
                    }
                  }
                }

                val (badgeBg, badgeText, badgeColor) = when (appt.status) {
                  AppointmentStatus.UPCOMING -> Triple(ForestGreenLight, "Upcoming", ForestGreen)
                  AppointmentStatus.COMPLETED -> Triple(SandMuted, "Completed", TaupeDark)
                  AppointmentStatus.PENDING -> Triple(AmberLight, "Pending", AmberPending)
                  AppointmentStatus.CANCELLED -> Triple(RoseLight, "Cancelled", RoseFavorite)
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = badgeBg
                ) {
                  Text(
                    text = badgeText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = badgeColor,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Property Title & Type
              Text(
                text = "${appt.propertyTitle} (${appt.type})",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = NaturalDark,
                  fontSize = 15.sp
                )
              )

              Spacer(modifier = Modifier.height(2.dp))

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = null,
                  tint = TaupeMuted,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = appt.propertyAddress,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = TaupeDark,
                    fontSize = 12.5.sp
                  )
                )
              }

              if (appt.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = SandMuted.copy(alpha = 0.6f),
                  border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
                ) {
                  Text(
                    text = "Note: ${appt.notes}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = TaupeDark,
                      fontSize = 11.5.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))
              HorizontalDivider(color = SandWarm, thickness = 1.dp)
              Spacer(modifier = Modifier.height(12.dp))

              // Client Details & Direct Actions
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Person,
                      contentDescription = null,
                      tint = ForestGreen,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                      text = appt.clientName,
                      style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NaturalDark,
                        fontSize = 13.sp
                      )
                    )
                  }

                  Spacer(modifier = Modifier.height(3.dp))

                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                      val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${appt.clientPhone.replace("[^0-9+]".toRegex(), "")}")
                      }
                      context.startActivity(dialIntent)
                    }
                  ) {
                    Icon(
                      imageVector = Icons.Default.Call,
                      contentDescription = "Call Client",
                      tint = ForestGreen,
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = appt.clientPhone,
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = ForestGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                      )
                    )
                  }
                }

                // Completion CTA
                if (appt.status == AppointmentStatus.UPCOMING || appt.status == AppointmentStatus.PENDING) {
                  Button(
                    onClick = { onUpdateStatus(appt.id, AppointmentStatus.COMPLETED) },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("complete_appt_${appt.id}")
                  ) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mark Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

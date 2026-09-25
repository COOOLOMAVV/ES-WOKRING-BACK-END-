package com.example.ui.screens

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Inquiry
import com.example.ui.theme.*
import com.example.ui.viewmodel.UserRole

@Composable
fun MessagesScreen(
  inquiries: List<Inquiry>,
  selectedInquiry: Inquiry?,
  userRole: UserRole,
  onSelectInquiry: (Inquiry) -> Unit,
  onBackToList: () -> Unit,
  onSendMessage: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  var replyText by remember { mutableStateOf("") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NaturalBg)
  ) {
    if (selectedInquiry == null) {
      // Message Thread List View matching Natural Tones "Message Logs" section
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "MESSAGE LOGS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = TaupeMuted,
              fontSize = 11.sp,
              letterSpacing = 1.sp
            )
          )
          Box(
            modifier = Modifier
              .size(20.dp)
              .clip(CircleShape)
              .background(ForestGreen),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${inquiries.count { it.isUnread }}",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = if (userRole == UserRole.AGENT) "Client Inquiries" else "Agent Conversations",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = NaturalDark
          )
        )
      }

      HorizontalDivider(color = SandWarm, thickness = 1.dp)

      if (inquiries.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize().padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No messages yet.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TaupeMuted)
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(inquiries, key = { it.id }) { inq ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (inq.isUnread) SandWarm.copy(alpha = 0.4f) else Color.White)
                .border(1.dp, SandWarm, RoundedCornerShape(16.dp))
                .clickable { onSelectInquiry(inq) }
                .padding(14.dp)
                .testTag("inquiry_chat_row_${inq.id}"),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(SandMuted)
                  .border(1.dp, SandWarm, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                if (inq.senderName.contains("Sarah", ignoreCase = true)) {
                  Image(
                    painter = painterResource(id = R.drawable.agent_sarah_avatar_1787131767100),
                    contentDescription = inq.senderName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                  )
                } else {
                  Text(
                    text = inq.senderName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = ForestGreen
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = inq.senderName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = if (inq.isUnread) FontWeight.Bold else FontWeight.SemiBold,
                      color = NaturalDark,
                      fontSize = 14.5.sp
                    )
                  )
                  Text(
                    text = inq.timeAgo,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = TaupeMuted,
                      fontSize = 10.5.sp
                    )
                  )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = inq.propertyTitle,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = ForestGreen,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.5.sp
                  ),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = inq.messages.lastOrNull()?.text ?: inq.message,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = if (inq.isUnread) NaturalDark else TaupeDark,
                    fontWeight = if (inq.isUnread) FontWeight.Medium else FontWeight.Normal,
                    fontSize = 12.5.sp
                  ),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              if (inq.isUnread) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .background(ForestGreen, CircleShape)
                )
              }
            }
          }
        }
      }
    } else {
      // Active Conversation View
      Column(modifier = Modifier.fillMaxSize()) {
        // Chat Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onBackToList,
            modifier = Modifier.testTag("chat_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = NaturalDark
            )
          }

          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(SandMuted)
              .border(1.dp, SandWarm, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            if (selectedInquiry.senderName.contains("Sarah", ignoreCase = true)) {
              Image(
                painter = painterResource(id = R.drawable.agent_sarah_avatar_1787131767100),
                contentDescription = selectedInquiry.senderName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
            } else {
              Text(
                text = selectedInquiry.senderName.take(1).uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = ForestGreen
                )
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = selectedInquiry.senderName,
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = NaturalDark
              )
            )
            Text(
              text = selectedInquiry.propertyTitle,
              style = MaterialTheme.typography.bodySmall.copy(
                color = TaupeDark,
                fontSize = 11.sp
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        HorizontalDivider(color = SandWarm, thickness = 1.dp)

        // Chat Message List
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(selectedInquiry.messages) { msg ->
            val isMyMsg = (userRole == UserRole.AGENT && msg.sender == "agent") || (userRole == UserRole.BUYER && msg.sender == "user")
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = if (isMyMsg) Arrangement.End else Arrangement.Start
            ) {
              Column(
                modifier = Modifier
                  .widthIn(max = 280.dp)
                  .clip(
                    RoundedCornerShape(
                      topStart = 16.dp,
                      topEnd = 16.dp,
                      bottomStart = if (isMyMsg) 16.dp else 4.dp,
                      bottomEnd = if (isMyMsg) 4.dp else 16.dp
                    )
                  )
                  .background(if (isMyMsg) ForestGreen else Color.White)
                  .border(
                    1.dp,
                    if (isMyMsg) ForestGreen else SandWarm,
                    RoundedCornerShape(
                      topStart = 16.dp,
                      topEnd = 16.dp,
                      bottomStart = if (isMyMsg) 16.dp else 4.dp,
                      bottomEnd = if (isMyMsg) 4.dp else 16.dp
                    )
                  )
                  .padding(horizontal = 14.dp, vertical = 10.dp)
              ) {
                Text(
                  text = msg.text,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isMyMsg) Color.White else NaturalDark,
                    fontSize = 13.5.sp
                  )
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = msg.time,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isMyMsg) Color.White.copy(alpha = 0.7f) else TaupeMuted,
                    fontSize = 9.5.sp
                  ),
                  modifier = Modifier.align(Alignment.End)
                )
              }
            }
          }
        }

        // Canned Quick Reply Suggestions
        val quickReplies = if (userRole == UserRole.AGENT) {
          listOf(
            "Viewing is available Monday 1:30 PM",
            "Sent strata documents to your email",
            "Yes, parking is included in the unit"
          )
        } else {
          listOf(
            "Is the tour scheduled for tomorrow at 10 AM still good?",
            "Can I book an appointment this weekend?",
            "Could you send me the floor plan?"
          )
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          quickReplies.forEach { suggestion ->
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                  onSendMessage(selectedInquiry.id, suggestion)
                },
              shape = RoundedCornerShape(16.dp),
              color = Color.White,
              border = androidx.compose.foundation.BorderStroke(1.dp, SandWarm)
            ) {
              Text(
                text = suggestion,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = NaturalDark,
                  fontSize = 11.5.sp
                )
              )
            }
          }
        }

        // Input Field in Natural Tones style
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = replyText,
            onValueChange = { replyText = it },
            placeholder = { Text("Write a message...", fontSize = 14.sp, color = TaupeMuted) },
            modifier = Modifier
              .weight(1f)
              .testTag("chat_input_field"),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = NaturalDark,
              unfocusedTextColor = NaturalDark,
              cursorColor = ForestGreen,
              focusedContainerColor = SandMuted,
              unfocusedContainerColor = SandMuted,
              focusedBorderColor = ForestGreen,
              unfocusedBorderColor = SandWarm
            ),
            singleLine = false,
            maxLines = 3
          )

          Spacer(modifier = Modifier.width(8.dp))

          IconButton(
            onClick = {
              if (replyText.isNotBlank()) {
                onSendMessage(selectedInquiry.id, replyText.trim())
                replyText = ""
              }
            },
            modifier = Modifier
              .size(44.dp)
              .background(ForestGreen, CircleShape)
              .testTag("send_chat_message_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Terminal
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LimeAccent
import com.example.ui.theme.NaturalBg
import com.example.ui.theme.NaturalDark
import com.example.ui.theme.NaturalDarkAlt
import com.example.ui.theme.SandWarm
import com.example.ui.theme.TaupeDark
import com.example.ui.theme.TaupeMuted

@Composable
fun SqlSchemaDialog(
  isOpen: Boolean,
  sqlContent: String,
  onDismiss: () -> Unit
) {
  if (!isOpen) return

  val context = LocalContext.current
  val vertScroll = rememberScrollState()
  val horizScroll = rememberScrollState()

  val isDark = isSystemInDarkTheme()
  val dialogBg = if (isDark) DarkSurface else Color.White
  val primaryText = if (isDark) Color(0xFFF5F5F0) else NaturalDark
  val secondaryText = if (isDark) Color(0xFFD6D4CC) else TaupeDark
  val instructionBg = if (isDark) NaturalDarkAlt else NaturalBg

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.85f),
      shape = RoundedCornerShape(20.dp),
      color = dialogBg
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .background(ForestGreen.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.DataObject, contentDescription = null, tint = if (isDark) LimeAccent else ForestGreen, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "PostgreSQL Schema (pgAdmin 4)",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = primaryText
                )
              )
              Text(
                text = "Ready to execute in pgAdmin Query Tool",
                style = MaterialTheme.typography.bodySmall.copy(color = secondaryText)
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = secondaryText)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Instructions
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = instructionBg)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "HOW TO RUN IN PGADMIN 4:",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = if (isDark) LimeAccent else ForestGreen)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "1. Open pgAdmin 4 -> Create Database 'estateflow_db'\n2. Right click 'estateflow_db' -> Click 'Query Tool'\n3. Copy this SQL below, paste into Query Tool, and press F5 (Execute)",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, color = primaryText, lineHeight = 16.sp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Code Preview Box
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .background(Color(0xFF1E1E2E), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF313244), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Column(
            modifier = Modifier
              .verticalScroll(vertScroll)
              .horizontalScroll(horizScroll)
          ) {
            Text(
              text = sqlContent,
              style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFCDD6F4),
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Close", color = primaryText, fontWeight = FontWeight.Medium)
          }

          Button(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("PostgreSQL Schema", sqlContent)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "SQL Schema copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.weight(1.5f),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Copy SQL Schema", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

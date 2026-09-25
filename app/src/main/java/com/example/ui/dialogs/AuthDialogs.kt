package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

@Composable
fun ClientAuthDialog(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  onLogin: (email: String) -> Unit,
  onSignUp: (name: String, email: String, phone: String) -> Unit,
  authError: String? = null
) {
  if (!isOpen) return

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Sign Up
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var confirmPasswordVisible by remember { mutableStateOf(false) }
  var rememberMe by remember { mutableStateOf(true) }
  var localError by remember { mutableStateOf<String?>(null) }

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
          .testTag("client_auth_dialog"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Key,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
            }
            Column {
              Text(
                text = "Client Account Required",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Sign in to book viewings & save properties",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_client_auth_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = {
              selectedTab = 0
              localError = null
            },
            text = { Text("Log In", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
            modifier = Modifier.testTag("client_login_tab")
          )
          Tab(
            selected = selectedTab == 1,
            onClick = {
              selectedTab = 1
              localError = null
            },
            text = { Text("Create Account", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
            modifier = Modifier.testTag("client_signup_tab")
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val displayError = localError ?: authError
        if (displayError != null) {
          Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 12.dp)
          ) {
            Text(
              text = displayError,
              color = MaterialTheme.colorScheme.onErrorContainer,
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(10.dp)
            )
          }
        }

        if (selectedTab == 0) {
          // --- LOG IN TAB ---
          OutlinedTextField(
            value = email,
            onValueChange = {
              email = it
              localError = null
            },
            label = { Text("Email address") },
            placeholder = { Text("e.g. buyer@example.com") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("client_login_email_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = password,
            onValueChange = {
              password = it
              localError = null
            },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("client_login_password_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { rememberMe = !rememberMe }
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Checkbox(
              checked = rememberMe,
              onCheckedChange = { rememberMe = it },
              colors = CheckboxDefaults.colors(checkedColor = ForestGreen),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Remember me on this device",
              style = MaterialTheme.typography.bodySmall.copy(color = NaturalDark, fontSize = 12.sp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              if (email.isBlank()) {
                localError = "Please enter your email address."
              } else {
                onLogin(email)
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("client_login_submit_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Log In & Continue", fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(10.dp))

          // One-tap quick demo buyer
          OutlinedButton(
            onClick = {
              onLogin("alex.turner@example.com")
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("client_quick_demo_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("One-Tap Demo: Log in as Alex Turner")
          }
        } else {
          // --- CREATE ACCOUNT TAB ---
          OutlinedTextField(
            value = name,
            onValueChange = {
              name = it
              localError = null
            },
            label = { Text("Full Name") },
            placeholder = { Text("e.g. Alex Turner") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("client_signup_name_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = email,
            onValueChange = {
              email = it
              localError = null
            },
            label = { Text("Email Address") },
            placeholder = { Text("e.g. alex@example.com") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("client_signup_email_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = phone,
            onValueChange = {
              phone = it
              localError = null
            },
            label = { Text("Phone Number") },
            placeholder = { Text("e.g. +1 (555) 019-8234") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("client_signup_phone_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = password,
            onValueChange = {
              password = it
              localError = null
            },
            label = { Text("Create Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("client_signup_password_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
              confirmPassword = it
              localError = null
            },
            label = { Text("Re-enter Password to Confirm") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                Icon(
                  imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                )
              }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("client_signup_confirm_password_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Dynamic Password Strength Meter & Live Requirement Checklist
          PasswordStrengthMeter(password = password, confirmPassword = confirmPassword)

          Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = {
              when {
                name.isBlank() -> localError = "Please enter your full name."
                email.isBlank() -> localError = "Please enter your email address."
                phone.isBlank() -> localError = "Please enter your phone number."
                password.length < 4 -> localError = "Password must be at least 4 characters."
                password != confirmPassword -> localError = "Passwords do not match. Please re-enter your password to confirm."
                else -> onSignUp(name, email, phone)
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("client_signup_submit_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Create Account & Book Tour", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgentLoginDialog(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  onLoginAgent: (emailOrLicense: String, password: String) -> Unit,
  onSignUpAgent: (name: String, email: String, licenseNo: String, agency: String, professionalRole: String, password: String) -> Unit = { _, _, _, _, _, _ -> },
  authError: String? = null
) {
  if (!isOpen) return

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Register New Agent

  // Sign In State
  var emailOrLicense by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }

  // Registration State
  var regName by remember { mutableStateOf("") }
  var regEmail by remember { mutableStateOf("") }
  var regLicense by remember { mutableStateOf("") }
  var regPassword by remember { mutableStateOf("") }
  var regConfirmPassword by remember { mutableStateOf("") }
  var regPasswordVisible by remember { mutableStateOf(false) }
  var regConfirmPasswordVisible by remember { mutableStateOf(false) }
  var rememberAgentDevice by remember { mutableStateOf(true) }

  var localError by remember { mutableStateOf<String?>(null) }

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
          .widthIn(min = 320.dp, max = 490.dp)
          .heightIn(max = 640.dp)
          .testTag("agent_login_dialog"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(ForestGreen),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Badge,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
            Column {
              Text(
                text = "Agent & Broker Portal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NaturalDark
              )
              Text(
                text = "Licensed Real Estate Professionals",
                style = MaterialTheme.typography.bodySmall,
                color = ForestGreen,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_agent_login_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TaupeDark)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs: Sign In vs Register Agent
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = SandMuted,
          contentColor = ForestGreen,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = {
              selectedTab = 0
              localError = null
            },
            text = {
              Text(
                "Agent Sign In",
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
              )
            }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = {
              selectedTab = 1
              localError = null
            },
            text = {
              Text(
                "Register Agent",
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
              )
            }
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val displayError = localError ?: authError
        if (displayError != null) {
          Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 12.dp)
          ) {
            Text(
              text = displayError,
              color = MaterialTheme.colorScheme.onErrorContainer,
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(10.dp)
            )
          }
        }

        if (selectedTab == 0) {
          // --- AGENT SIGN IN TAB ---
          Text(
            text = "Sign in with your agent email or MLS license number to manage active listings, approve tour appointments, and reply to client inquiries.",
            style = MaterialTheme.typography.bodySmall,
            color = TaupeDark
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = emailOrLicense,
            onValueChange = {
              emailOrLicense = it
              localError = null
            },
            label = { Text("Agent Email or License #") },
            placeholder = { Text("e.g. sarah.jenkins@estateflow.com or RE-94821-BC") },
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("agent_license_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = password,
            onValueChange = {
              password = it
              localError = null
            },
            label = { Text("Portal Password") },
            placeholder = { Text("••••••••") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("agent_password_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { rememberAgentDevice = !rememberAgentDevice }
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Checkbox(
              checked = rememberAgentDevice,
              onCheckedChange = { rememberAgentDevice = it },
              colors = CheckboxDefaults.colors(checkedColor = ForestGreen),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Remember MLS license on this device",
              style = MaterialTheme.typography.bodySmall.copy(color = NaturalDark, fontSize = 12.sp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              when {
                emailOrLicense.isBlank() -> localError = "Please enter your agent email or MLS license number."
                password.length < 4 -> localError = "Password must be at least 4 characters."
                else -> onLoginAgent(emailOrLicense, password)
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("agent_login_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Sign In as Licensed Agent", fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Demo Quick Agent login
          OutlinedButton(
            onClick = {
              onLoginAgent("sarah.jenkins@estateflow.com", "agentpass123")
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("agent_quick_demo_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Demo: Log In as Sarah Jenkins (RE-94821)", fontSize = 12.sp)
          }
        } else {
          // --- AGENT REGISTRATION TAB ---
          Text(
            text = "Join our verified agent network. Specify your professional license, agency brokerage, and role in the real estate landscape.",
            style = MaterialTheme.typography.bodySmall,
            color = TaupeDark
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = regName,
            onValueChange = {
              regName = it
              localError = null
            },
            label = { Text("Full Legal Name") },
            placeholder = { Text("e.g. Marcus Vance") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("agent_reg_name_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = regEmail,
            onValueChange = {
              regEmail = it
              localError = null
            },
            label = { Text("Professional Agent Email") },
            placeholder = { Text("e.g. marcus@vancerealty.com") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("agent_reg_email_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = regLicense,
            onValueChange = {
              regLicense = it
              localError = null
            },
            label = { Text("Realtor ID (License #)") },
            placeholder = { Text("e.g. RE-77291-BC or BK-90412") },
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("agent_reg_license_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Password Field 1
          OutlinedTextField(
            value = regPassword,
            onValueChange = {
              regPassword = it
              localError = null
            },
            label = { Text("Create Agent Password") },
            placeholder = { Text("••••••••") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                Icon(
                  imageVector = if (regPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (regPasswordVisible) "Hide password" else "Show password"
                )
              }
            },
            visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("agent_reg_password_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Password Field 2: Re-verify password entered twice
          OutlinedTextField(
            value = regConfirmPassword,
            onValueChange = {
              regConfirmPassword = it
              localError = null
            },
            label = { Text("Re-enter Password to Confirm") },
            placeholder = { Text("••••••••") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { regConfirmPasswordVisible = !regConfirmPasswordVisible }) {
                Icon(
                  imageVector = if (regConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (regConfirmPasswordVisible) "Hide password" else "Show password"
                )
              }
            },
            visualTransformation = if (regConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("agent_reg_confirm_password_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Dynamic Password Strength Meter & Live Requirement Checklist for Agents
          PasswordStrengthMeter(password = regPassword, confirmPassword = regConfirmPassword)

          Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = {
              when {
                regName.isBlank() -> localError = "Please enter your legal name."
                regEmail.isBlank() -> localError = "Please enter your professional agent email."
                regLicense.isBlank() -> localError = "Please enter your Realtor ID / License number."
                regPassword.length < 4 -> localError = "Password must be at least 4 characters."
                regPassword != regConfirmPassword -> localError = "Passwords do not match. Please re-enter your password to confirm."
                else -> onSignUpAgent(
                  regName,
                  regEmail,
                  regLicense,
                  "Estateflow Luxury Realty",
                  "Licensed Real Estate Agent",
                  regPassword
                )
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("agent_register_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Complete Registration & Enter Portal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }
    }
  }
}
}

@Composable
fun PasswordStrengthMeter(
  password: String,
  confirmPassword: String = "",
  modifier: Modifier = Modifier
) {
  val hasMinLength = password.length >= 6
  val hasLetter = password.any { it.isLetter() }
  val hasDigit = password.any { it.isDigit() }
  val hasSpecial = password.any { !it.isLetterOrDigit() }

  val strengthScore = when {
    password.isEmpty() -> 0
    password.length < 6 -> 1
    hasLetter && hasDigit && (hasSpecial || password.length >= 8) -> 3
    hasLetter && hasDigit -> 2
    else -> 1
  }

  val strengthLabel = when (strengthScore) {
    0 -> "Enter password"
    1 -> "Weak"
    2 -> "Moderate"
    else -> "Strong"
  }

  val strengthColor = when (strengthScore) {
    0 -> SandWarm
    1 -> Color(0xFFBA1A1A)
    2 -> AmberPending
    else -> ForestGreen
  }

  Column(modifier = modifier.fillMaxWidth()) {
    // 3-segment bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      for (i in 1..3) {
        val segmentColor = if (strengthScore >= i) strengthColor else SandWarm.copy(alpha = 0.6f)
        Box(
          modifier = Modifier
            .weight(1f)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(segmentColor)
        )
      }
    }

    // Strength label & match feedback
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 2.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (password.isNotEmpty()) "Security: $strengthLabel" else "Password security",
        style = MaterialTheme.typography.labelSmall.copy(
          color = if (password.isNotEmpty()) strengthColor else TaupeDark,
          fontWeight = FontWeight.SemiBold,
          fontSize = 11.sp
        )
      )

      if (confirmPassword.isNotEmpty()) {
        val isMatch = password == confirmPassword
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isMatch) Icons.Default.CheckCircle else Icons.Default.Close,
            contentDescription = null,
            tint = if (isMatch) ForestGreen else Color(0xFFBA1A1A),
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = if (isMatch) "Passwords match" else "Mismatch",
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isMatch) ForestGreen else Color(0xFFBA1A1A),
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
          )
        }
      }
    }

    // Interactive Requirement Pills
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      RequirementPill(
        label = "6+ chars",
        satisfied = hasMinLength,
        modifier = Modifier.weight(1f)
      )
      RequirementPill(
        label = "Letter & number",
        satisfied = hasLetter && hasDigit,
        modifier = Modifier.weight(1.3f)
      )
      if (confirmPassword.isNotEmpty()) {
        RequirementPill(
          label = "Matched",
          satisfied = password.isNotEmpty() && password == confirmPassword,
          modifier = Modifier.weight(1.1f)
        )
      }
    }
  }
}

@Composable
fun RequirementPill(
  label: String,
  satisfied: Boolean,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(6.dp),
    color = if (satisfied) ForestGreen.copy(alpha = 0.12f) else SandWarm.copy(alpha = 0.4f)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = if (satisfied) Icons.Default.Check else Icons.Default.CheckCircle,
        contentDescription = null,
        tint = if (satisfied) ForestGreen else TaupeDark.copy(alpha = 0.5f),
        modifier = Modifier.size(11.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          color = if (satisfied) ForestGreen else TaupeDark,
          fontWeight = if (satisfied) FontWeight.SemiBold else FontWeight.Normal,
          fontSize = 10.sp
        ),
        maxLines = 1
      )
    }
  }
}

@Composable
fun ChangePasswordDialog(
  isOpen: Boolean,
  userEmail: String,
  onDismiss: () -> Unit,
  onPasswordChanged: (newPassword: String) -> Unit
) {
  if (!isOpen) return

  var currentPassword by remember { mutableStateOf("") }
  var newPassword by remember { mutableStateOf("") }
  var confirmNewPassword by remember { mutableStateOf("") }
  var currentPasswordVisible by remember { mutableStateOf(false) }
  var newPasswordVisible by remember { mutableStateOf(false) }
  var confirmNewPasswordVisible by remember { mutableStateOf(false) }
  var localError by remember { mutableStateOf<String?>(null) }
  var isSuccess by remember { mutableStateOf(false) }

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
          .widthIn(min = 300.dp, max = 450.dp)
          .heightIn(max = 600.dp)
          .testTag("change_password_dialog"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, SandWarm)
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
            Box(
              modifier = Modifier
                .size(38.dp)
                .background(ForestGreen.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Security, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Account Security",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NaturalDark)
              )
              Text(
                text = "Update credentials",
                style = MaterialTheme.typography.bodySmall.copy(color = TaupeDark, fontSize = 11.5.sp)
              )
            }
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TaupeDark)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isSuccess) {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = ForestGreen.copy(alpha = 0.12f)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(22.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Password successfully updated! Your account credentials have been re-verified.",
                style = MaterialTheme.typography.bodySmall.copy(color = ForestGreen, fontWeight = FontWeight.SemiBold)
              )
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Done", fontWeight = FontWeight.Bold)
          }
        } else {
          Text(
            text = "Active account: $userEmail",
            style = MaterialTheme.typography.labelSmall.copy(color = TaupeDark, fontWeight = FontWeight.Medium)
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = currentPassword,
            onValueChange = {
              currentPassword = it
              localError = null
            },
            label = { Text("Current Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                Icon(
                  imageVector = if (currentPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = null
                )
              }
            },
            visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = newPassword,
            onValueChange = {
              newPassword = it
              localError = null
            },
            label = { Text("New Password") },
            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                Icon(
                  imageVector = if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = null
                )
              }
            },
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          PasswordStrengthMeter(password = newPassword, confirmPassword = confirmNewPassword)

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = {
              confirmNewPassword = it
              localError = null
            },
            label = { Text("Re-enter New Password to Confirm") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { confirmNewPasswordVisible = !confirmNewPasswordVisible }) {
                Icon(
                  imageVector = if (confirmNewPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = null
                )
              }
            },
            visualTransformation = if (confirmNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          if (localError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = localError!!,
              color = Color(0xFFBA1A1A),
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = {
              when {
                currentPassword.isBlank() -> localError = "Please enter your current password."
                newPassword.length < 4 -> localError = "New password must be at least 4 characters."
                newPassword != confirmNewPassword -> localError = "Passwords do not match. Please re-enter your password to confirm."
                else -> {
                  onPasswordChanged(newPassword)
                  isSuccess = true
                }
              }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Update & Save Password", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
}

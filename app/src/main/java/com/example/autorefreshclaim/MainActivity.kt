package com.example.autorefreshclaim

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoRefreshClaimApp()
        }
    }
}

@Composable
fun AutoRefreshClaimApp() {
    val context = LocalContext.current
    val prefs = remember {
        context.applicationContext.getSharedPreferences(
            Preferences.PREFS_FILE,
            Context.MODE_PRIVATE
        )
    }
    var refreshInterval by remember {
        mutableStateOf(Preferences.loadRefreshInterval(prefs).toString())
    }
    var savedMessage by remember { mutableStateOf("") }
    var serviceEnabled by remember { mutableStateOf(isAccessibilityServiceEnabled(context)) }
    var automationEnabled by remember { mutableStateOf(Preferences.isAutomationEnabled(prefs)) }
    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text("Auto Refresh & Claim", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = refreshInterval,
                onValueChange = { refreshInterval = it.filter { char -> char.isDigit() } },
                label = { Text("Refresh interval (ms)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val interval = refreshInterval.toLongOrNull() ?: 3000L
                    Preferences.saveRefreshInterval(prefs, interval)
                    savedMessage = "Saved interval: ${interval}ms"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save interval")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Accessibility Settings")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val enabled = !automationEnabled
                    Preferences.saveAutomationEnabled(prefs, enabled)
                    automationEnabled = enabled
                    savedMessage = if (enabled) {
                        "Automation started. Switch to target app now."
                    } else {
                        "Automation stopped."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (automationEnabled) "Stop automation" else "Start automation")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Accessibility service status", style = MaterialTheme.typography.bodyMedium)
                Text(
                    if (serviceEnabled) "Enabled" else "Disabled",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (serviceEnabled) Color(0xFF388E3C) else Color(0xFFD32F2F)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Automation status: ${if (automationEnabled) "Active" else "Inactive"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("How to use", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "1. Enable the Auto Refresh & Claim service in Android Accessibility.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "2. Set the refresh interval in milliseconds and save it.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "3. Press Start automation and then open the target app where claim buttons appear.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "4. The service only scans other apps when automation is active and accessibility permission is granted.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "5. Do not set the refresh interval too low; values under 1000ms are not recommended.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Logs", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LogOverlay()

            Spacer(modifier = Modifier.height(24.dp))
            Text("Status", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Current saved interval: ${Preferences.loadRefreshInterval(prefs)} ms", style = MaterialTheme.typography.bodyMedium)
            if (savedMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(savedMessage, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Notice", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "This app requires an accessibility service and will only work after granting permission.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "If the claim button disappears quickly, increase the refresh interval in milliseconds and save again.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun LogOverlay() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (LogRepository.messages.isEmpty()) {
            Text("No logs yet.", style = MaterialTheme.typography.bodySmall)
        } else {
            LogRepository.messages.take(10).forEach { message ->
                Text(message, fontSize = 12.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val expected = ComponentName(context, ClaimAccessibilityService::class.java).flattenToString()
    val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        ?: return false
    return enabledServices.split(':').any { it.equals(expected, ignoreCase = true) }
}

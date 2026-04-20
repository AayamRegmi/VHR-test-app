package np.com.aayamregmi.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import np.com.aayamregmi.ui.theme.VHRtestappTheme
import np.com.aayamregmi.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit = {},
    vm: ProfileViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        SettingsDialog(
            currentFirstName = state.displayName.substringBefore(" "),
            currentLastName = state.displayName.substringAfter(" ", ""),
            onDismiss = { showSettingsDialog = false },
            onSave = { first, last ->
                vm.updateName(first, last)
                showSettingsDialog = false
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy") },
            text = {
                Text(
                    "VHR Test App collects only the data you provide during registration " +
                    "(name, email) and the results of tests you complete. All data is stored " +
                    "locally on your device and is never shared with third parties. " +
                    "You can delete your data at any time by uninstalling the app."
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) { Text("OK") }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About VHR Test App") },
            text = {
                Text(
                    "VHR Test App v1.0\n\n" +
                    "A suite of simple health reflex tests:\n" +
                    "• Color Blindness (Ishihara)\n" +
                    "• Hearing Test\n" +
                    "• Reflex Test\n\n" +
                    "Built with Jetpack Compose and Room."
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("OK") }
            }
        )
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.initial,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = state.displayName.ifBlank { "—" },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = state.email.ifBlank { "—" },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem("Tests Taken", "${state.testsTaken}")
                ProfileStatItem(
                    "Best Color",
                    state.bestColorBlindPct?.let { "${it.toInt()}%" } ?: "—"
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            ProfileOptionItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                onClick = { showSettingsDialog = true }
            )

            ProfileOptionItem(
                icon = Icons.Default.PrivacyTip,
                title = "Privacy Policy",
                onClick = { showPrivacyDialog = true }
            )

            ProfileOptionItem(
                icon = Icons.Default.Info,
                title = "About App",
                onClick = { showAboutDialog = true }
            )

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = {
                    vm.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Logout", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun SettingsDialog(
    currentFirstName: String,
    currentLastName: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var firstName by remember { mutableStateOf(currentFirstName) }
    var lastName by remember { mutableStateOf(currentLastName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(firstName, lastName) },
                enabled = firstName.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ProfileStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenLightPreview() {
    VHRtestappTheme { ProfileScreen(onBack = {}) }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileScreenDarkPreview() {
    VHRtestappTheme(darkTheme = true) { ProfileScreen(onBack = {}) }
}

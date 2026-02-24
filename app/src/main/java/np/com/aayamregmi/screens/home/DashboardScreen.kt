package np.com.aayamregmi.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import np.com.aayamregmi.ui.theme.VHRtestappTheme   // ← correct import!

@OptIn(ExperimentalMaterial3Api::class)  // ← suppresses experimental warning
@Composable
fun DashboardScreen(
    onColorBlindClick: () -> Unit = {},
    onHearingClick: () -> Unit = {},
    onReflexClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onLeaderboardClick,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Leaderboard,
                    contentDescription = "Leaderboard"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your Best Scores",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    icon = Icons.Default.RemoveRedEye,
                    title = "Color Blind",
                    score = "92%"
                )
                StatCard(
                    icon = Icons.Default.Hearing,
                    title = "Hearing",
                    score = "85%"
                )
                StatCard(
                    icon = Icons.Default.Speed,
                    title = "Reflex",
                    score = "0.32s"
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Choose a Test",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            TestButton(
                text = "Color Blind Test",
                icon = Icons.Default.RemoveRedEye,
                onClick = onColorBlindClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            TestButton(
                text = "Hearing Test",
                icon = Icons.Default.Hearing,
                onClick = onHearingClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            TestButton(
                text = "Reflex Test",
                icon = Icons.Default.Speed,
                onClick = onReflexClick
            )
        }
    }
}


@Composable
private fun StatCard(
    icon: ImageVector,
    title: String,
    score: String
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = score,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TestButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// ───────────────────────────────────────────────
// Previews
// ───────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun DashboardScreenLightPreview() {
    VHRtestappTheme {
        DashboardScreen()
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DashboardScreenDarkPreview() {
    VHRtestappTheme(darkTheme = true) {
        DashboardScreen()
    }
}


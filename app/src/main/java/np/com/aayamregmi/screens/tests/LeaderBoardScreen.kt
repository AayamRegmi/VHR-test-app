package np.com.aayamregmi.screens.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import np.com.aayamregmi.ui.theme.VHRtestappTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderBoardScreen(
    onBack: () -> Unit
) {
    // Fake data – replace with real data later (from ViewModel / Firebase / Room)
    val leaderboard = listOf(
        LeaderboardEntry(1, "Aayam", 0.18f, "0.18 s"),
        LeaderboardEntry(2, "Sabin", 0.21f, "0.21 s"),
        LeaderboardEntry(3, "Rohan", 0.24f, "0.24 s"),
        LeaderboardEntry(4, "Pratik", 0.27f, "0.27 s"),
        LeaderboardEntry(5, "Anmol", 0.29f, "0.29 s"),
        LeaderboardEntry(6, "Nischal", 0.32f, "0.32 s"),
        LeaderboardEntry(7, "Sushant", 0.35f, "0.35 s"),
        LeaderboardEntry(8, "Bibek", 0.38f, "0.38 s"),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reflex Test Leaderboard") },
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
                .padding(horizontal = 16.dp)
        ) {
            // Podium – Top 3
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                PodiumItem(rank = 2, name = "Sabin", time = "0.21 s", heightFactor = 0.85f, color = Color(0xFFC0C0C0)) // Silver
                PodiumItem(rank = 1, name = "Aayam", time = "0.18 s", heightFactor = 1f, color = Color(0xFFFFD700))   // Gold
                PodiumItem(rank = 3, name = "Rohan", time = "0.24 s", heightFactor = 0.75f, color = Color(0xFFCD7F32)) // Bronze
            }

            // Other ranks list
            Text(
                text = "Other Rankings",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyColumn {
                itemsIndexed(leaderboard.drop(3)) { index, entry ->
                    LeaderboardRow(
                        rank = entry.rank,
                        name = entry.name,
                        time = entry.displayTime,
                        isCurrentUser = entry.name == "Aayam" // highlight current user
                    )
                }
            }
        }
    }
}

@Composable
private fun PodiumItem(
    rank: Int,
    name: String,
    time: String,
    heightFactor: Float,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.height(180.dp * heightFactor)
    ) {
        Text(
            text = time,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.first().toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "#$rank",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .width(80.dp)
                .height(20.dp * heightFactor)
                .background(color)
        )
    }
}

@Composable
private fun LeaderboardRow(
    rank: Int,
    name: String,
    time: String,
    isCurrentUser: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$rank",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(48.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal
                )
                if (isCurrentUser) {
                    Text(
                        text = "You",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = time,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val timeSeconds: Float,
    val displayTime: String
)

// ───────────────────────────────────────────────
// Previews
// ───────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun LeaderBoardScreenPreview() {
    VHRtestappTheme {
        LeaderBoardScreen(onBack = {})
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LeaderBoardScreenDarkPreview() {
    VHRtestappTheme(darkTheme = true) {
        LeaderBoardScreen(onBack = {})
    }
}
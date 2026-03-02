package np.com.aayamregmi.screens.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import np.com.aayamregmi.ui.theme.VHRtestappTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflexTestScreen(
    onBack: () -> Unit,
    onFinish: (Long) -> Unit = {}   // returns reaction time in ms
) {
    var screenState by remember { mutableStateOf(TestState.WaitingToStart) }
    var startTime by remember { mutableLongStateOf(0L) }
    var reactionTimeMs by remember { mutableLongStateOf(0L) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reflex Test") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Dashboard"
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Reflex Test",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tap the red area to start.\nWhen it turns green — tap as fast as possible!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(40.dp))

            // The reaction zone (starts red, turns green)
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(
                        when (screenState) {
                            TestState.WaitingToStart -> Color(0xFFE53935)   // Red
                            TestState.WaitingForGreen -> Color(0xFFE53935)
                            TestState.Green -> Color(0xFF43A047)           // Green
                            TestState.Finished -> Color(0xFF1976D2)        // Blue result
                        }
                    )
                    .clickable {
                        when (screenState) {
                            TestState.WaitingToStart -> {
                                screenState = TestState.WaitingForGreen
                                kotlinx.coroutines.MainScope().launch {
                                    delay(Random.nextLong(1500, 5000))
                                    startTime = System.currentTimeMillis()
                                    screenState = TestState.Green
                                }
                            }
                            TestState.Green -> {
                                reactionTimeMs = System.currentTimeMillis() - startTime
                                screenState = TestState.Finished
                                onFinish(reactionTimeMs)
                            }
                            TestState.Finished -> {
                                // Restart
                                screenState = TestState.WaitingToStart
                                reactionTimeMs = 0L
                            }
                            else -> {}
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                when (screenState) {
                    TestState.WaitingToStart -> {
                        Text(
                            text = "Tap to Start",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    TestState.WaitingForGreen -> {
                        Text(
                            text = "Wait...",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    TestState.Green -> {
                        Text(
                            text = "TAP NOW!",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    TestState.Finished -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Reaction time",
                                fontSize = 24.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${reactionTimeMs} ms",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (screenState == TestState.Finished) {
                Text(
                    text = "Tap the box again to restart",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private enum class TestState {
    WaitingToStart,
    WaitingForGreen,
    Green,
    Finished
}

// ───────────────────────────────────────────────
// Previews
// ───────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun ReflexTestWaitingPreview() {
    VHRtestappTheme {
        ReflexTestScreen(onBack = {})
    }
}
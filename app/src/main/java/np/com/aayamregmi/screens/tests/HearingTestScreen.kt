package np.com.aayamregmi.screens.tests

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import np.com.aayamregmi.ui.theme.VHRtestappTheme

@Composable
fun HearingTestScreen(
    onFinish: (Int) -> Unit = {}   // returns the frequency where user stopped hearing (in Hz)
) {
    var currentFrequency by remember { mutableIntStateOf(250) }       // start at 250 Hz
    var isTestRunning by remember { mutableStateOf(false) }
    var hearingThresholdHz by remember { mutableIntStateOf(0) }

    // Simple fake mapping: 250 Hz → young, 16000 Hz → older hearing loss
    val estimatedHearingAge = when {
        hearingThresholdHz == 0 -> 0
        hearingThresholdHz <= 4000 -> 20 + (hearingThresholdHz / 200)
        else -> 60 + ((hearingThresholdHz - 4000) / 200)
    }.coerceIn(18, 80)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hearing Test",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "When you stop hearing it → press stop.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Frequency display
        Text(
            text = if (isTestRunning) "${currentFrequency} Hz" else "Ready",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = if (isTestRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress bar (visualizes frequency range)
        LinearProgressIndicator(
            progress = { (currentFrequency - 250) / (16000f - 250f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (!isTestRunning) {
            // Start button
            Button(
                onClick = { isTestRunning = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Start Test", fontSize = 18.sp)
            }
        } else {
            // During test: Stop button
            Button(
                onClick = {
                    hearingThresholdHz = currentFrequency
                    isTestRunning = false
                    onFinish(currentFrequency)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("I can't hear it anymore", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Result (shown after stop)
        if (hearingThresholdHz > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your estimated hearing age",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$estimatedHearingAge years",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "(based on last frequency you could hear)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ───────────────────────────────────────────────
// Previews
// ───────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun HearingTestScreenPreview() {
    VHRtestappTheme {
        HearingTestScreen(onFinish = {})
    }
}
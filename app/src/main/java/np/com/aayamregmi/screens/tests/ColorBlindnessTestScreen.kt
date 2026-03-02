package np.com.aayamregmi.screens.tests

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import np.com.aayamregmi.ui.theme.VHRtestappTheme

@Composable
fun ColorBlindTestScreen(
    onFinish: () -> Unit
) {
    var userInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Color Blind Test",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "What number do you see in the circle?\n(If you see no number, type 0 or nothing)",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // The Ishihara plate image box
        Card(
            modifier = Modifier
                .size(320.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            AsyncImage(
                model = "https://upload.wikimedia.org/wikipedia/commons/9/9f/Ishihara_38_plate.PNG",  // ← placeholder Ishihara image
                contentDescription = "Ishihara color blindness test plate",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Input field for the number
        OutlinedTextField(
            value = userInput,
            onValueChange = {
                // Allow only digits
                if (it.all { char -> char.isDigit() }) {
                    userInput = it.take(2)  // max 2 digits usually
                }
            },
            label = { Text("Enter the number you see") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(64.dp),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            ),
            placeholder = { Text("??", fontSize = 28.sp) }
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Next / Submit button
        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            enabled = userInput.isNotBlank(),  // optional: disable if empty
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Next", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Plate 1 of 10",  // ← can be dynamic later
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AsyncImage(
    model: String,
    contentDescription: String,
    modifier: Modifier,
    contentScale: ContentScale,
    alignment: Alignment
) {
    TODO("Not yet implemented")
}

// ───────────────────────────────────────────────
// Previews
// ───────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun ColorBlindTestScreenPreview() {
    VHRtestappTheme {
        ColorBlindTestScreen(onFinish = {})
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ColorBlindTestScreenDarkPreview() {
    VHRtestappTheme(darkTheme = true) {
        ColorBlindTestScreen(onFinish = {})
    }
}
package np.com.aayamregmi.screens.tests

import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import np.com.aayamregmi.ui.theme.VHRtestappTheme
import np.com.aayamregmi.viewmodel.ColorBlindState
import np.com.aayamregmi.viewmodel.ColorBlindTestViewModel

@Composable
fun ColorBlindTestScreen(
    onBack: () -> Unit,
    vm: ColorBlindTestViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val plateIndex by vm.plateIndex.collectAsState()
    val currentImageRes by vm.currentImageRes.collectAsState()
    val score by vm.score.collectAsState()

    var userInput by remember(plateIndex) { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (state) {
            ColorBlindState.IDLE -> IdleContent(
                onBack = onBack,
                onStart = { vm.onStart() }
            )

            ColorBlindState.TESTING -> TestingContent(
                plateNumber = plateIndex + 1,
                totalPlates = vm.totalPlates,
                imageRes = currentImageRes,
                userInput = userInput,
                onInputChange = { userInput = it },
                onNext = {
                    vm.onSubmit(userInput)
                    userInput = ""
                },
                onBack = onBack
            )

            ColorBlindState.FINISHED -> FinishedContent(
                score = score,
                onRetry = { vm.onRetry() },
                onBack = onBack
            )
        }
    }
}

@Composable
private fun IdleContent(onBack: () -> Unit, onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackRow(onBack)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Color Blind Test",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You'll be shown 24 Ishihara plates.\nType the number you see in each one.\nIf you see nothing, type 0.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth(0.7f).height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Start Test", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TestingContent(
    plateNumber: Int,
    totalPlates: Int,
    @DrawableRes imageRes: Int,
    userInput: String,
    onInputChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackRow(onBack)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "What number do you see?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "(Type 0 if you see nothing)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = "Ishihara color blindness test plate",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { if (it.all { c -> c.isDigit() }) onInputChange(it.take(3)) },
                label = { Text("Enter the number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f),
                textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, textAlign = TextAlign.Center),
            )
            FilledIconButton(
                onClick = onNext,
                enabled = userInput.isNotBlank(),
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (plateNumber < totalPlates)
                        Icons.AutoMirrored.Filled.ArrowForward
                    else
                        Icons.Default.Check,
                    contentDescription = if (plateNumber < totalPlates) "Next" else "Finish"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Plate $plateNumber of $totalPlates",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FinishedContent(
    score: Float,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackRow(onBack)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Test Complete",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Score",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${score.toInt()}%",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        score >= 90 -> "Excellent color vision!"
                        score >= 70 -> "Good color vision."
                        score >= 50 -> "Some color vision deficiency detected."
                        else -> "Significant color vision deficiency detected."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Try Again", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Back to Dashboard", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun BackRow(onBack: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColorBlindTestScreenPreview() {
    VHRtestappTheme { ColorBlindTestScreen(onBack = {}) }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ColorBlindTestScreenDarkPreview() {
    VHRtestappTheme(darkTheme = true) { ColorBlindTestScreen(onBack = {}) }
}

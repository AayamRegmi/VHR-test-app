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
import androidx.lifecycle.viewmodel.compose.viewModel
import np.com.aayamregmi.ui.theme.VHRtestappTheme
import np.com.aayamregmi.viewmodel.ReflexState
import np.com.aayamregmi.viewmodel.ReflexTestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflexTestScreen(
    onBack: () -> Unit,
    vm: ReflexTestViewModel = viewModel()
) {
    val screenState by vm.state.collectAsState()
    val reactionTimeMs by vm.reactionTimeMs.collectAsState()

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
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

            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(
                        when (screenState) {
                            ReflexState.WAITING_TO_START -> Color(0xFFE53935)
                            ReflexState.WAITING_FOR_GREEN -> Color(0xFFE53935)
                            ReflexState.GREEN -> Color(0xFF43A047)
                            ReflexState.FINISHED -> Color(0xFF1976D2)
                        }
                    )
                    .clickable { vm.onTap() },
                contentAlignment = Alignment.Center
            ) {
                when (screenState) {
                    ReflexState.WAITING_TO_START -> {
                        Text(
                            text = "Tap to Start",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    ReflexState.WAITING_FOR_GREEN -> {
                        Text(
                            text = "Wait...",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    ReflexState.GREEN -> {
                        Text(
                            text = "TAP NOW!",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    ReflexState.FINISHED -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Reaction time",
                                fontSize = 24.sp,
                                color = Color.White
                            )
                            Text(
                                text = "$reactionTimeMs ms",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (screenState == ReflexState.FINISHED) {
                Text(
                    text = "Tap the box again to restart",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReflexTestWaitingPreview() {
    VHRtestappTheme {
        ReflexTestScreen(onBack = {})
    }
}

package np.com.aayamregmi

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// All screen composables
import np.com.aayamregmi.screens.credential.LoginScreen
import np.com.aayamregmi.screens.credential.RegisterScreen
import np.com.aayamregmi.screens.home.DashboardScreen
import np.com.aayamregmi.screens.home.ProfileScreen
import np.com.aayamregmi.screens.tests.ColorBlindTestScreen
import np.com.aayamregmi.screens.tests.HearingTestScreen
import np.com.aayamregmi.screens.tests.LeaderBoardScreen
import np.com.aayamregmi.screens.tests.ReflexTestScreen
import np.com.aayamregmi.session.SessionManager

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val session = SessionManager.getInstance(context)
    val startDestination = if (session.isLoggedIn) Routes.DASHBOARD else Routes.LOGIN

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(Routes.REGISTER) },
                onGuestContinue = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onGoToLogin = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onColorBlindClick  = { navController.navigate(Routes.COLOR_BLIND) },
                onHearingClick     = { navController.navigate(Routes.HEARING) },
                onReflexClick      = { navController.navigate(Routes.REFLEX) },
                onLeaderboardClick = { navController.navigate(Routes.LEADERBOARD) },
                onProfileClick     = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.COLOR_BLIND) {
            ColorBlindTestScreen(
                onFinish = { navController.popBackStack() }
            )
        }

        composable(Routes.HEARING) {
            HearingTestScreen(
                onFinish = { navController.popBackStack() }
            )
        }

        composable(Routes.REFLEX) {
            ReflexTestScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LEADERBOARD) {
            LeaderBoardScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

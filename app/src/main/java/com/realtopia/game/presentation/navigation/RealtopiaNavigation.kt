package com.realtopia.game.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.realtopia.game.presentation.ui.screen.GameScreen
import com.realtopia.game.presentation.ui.screen.MainMenuScreen

@Composable
fun RealtopiaNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "main_menu"
    ) {
        composable("main_menu") {
            MainMenuScreen(
                onNavigateToCareer = {
                    navController.navigate("game/career")
                },
                onNavigateToTimeTrial = {
                    navController.navigate("game/time_trial")
                },
                onNavigateToEndless = {
                    navController.navigate("game/endless")
                }
            )
        }
        
        composable("game/career") {
            GameScreen(
                gameMode = GameMode.CAREER
            )
        }
        
        composable("game/time_trial") {
            GameScreen(
                gameMode = GameMode.TIME_TRIAL
            )
        }
        
        composable("game/endless") {
            GameScreen(
                gameMode = GameMode.ENDLESS
            )
        }
    }
}

enum class GameMode {
    CAREER,
    TIME_TRIAL,
    ENDLESS
}

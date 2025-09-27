package com.example.jarassignment.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jarassignment.data.model.ManualBuyEducationData
import com.example.jarassignment.ui.MainEducationScreen
import com.example.jarassignment.ui.screens.IntroFadeScreen
import com.example.jarassignment.ui.screens.LandingScreen
import com.example.jarassignment.util.EducationDest

@Composable
fun EducationNavHost(
    navController: NavHostController,
    educationData: ManualBuyEducationData,
    onExitFlow: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = EducationDest.Intro.route
    ) {
        composable(EducationDest.Intro.route) {
            IntroFadeScreen(
                title = educationData.introTitle ?: "",
                subtitle = educationData.introSubtitle ?: "",
                onFinished = { navController.navigate(EducationDest.Cards.route) }
            )
        }

        composable(EducationDest.Cards.route) {
            MainEducationScreen(
                cards = educationData.educationCardList,
                apiConfig = educationData,
                actionText = educationData.actionText ?: "",
                ctaLottie = educationData.ctaLottie ?: "",
                onNavigateLanding = {
                    navController.navigate(EducationDest.Landing.route)
                }
            )
        }

        composable(EducationDest.Landing.route) {
            LandingScreen(
                toolbarText = educationData.toolBarText ?: "",
                onBack = { navController.popBackStack() }
            )
        }
    }
}

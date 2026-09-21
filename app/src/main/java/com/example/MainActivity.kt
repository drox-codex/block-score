package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.AdventureGameScreen
import com.example.ui.screens.AdventureMapScreen
import com.example.ui.screens.ClassicGameScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepNavy
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    // Handle back press gracefully across game screens
                    BackHandler(enabled = currentScreen != GameViewModel.Screen.MAIN_MENU && currentScreen != GameViewModel.Screen.SPLASH) {
                        when (currentScreen) {
                            GameViewModel.Screen.CLASSIC_GAME,
                            GameViewModel.Screen.ADVENTURE_MAP,
                            GameViewModel.Screen.ACHIEVEMENTS,
                            GameViewModel.Screen.SETTINGS,
                            GameViewModel.Screen.ONE_LINE_GAME,
                            GameViewModel.Screen.TIC_TAC_TOE_GAME -> {
                                viewModel.navigateTo(GameViewModel.Screen.MAIN_MENU)
                            }
                            GameViewModel.Screen.ADVENTURE_GAME -> {
                                viewModel.navigateTo(GameViewModel.Screen.ADVENTURE_MAP)
                            }
                            GameViewModel.Screen.MAIN_MENU,
                            GameViewModel.Screen.SPLASH -> {
                                // Default back behavior
                            }
                        }
                    }

                    // Native iOS UINavigationController style spring transition
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            val isBack = (targetState == GameViewModel.Screen.MAIN_MENU) ||
                                    (targetState == GameViewModel.Screen.ADVENTURE_MAP && initialState == GameViewModel.Screen.ADVENTURE_GAME)
                            if (isBack) {
                                (slideInHorizontally(
                                    initialOffsetX = { -it / 3 },
                                    animationSpec = spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessMediumLow)
                                ) + fadeIn(animationSpec = tween(220)))
                                    .togetherWith(
                                        slideOutHorizontally(
                                            targetOffsetX = { it },
                                            animationSpec = spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessMediumLow)
                                        ) + fadeOut(animationSpec = tween(180))
                                    )
                            } else {
                                (slideInHorizontally(
                                    initialOffsetX = { it },
                                    animationSpec = spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessMediumLow)
                                ) + fadeIn(animationSpec = tween(220)))
                                    .togetherWith(
                                        slideOutHorizontally(
                                            targetOffsetX = { -it / 3 },
                                            animationSpec = spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessMediumLow)
                                        ) + fadeOut(animationSpec = tween(180))
                                    )
                            }
                        },
                        label = "iosScreenTransition"
                    ) { targetScreen ->
                        when (targetScreen) {
                            GameViewModel.Screen.SPLASH -> SplashScreen(viewModel = viewModel)
                            GameViewModel.Screen.MAIN_MENU -> MainMenuScreen(viewModel = viewModel)
                            GameViewModel.Screen.CLASSIC_GAME -> ClassicGameScreen(viewModel = viewModel)
                            GameViewModel.Screen.ADVENTURE_MAP -> AdventureMapScreen(viewModel = viewModel)
                            GameViewModel.Screen.ADVENTURE_GAME -> AdventureGameScreen(viewModel = viewModel)
                            GameViewModel.Screen.ACHIEVEMENTS -> AchievementsScreen(viewModel = viewModel)
                            GameViewModel.Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            GameViewModel.Screen.ONE_LINE_GAME -> com.example.ui.screens.OneLineGameScreen(viewModel = viewModel)
                            GameViewModel.Screen.TIC_TAC_TOE_GAME -> com.example.ui.screens.TicTacToeScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

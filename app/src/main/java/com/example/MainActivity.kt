package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
                            GameViewModel.Screen.SETTINGS -> {
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

                    when (currentScreen) {
                        GameViewModel.Screen.SPLASH -> SplashScreen(viewModel = viewModel)
                        GameViewModel.Screen.MAIN_MENU -> MainMenuScreen(viewModel = viewModel)
                        GameViewModel.Screen.CLASSIC_GAME -> ClassicGameScreen(viewModel = viewModel)
                        GameViewModel.Screen.ADVENTURE_MAP -> AdventureMapScreen(viewModel = viewModel)
                        GameViewModel.Screen.ADVENTURE_GAME -> AdventureGameScreen(viewModel = viewModel)
                        GameViewModel.Screen.ACHIEVEMENTS -> AchievementsScreen(viewModel = viewModel)
                        GameViewModel.Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
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

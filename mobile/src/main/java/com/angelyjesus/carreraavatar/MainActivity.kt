package com.angelyjesus.carreraavatar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.angelyjesus.carreraavatar.ui.screens.AvatarSelectionScreen
import com.angelyjesus.carreraavatar.ui.screens.ConnectionScreen
import com.angelyjesus.carreraavatar.ui.screens.GameScreen
import com.angelyjesus.carreraavatar.ui.theme.CarreraAvatarTheme
import com.angelyjesus.carreraavatar.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarreraAvatarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CarreraAvatarApp(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CarreraAvatarApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val playerData by viewModel.playerData.collectAsState()
    val gameState by viewModel.gameState.collectAsState()

    when (currentScreen) {
        is MainViewModel.Screen.CONNECTION -> {
            ConnectionScreen(
                viewModel = viewModel,
                onConnectionSuccess = { /* La navegación se maneja automáticamente */ }
            )
        }
        is MainViewModel.Screen.AVATAR_SELECTION -> {
            AvatarSelectionScreen(
                viewModel = viewModel,
                onAvatarSelected = { /* La navegación se maneja automáticamente */ }
            )
        }
        is MainViewModel.Screen.GAME -> {
            GameScreen(
                viewModel = viewModel,
                playerData = playerData,
                gameState = gameState
            )
        }
    }
}
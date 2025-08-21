package com.angelyjesus.carreraavatar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.angelyjesus.carreraavatar.ui.screens.GameLobbyScreen
import com.angelyjesus.carreraavatar.ui.screens.GameScreen
import com.angelyjesus.carreraavatar.viewmodel.GameViewModel

sealed class GameScreen {
    object Lobby : GameScreen()
    object Game : GameScreen()
}

@Composable
fun GameNavigation(
    viewModel: GameViewModel
) {
    var currentScreen by remember { mutableStateOf<GameScreen>(GameScreen.Lobby) }
    
    when (currentScreen) {
        GameScreen.Lobby -> {
            GameLobbyScreen(
                viewModel = viewModel,
                onStartGame = {
                    currentScreen = GameScreen.Game
                }
            )
        }
        GameScreen.Game -> {
            GameScreen(
                viewModel = viewModel,
                onReturnToLobby = {
                    currentScreen = GameScreen.Lobby
                }
            )
        }
    }
}

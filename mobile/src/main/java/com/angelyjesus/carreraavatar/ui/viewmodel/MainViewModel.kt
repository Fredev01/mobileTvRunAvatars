package com.angelyjesus.carreraavatar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelyjesus.carreraavatar.network.ConnectionState
import com.angelyjesus.carreraavatar.network.GameState
import com.angelyjesus.carreraavatar.network.PlayerData
import com.angelyjesus.carreraavatar.network.WebSocketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Connection : AppScreen()
    object AvatarSelection : AppScreen()
    object Game : AppScreen()
}

class MainViewModel : ViewModel() {
    private val webSocketService = WebSocketService()
    
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Connection)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()
    
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private val _playerData = MutableStateFlow<PlayerData?>(null)
    val playerData: StateFlow<PlayerData?> = _playerData.asStateFlow()
    
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    init {
        viewModelScope.launch {
            webSocketService.connectionState.collect { state ->
                _connectionState.value = state
                handleConnectionStateChange(state)
            }
        }
        
        viewModelScope.launch {
            webSocketService.playerData.collect { data ->
                _playerData.value = data
                handlePlayerDataChange(data)
            }
        }
        
        viewModelScope.launch {
            webSocketService.gameState.collect { state ->
                _gameState.value = state
                handleGameStateChange(state)
            }
        }
    }

    private fun handleConnectionStateChange(state: ConnectionState) {
        when (state) {
            is ConnectionState.ERROR -> {
                // Volver a la pantalla de conexión en caso de error
                _currentScreen.value = AppScreen.Connection
            }
            else -> {
                // No cambiar pantalla automáticamente para otros estados
            }
        }
    }

    private fun handlePlayerDataChange(data: PlayerData?) {
        if (data != null) {
            // Si tenemos datos del jugador pero no avatar seleccionado, ir a selección de avatar
            if (data.selectedAvatar == null) {
                _currentScreen.value = AppScreen.AvatarSelection
            } else {
                // Si tenemos avatar seleccionado, ir al juego
                _currentScreen.value = AppScreen.Game
            }
        }
    }

    private fun handleGameStateChange(state: GameState?) {
        if (state?.isGameActive == true) {
            // Si el juego está activo, asegurar que estamos en la pantalla de juego
            if (_playerData.value?.selectedAvatar != null) {
                _currentScreen.value = AppScreen.Game
            }
        }
    }

    fun onConnectionSuccess() {
        _currentScreen.value = AppScreen.AvatarSelection
    }

    fun onAvatarSelected() {
        _currentScreen.value = AppScreen.Game
    }

    fun getWebSocketService(): WebSocketService {
        return webSocketService
    }

    fun disconnect() {
        webSocketService.disconnect()
        _currentScreen.value = AppScreen.Connection
        _playerData.value = null
        _gameState.value = null
    }

    override fun onCleared() {
        super.onCleared()
        webSocketService.disconnect()
    }
} 
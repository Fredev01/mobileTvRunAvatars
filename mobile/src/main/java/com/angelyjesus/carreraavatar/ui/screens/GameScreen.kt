package com.angelyjesus.carreraavatar.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.angelyjesus.carreraavatar.data.AvatarRepository
import com.angelyjesus.carreraavatar.model.Player
import com.angelyjesus.carreraavatar.viewmodel.MainViewModel
import com.angelyjesus.carreraavatar.viewmodel.GameState
import com.angelyjesus.carreraavatar.viewmodel.PlayerData

@Composable
fun GameScreen(
    viewModel: MainViewModel,
    playerData: PlayerData?,
    gameState: GameState?
) {
    var tapCount by remember { mutableStateOf(0) }
    
    if (playerData == null) {
        WaitingForGameScreen()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 48.dp), // Margen superior para evitar la cámara frontal
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header del juego
        Text(
            text = "¡Carrera en Progreso!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp) // Aumentar espaciado
        )

        // Botón de tap principal
        TapButton(
            onTap = {
                tapCount++
                viewModel.sendTap()
            }
        )

        Spacer(modifier = Modifier.height(32.dp)) // Aumentar espaciado

        // Contador de taps
        Text(
            text = "Tus taps: $tapCount",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp)) // Aumentar espaciado

        // Información del jugador
        PlayerInfoCard(playerData = playerData)
        
        Spacer(modifier = Modifier.height(24.dp)) // Aumentar espaciado
        
        // Información del juego
        gameState?.let { state ->
            GameInfoCard(gameState = state, tapCount = tapCount)
        }
    }
}

@Composable
fun TapButton(onTap: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "tap_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Button(
        onClick = onTap,
        modifier = Modifier
            .size(120.dp)
            .scale(scale),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = "TAP!",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun PlayerInfoCard(playerData: PlayerData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tu Información",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Nombre: ${playerData.playerName}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Sala: ${playerData.roomCode}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            playerData.selectedAvatar?.let { avatarId ->
                val avatar = AvatarRepository.getAvatarById(avatarId)
                avatar?.let {
                    Text(
                        text = "Avatar: ${it.emoji}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun GameInfoCard(gameState: GameState, tapCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Estado del Juego",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (gameState.isGameActive) {
                Text(
                    text = "¡Carrera en progreso!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Jugadores: ${gameState.players.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                // Lista de jugadores
                gameState.players.forEach { player ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val avatar = player.avatarId?.let { AvatarRepository.getAvatarById(it) }
                        avatar?.let {
                            Text(
                                text = it.emoji,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                Text(
                    text = "Esperando inicio de la carrera...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Tus taps: $tapCount",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun WaitingForGameScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 48.dp), // Margen superior para evitar la cámara frontal
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Esperando que el host inicie la carrera...",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Mantén la aplicación abierta",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 
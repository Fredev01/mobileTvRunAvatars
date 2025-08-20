package com.angelyjesus.carreraavatar.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.angelyjesus.carreraavatar.data.AvatarRepository
import com.angelyjesus.carreraavatar.network.GameState
import com.angelyjesus.carreraavatar.network.PlayerData
import com.angelyjesus.carreraavatar.network.WebSocketService

@Composable
fun GameScreen(
    webSocketService: WebSocketService,
    playerData: PlayerData?,
    gameState: GameState?
) {
    var tapCount by remember { mutableStateOf(0) }
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header con información del jugador
        PlayerInfoCard(playerData)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Información del juego
        GameInfoCard(gameState, tapCount)
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Botón de tap principal
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable {
                    tapCount++
                    webSocketService.sendTap()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "TAP!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Contador de taps
        Text(
            text = "Taps: $tapCount",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Instrucciones
        Text(
            text = "Toca el botón para acelerar tu vehículo en la carrera",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PlayerInfoCard(playerData: PlayerData?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar del jugador
            playerData?.selectedAvatar?.let { avatarId ->
                val avatar = AvatarRepository.getAvatarById(avatarId)
                avatar?.let {
                    Text(
                        text = it.emoji,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
            
            Column {
                Text(
                    text = "Tu Avatar",
                    style = MaterialTheme.typography.titleMedium
                )
                playerData?.selectedAvatar?.let { avatarId ->
                    val avatar = AvatarRepository.getAvatarById(avatarId)
                    avatar?.let {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameInfoCard(gameState: GameState?, tapCount: Int) {
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
            
            if (gameState?.isGameActive == true) {
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
                                fontSize = 16.sp,
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
            .padding(16.dp),
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
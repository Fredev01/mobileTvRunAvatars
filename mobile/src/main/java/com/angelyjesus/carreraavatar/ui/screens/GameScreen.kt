package com.angelyjesus.carreraavatar.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
            text = when (gameState?.currentState) {
                "WAITING" -> "Esperando jugadores..."
                "COUNTDOWN" -> "¡Preparados! ${gameState.countdown}"
                "RACING" -> "¡Carrera en Progreso!"
                "FINISHED" -> "¡Carrera Terminada!"
                else -> "¡Carrera en Progreso!"
            },
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp) // Aumentar espaciado
        )

        // Botón de tap principal
        TapButton(
            enabled = gameState?.currentState == "RACING" && gameState?.winner == null,
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
        PlayerInfoCard(playerData = playerData, gameState = gameState)
        
        Spacer(modifier = Modifier.height(24.dp)) // Aumentar espaciado
        
        // Información del juego
        gameState?.let { state ->
            GameInfoCard(gameState = state, tapCount = tapCount)
        }
    }
}

@Composable
fun TapButton(enabled: Boolean = true, onTap: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "tap_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (enabled) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Button(
        onClick = if (enabled) onTap else { {} },
        enabled = enabled,
        modifier = Modifier
            .size(120.dp)
            .scale(scale),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
    ) {
        Text(
            text = if (enabled) "TAP!" else "ESPERA",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun PlayerInfoCard(playerData: PlayerData, gameState: GameState?) {
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
            
            // Mostrar avatar del jugador actual desde los datos de Firebase
            gameState?.players?.find { it.id == playerData.playerId }?.let { currentPlayer ->
                currentPlayer.avatarImageUrl?.let { imageUrl ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Avatar: ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Avatar de ${playerData.playerName}",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
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
            
            // Estado actual del juego
            Text(
                text = when (gameState.currentState) {
                    "WAITING" -> "⏳ Esperando jugadores..."
                    "COUNTDOWN" -> "🚦 ¡Preparados! ${gameState.countdown}"
                    "RACING" -> "🏁 ¡Carrera en progreso!"
                    "FINISHED" -> "🏆 ¡Carrera terminada!"
                    else -> "Esperando inicio de la carrera..."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = when (gameState.currentState) {
                    "RACING" -> MaterialTheme.colorScheme.primary
                    "FINISHED" -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Jugadores: ${gameState.players.size}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Mostrar ganador si el juego terminó
            if (gameState.currentState == "FINISHED" && gameState.winner != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🏆 Ganador: ${gameState.winner.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Lista de jugadores con progreso
            gameState.players.forEach { player ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mostrar imagen de Pokémon desde Firebase
                        player.avatarImageUrl?.let { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Avatar de ${player.name}",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .padding(end = 8.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Mostrar progreso si está disponible
                    player.progress?.let { progress ->
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
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
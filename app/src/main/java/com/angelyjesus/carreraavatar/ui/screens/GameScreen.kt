package com.angelyjesus.carreraavatar.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.tv.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.angelyjesus.carreraavatar.model.Player
import com.angelyjesus.carreraavatar.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
    onReturnToLobby: () -> Unit = {}
) {
    val players by viewModel.players.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val winner by viewModel.winner.collectAsState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E),
                        Color(0xFF0D47A1),
                        Color(0xFF1565C0)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header del juego
            Text(
                text = when (gameState) {
                    "WAITING" -> "Esperando jugadores..."
                    "COUNTDOWN" -> "¡Preparados!"
                    "RACING" -> "🏁 ¡CARRERA EN CURSO! 🏁"
                    "FINISHED" -> "🏆 ¡CARRERA TERMINADA! 🏆"
                    else -> "Carrera de Avatares"
                },
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            // Canvas de la pista de carreras
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2E7D32))
            ) {
                RaceTrackCanvas(
                    players = players,
                    gameState = gameState,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Información del juego
            GameInfo(
                players = players,
                gameState = gameState,
                winner = winner,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        
        // Botón de volver al lobby
        if (gameState == "FINISHED") {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Button(
                    onClick = { 
                        viewModel.returnToLobby()
                        onReturnToLobby()
                    },
                    modifier = Modifier
                        .height(60.dp)
                        .width(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2196F3))
                ) {
                    Text(
                        text = "Volver al Lobby",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RaceTrackCanvas(
    players: List<Player>,
    gameState: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Canvas para la pista y avatares
        Canvas(modifier = Modifier.fillMaxSize()) {
            val trackWidth = size.width
            val trackHeight = size.height
            val laneHeight = trackHeight / 4
            val startX = 50f
            val finishX = trackWidth - 100f
            
            // Dibujar pista de carreras
            drawRaceTrack(trackWidth, trackHeight, laneHeight, startX, finishX)
            
            // Dibujar jugadores (solo círculos)
            players.forEachIndexed { index, player ->
                val laneY = (index + 1) * laneHeight - laneHeight / 2
                val playerX = startX + (player.progress ?: 0f) * (finishX - startX)
                
                drawPlayer(player, playerX, laneY, laneHeight)
            }
            
            // Dibujar línea de meta
            drawFinishLine(finishX, trackHeight)
        }
        
        // Nombres de jugadores superpuestos como Text composables
        players.forEachIndexed { index, player ->
            val progress = player.progress ?: 0f
            PlayerNameText(
                player = player,
                index = index,
                progress = progress,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun PlayerNameText(
    player: Player,
    index: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    // Simplificamos usando Box y valores fijos para evitar problemas de imports
    Box(modifier = modifier) {
        // Calculamos posición aproximada para el texto
        val laneOffset = (index * 25) + 100  // Aproximación de la posición Y
        val progressOffset = (progress * 300).coerceAtMost(250f)  // Aproximación de la posición X
        
        Text(
            text = player.name,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .offset(
                    x = progressOffset.dp,
                    y = laneOffset.dp
                )
        )
    }
}

private fun DrawScope.drawRaceTrack(
    trackWidth: Float,
    trackHeight: Float,
    laneHeight: Float,
    startX: Float,
    finishX: Float
) {
    // Fondo de la pista
    drawRect(
        color = Color(0xFF4CAF50),
        topLeft = Offset(0f, 0f),
        size = androidx.compose.ui.geometry.Size(trackWidth, trackHeight)
    )
    
    // Líneas de carril
    for (i in 1..3) {
        val y = i * laneHeight
        drawLine(
            color = Color.White,
            start = Offset(0f, y),
            end = Offset(trackWidth, y),
            strokeWidth = 4f
        )
    }
    
    // Línea de salida
    drawLine(
        color = Color.White,
        start = Offset(startX, 0f),
        end = Offset(startX, trackHeight),
        strokeWidth = 6f
    )
    
    // Línea de meta
    drawLine(
        color = Color(0xFFF44336),
        start = Offset(finishX, 0f),
        end = Offset(finishX, trackHeight),
        strokeWidth = 8f
    )
}

private fun DrawScope.drawPlayer(
    player: Player,
    x: Float,
    y: Float,
    laneHeight: Float
) {
    val playerSize = laneHeight * 0.6f
    
    // Dibujar avatar del jugador
    drawCircle(
        color = getPlayerColor(player.avatarId ?: "default"),
        radius = playerSize / 2,
        center = Offset(x, y)
    )
    
    // Para el texto del nombre, lo manejaremos fuera del Canvas
    // usando Text composables posicionados absolutamente
}

private fun DrawScope.drawFinishLine(finishX: Float, trackHeight: Float) {
    // Bandera de meta
    drawRect(
        color = Color(0xFFF44336),
        topLeft = Offset(finishX + 10f, 0f),
        size = androidx.compose.ui.geometry.Size(20f, trackHeight)
    )
    
    // Patrón de cuadros en la bandera
    val squareSize = 15f
    for (y in 0..(trackHeight / squareSize).toInt()) {
        for (x in 0..1) {
            val color = if ((y + x) % 2 == 0) Color.White else Color(0xFFF44336)
            drawRect(
                color = color,
                topLeft = Offset(finishX + 10f + x * 10f, y * squareSize),
                size = androidx.compose.ui.geometry.Size(10f, squareSize)
            )
        }
    }
}

private fun getPlayerColor(avatarId: String): Color {
    return when {
        avatarId.contains("red") -> Color(0xFFF44336)
        avatarId.contains("blue") -> Color(0xFF2196F3)
        avatarId.contains("green") -> Color(0xFF4CAF50)
        avatarId.contains("yellow") -> Color(0xFFFFEB3B)
        avatarId.contains("purple") -> Color(0xFF9C27B0)
        avatarId.contains("orange") -> Color(0xFFFF9800)
        avatarId.contains("pink") -> Color(0xFFE91E63)
        avatarId.contains("cyan") -> Color(0xFF00BCD4)
        else -> Color(0xFF9E9E9E)
    }
}

@Composable
private fun GameInfo(
    players: List<Player>,
    gameState: String,
    winner: Player?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (gameState) {
            "RACING" -> {
                // Mostrar progreso de la carrera
                players.forEach { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        Text(
                            text = "${((player.progress ?: 0f) * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            "FINISHED" -> {
                // Mostrar resultados
                winner?.let { winnerPlayer ->
                    Text(
                        text = "🏆 Ganador: ${winnerPlayer.name} 🏆",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                
                // Ranking de jugadores
                players.sortedByDescending { it.progress ?: 0f }.forEachIndexed { index, player ->
                    val position = index + 1
                    val emoji = when (position) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "🏅"
                    }
                    
                    Text(
                        text = "$emoji $position. ${player.name} - ${((player.progress ?: 0f) * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.tv.material3.Button(
        onClick = onClick,
        modifier = modifier
    ) {
        content()
    }
}

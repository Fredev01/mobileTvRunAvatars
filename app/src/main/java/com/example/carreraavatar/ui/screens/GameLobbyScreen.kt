package com.example.carreraavatar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.Button
import com.example.carreraavatar.ui.components.PlayersList
import com.example.carreraavatar.ui.components.RoomCodeDisplay
import com.example.carreraavatar.viewmodel.GameViewModel

@Composable
fun GameLobbyScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val roomCode by viewModel.roomCode.collectAsState()
    val players by viewModel.players.collectAsState()
    val isServerRunning by viewModel.isServerRunning.collectAsState()
    val gameStarted by viewModel.gameStarted.collectAsState()
    
    // Inicializar el ViewModel con el contexto
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }
    
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
            // Header
            Text(
                text = "Carrera de Avatares",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 32.dp)
            )
            
            // Server status
            if (isServerRunning) {
                Text(
                    text = "Servidor activo - Esperando jugadores",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                Text(
                    text = "Servidor no disponible",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFF44336),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Main content in two columns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Left column - Room code
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    RoomCodeDisplay(
                        roomCode = roomCode,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                // Right column - Players list
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    PlayersList(
                        players = players,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
            
            // Start Game Button
            if (players.isNotEmpty()) {
                Button(
                    onClick = {
                        viewModel.startGame()
                    },
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .height(56.dp)
                        .width(200.dp)
                ) {
                    Text(
                        text = "Iniciar Juego",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Footer instructions
            Text(
                text = "Usa el control remoto para navegar • Presiona OK para continuar",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

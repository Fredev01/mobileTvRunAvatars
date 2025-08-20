package com.example.appmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.appmobile.config.GameConfig
import com.example.appmobile.network.ConnectionState
import com.example.appmobile.network.WebSocketService

@Composable
fun ConnectionScreen(
    webSocketService: WebSocketService,
    onConnectionSuccess: () -> Unit
) {
    var roomCode by remember { mutableStateOf("") }
    var playerName by remember { mutableStateOf("") }
    var serverUrl by remember { mutableStateOf(GameConfig.DEFAULT_SERVER_URL) }
    
    val connectionState by webSocketService.connectionState.collectAsState()
    val playerData by webSocketService.playerData.collectAsState()

    LaunchedEffect(playerData) {
        if (playerData != null) {
            onConnectionSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Carrera Avatar",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Estado de conexión
        val currentConnectionState = connectionState
        when (currentConnectionState) {
            is ConnectionState.DISCONNECTED -> {
                ConnectionStatusCard(
                    title = "Desconectado",
                    message = "Ingresa el código de la sala para conectarte",
                    color = MaterialTheme.colorScheme.error
                )
            }
            is ConnectionState.CONNECTING -> {
                ConnectionStatusCard(
                    title = "Conectando...",
                    message = "Estableciendo conexión con el servidor",
                    color = MaterialTheme.colorScheme.primary
                )
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }
            is ConnectionState.CONNECTED -> {
                ConnectionStatusCard(
                    title = "Conectado",
                    message = "Conexión establecida. Uniéndose a la sala...",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is ConnectionState.ERROR -> {
                ConnectionStatusCard(
                    title = "Error de conexión",
                    message = currentConnectionState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Campos de entrada
        OutlinedTextField(
            value = serverUrl,
            onValueChange = { serverUrl = it },
            label = { Text("URL del servidor") },
            modifier = Modifier.fillMaxWidth(),
            enabled = connectionState is ConnectionState.DISCONNECTED
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = roomCode,
            onValueChange = { roomCode = it.uppercase().take(4) },
            label = { Text("Código de sala") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            enabled = connectionState is ConnectionState.DISCONNECTED
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text("Tu nombre") },
            modifier = Modifier.fillMaxWidth(),
            enabled = connectionState is ConnectionState.DISCONNECTED
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    if (roomCode.isNotBlank() && playerName.isNotBlank()) {
                        webSocketService.connectToServer(serverUrl)
                        webSocketService.joinRoom(roomCode, playerName)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = roomCode.isNotBlank() && 
                         playerName.isNotBlank() && 
                         connectionState is ConnectionState.DISCONNECTED
            ) {
                Text("Conectar")
            }

            if (connectionState !is ConnectionState.DISCONNECTED) {
                Button(
                    onClick = { webSocketService.disconnect() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Desconectar")
                }
            }
        }
    }
}

@Composable
fun ConnectionStatusCard(
    title: String,
    message: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
} 
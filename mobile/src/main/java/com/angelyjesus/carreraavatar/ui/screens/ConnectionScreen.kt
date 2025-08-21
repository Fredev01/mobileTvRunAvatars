package com.angelyjesus.carreraavatar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.angelyjesus.carreraavatar.viewmodel.ConnectionState
import com.angelyjesus.carreraavatar.viewmodel.MainViewModel

@Composable
fun ConnectionScreen(
    viewModel: MainViewModel,
    onConnectionSuccess: () -> Unit
) {
    var roomCode by remember { mutableStateOf("") }
    var playerName by remember { mutableStateOf("") }
    
    val connectionState by viewModel.connectionState.collectAsState()
    val playerData by viewModel.playerData.collectAsState()

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
                    message = "Estableciendo conexión con Firebase",
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
            value = roomCode,
            onValueChange = { roomCode = it.uppercase().take(4) },
            label = { Text("Código de sala") },
            modifier = Modifier.fillMaxWidth(),
            enabled = connectionState is ConnectionState.DISCONNECTED,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = playerName,
            onValueChange = { playerName = it.trim() },
            label = { Text("Tu nombre") },
            modifier = Modifier.fillMaxWidth(),
            enabled = connectionState is ConnectionState.DISCONNECTED,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de conexión
        Button(
            onClick = {
                if (roomCode.isNotEmpty() && playerName.isNotEmpty()) {
                    viewModel.connectToRoom(roomCode, playerName)
                }
            },
            enabled = roomCode.isNotEmpty() && playerName.isNotEmpty() && connectionState is ConnectionState.DISCONNECTED,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Conectar a la sala")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de desconexión
        if (connectionState !is ConnectionState.DISCONNECTED) {
            OutlinedButton(
                onClick = { viewModel.disconnect() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Desconectar")
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
                color = color.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
} 
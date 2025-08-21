package com.angelyjesus.carreraavatar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.angelyjesus.carreraavatar.data.Avatar
import com.angelyjesus.carreraavatar.data.AvatarRepository
import com.angelyjesus.carreraavatar.viewmodel.MainViewModel

@Composable
fun AvatarSelectionScreen(
    viewModel: MainViewModel,
    onAvatarSelected: () -> Unit
) {
    var selectedAvatar by remember { mutableStateOf<Avatar?>(null) }
    val playerData by viewModel.playerData.collectAsState()

    LaunchedEffect(selectedAvatar) {
        if (selectedAvatar != null) {
            selectedAvatar?.let { avatar ->
                viewModel.selectAvatar(avatar.id)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selecciona tu Avatar",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Mostrar información del jugador
        val currentPlayerData = playerData
        if (currentPlayerData != null) {
            Text(
                text = "Jugador: ${currentPlayerData.playerName}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Grid de avatares
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(AvatarRepository.availableAvatars) { avatar ->
                AvatarCard(
                    avatar = avatar,
                    isSelected = selectedAvatar?.id == avatar.id,
                    onClick = { selectedAvatar = avatar }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de confirmación
        Button(
            onClick = {
                selectedAvatar?.let { avatar ->
                    viewModel.selectAvatar(avatar.id)
                }
            },
            enabled = selectedAvatar != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar Avatar")
        }
    }
}

@Composable
fun AvatarCard(
    avatar: Avatar,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = avatar.emoji,
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
} 
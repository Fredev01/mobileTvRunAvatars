package com.angelyjesus.carreraavatar.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.angelyjesus.carreraavatar.data.Avatar
import com.angelyjesus.carreraavatar.data.AvatarRepository
import com.angelyjesus.carreraavatar.network.WebSocketService

@Composable
fun AvatarSelectionScreen(
    webSocketService: WebSocketService,
    onAvatarSelected: () -> Unit
) {
    var selectedAvatar by remember { mutableStateOf<Avatar?>(null) }
    val playerData by webSocketService.playerData.collectAsState()

    LaunchedEffect(playerData?.selectedAvatar) {
        if (playerData?.selectedAvatar != null) {
            onAvatarSelected()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Selecciona tu Avatar",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Elige el vehículo que representará tu jugador en la carrera",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Grid de avatares
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
                    webSocketService.selectAvatar(avatar.id)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedAvatar != null
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
            .aspectRatio(1f)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = avatar.emoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = avatar.name,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun AvatarSelectionStatus(
    playerData: com.angelyjesus.carreraavatar.network.PlayerData?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Estado de Selección",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (playerData?.selectedAvatar != null) {
                val avatar = AvatarRepository.getAvatarById(playerData.selectedAvatar)
                avatar?.let {
                    Text(
                        text = "Avatar seleccionado: ${it.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = it.emoji,
                        fontSize = 24.sp
                    )
                }
            } else {
                Text(
                    text = "Ningún avatar seleccionado",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 
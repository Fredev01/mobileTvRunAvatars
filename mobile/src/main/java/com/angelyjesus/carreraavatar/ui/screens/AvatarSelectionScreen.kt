package com.angelyjesus.carreraavatar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 48.dp), // Margen superior para evitar la cámara frontal
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
            columns = GridCells.Fixed(2), // 2 columnas para 6 Pokémon
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

        // Mensaje informativo
        if (selectedAvatar == null) {
            Text(
                text = "Toca un avatar para seleccionarlo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
            Text(
                text = "Avatar seleccionado: ${selectedAvatar?.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Botón de confirmación
        Button(
            onClick = {
                selectedAvatar?.let { avatar ->
                    viewModel.selectAvatar(avatar.id)
                    onAvatarSelected() // Llamar al callback después de seleccionar
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
            .size(140.dp) // Tamaño más grande para mejor visualización de Pokémon
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
        ),
        onClick = onClick,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                width = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mostrar imagen del Pokémon
                if (avatar.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(avatar.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = avatar.name,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Fallback al emoji si no hay imagen
                    Text(
                        text = avatar.emoji,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = avatar.name,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}
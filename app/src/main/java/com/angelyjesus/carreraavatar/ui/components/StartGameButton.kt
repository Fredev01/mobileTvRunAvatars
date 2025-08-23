package com.angelyjesus.carreraavatar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.tv.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.Text

@Composable
fun StartGameButton(
    onStartGame: () -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onStartGame,
        enabled = isEnabled,
        modifier = modifier
            .height(60.dp)
            .width(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isEnabled) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
            ),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🚀",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "INICIAR JUEGO",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

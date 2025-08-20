package com.example.carreraavatar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import androidx.tv.material3.lightColorScheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CarreraAvatarTheme(
    isInDarkTheme: Boolean = true, // Forzar tema oscuro para mejor experiencia en TV
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isInDarkTheme) {
        darkColorScheme(
            primary = GameBlue,
            secondary = GameGreen,
            tertiary = GameOrange,
            background = GameDarkBlue,
            surface = GameSurface,
            onPrimary = GameWhite,
            onSecondary = GameWhite,
            onTertiary = GameWhite,
            onBackground = GameWhite,
            onSurface = GameWhite
        )
    } else {
        lightColorScheme(
            primary = GameBlue,
            secondary = GameGreen,
            tertiary = GameOrange,
            background = GameLightBlue,
            surface = GameLightSurface,
            onPrimary = GameWhite,
            onSecondary = GameWhite,
            onTertiary = GameWhite,
            onBackground = GameDarkBlue,
            onSurface = GameDarkBlue
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
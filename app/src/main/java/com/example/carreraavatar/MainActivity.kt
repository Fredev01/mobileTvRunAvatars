package com.example.carreraavatar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.example.carreraavatar.ui.screens.GameLobbyScreen
import com.example.carreraavatar.ui.theme.CarreraAvatarTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarreraAvatarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    GameLobbyScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameLobbyPreview() {
    CarreraAvatarTheme {
        GameLobbyScreen()
    }
}
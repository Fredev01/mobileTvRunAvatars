package com.angelyjesus.carreraavatar

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
import com.angelyjesus.carreraavatar.navigation.GameNavigation
import com.angelyjesus.carreraavatar.ui.theme.CarreraAvatarTheme
import com.angelyjesus.carreraavatar.viewmodel.GameViewModel

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
                    GameNavigation(GameViewModel())
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameNavigationPreview() {
    CarreraAvatarTheme {
        GameNavigation(GameViewModel())
    }
}
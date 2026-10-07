package com.gempatrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gempatrack.app.ui.navigation.GempaTrackNavGraph
import com.gempatrack.app.ui.theme.GempaTrackTheme

/**
 * Entry point GempaTrack: tema Material 3 + navigation 2 screen.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GempaTrackTheme {
                GempaTrackNavGraph()
            }
        }
    }
}

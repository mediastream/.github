package com.example.mediastreamandroidtvsample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.example.mediastreamandroidtvsample.ui.theme.MediastreamAndroidTVSampleTheme
import androidx.media3.ui.PlayerView
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayer
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerConfig
import android.widget.FrameLayout
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    private var player: MediastreamPlayer? = null

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediastreamAndroidTVSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    PlayerScreen()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.releasePlayer()
    }

    @Composable
    fun PlayerScreen() {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val playerView = PlayerView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }

                val config = MediastreamPlayerConfig().apply {
                    accountID = "5fbfd5b96660885379e1a129"
                    id = "66cf33ecdb3e08cc9b5f3318"
                    type = MediastreamPlayerConfig.VideoTypes.VOD
                }

                player = MediastreamPlayer(context, config, playerView, playerView, null)
                playerView
            }
        )
    }
}
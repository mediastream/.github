package am.mediastre.mediastreamsampleapp.main

import am.mediastre.mediastreamsampleapp.audio.LiveAudioAsServiceActivity
import am.mediastre.mediastreamsampleapp.audio.AudioEpisodeActivity
import am.mediastre.mediastreamsampleapp.audio.AudioLiveActivity
import am.mediastre.mediastreamsampleapp.audio.AudioOnDemandActivity
import am.mediastre.mediastreamsampleapp.audio.AudioOnDemandAsServiceActivity
import am.mediastre.mediastreamsampleapp.audio.EpisodeAudioAsServiceActivity
import am.mediastre.mediastreamsampleapp.ui.theme.Material3ComposeTheme
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AudioScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundedCornerCard(
            content = {
                TextWithIconRow(
                    text = "Live Audio as Service",
                    icon = Icons.Default.KeyboardArrowRight
                )
            },
            onClick = {
                context.startActivity(Intent(context, LiveAudioAsServiceActivity::class.java))
            }
        )

        Spacer(modifier = Modifier.height(5.dp))

        RoundedCornerCard(
            content = {
                TextWithIconRow(
                    text = "Audio on Demand as Service",
                    icon = Icons.Default.KeyboardArrowRight
                )
            },
            onClick = {
                context.startActivity(Intent(context, AudioOnDemandAsServiceActivity::class.java))
            }
        )

        Spacer(modifier = Modifier.height(5.dp))

        RoundedCornerCard(
            content = {
                TextWithIconRow(
                    text = "Episode as Service",
                    icon = Icons.Default.KeyboardArrowRight
                )
            },
            onClick = {
                context.startActivity(Intent(context, EpisodeAudioAsServiceActivity::class.java))
            }
        )

        Spacer(modifier = Modifier.height(5.dp))

        RoundedCornerCard(
            content = {
                TextWithIconRow(
                    text = "Audio on Demand",
                    icon = Icons.Default.KeyboardArrowRight
                )
            },
            onClick = {
                context.startActivity(Intent(context, AudioOnDemandActivity::class.java))
            }
        )

        Spacer(modifier = Modifier.height(5.dp))
        RoundedCornerCard(
            content = {
                TextWithIconRow(
                    text = "Audio Live",
                    icon = Icons.Default.KeyboardArrowRight
                )
            },
            onClick = {
                context.startActivity(Intent(context, AudioLiveActivity::class.java))
            }
        )

        Spacer(modifier = Modifier.height(5.dp))
        RoundedCornerCard(
            content = {
                TextWithIconRow(
                    text = "Episode",
                    icon = Icons.Default.KeyboardArrowRight
                )
            },
            onClick = {
                context.startActivity(Intent(context, AudioEpisodeActivity::class.java))
            }
        )
    }
}

@Preview
@Composable
fun AudioScreenPreview() {
    Material3ComposeTheme {
        AudioScreen()
    }
}
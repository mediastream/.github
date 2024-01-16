package am.mediastre.mediastreamsampleapp.main

import am.mediastre.mediastreamsampleapp.ui.theme.Material3ComposeTheme
import am.mediastre.mediastreamsampleapp.video.VideoLiveActivity
import am.mediastre.mediastreamsampleapp.video.VideoLiveDvrActivity
import am.mediastre.mediastreamsampleapp.video.VideoOnDemandActivity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VideoScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundedCornerCard(
            content = {
                TextWithIconRow(
                    text = "VIDEO ON DEMAND",
                    icon = Icons.Default.KeyboardArrowRight
                )
            },
            onClick = {
                context.startActivity(Intent(context, VideoOnDemandActivity::class.java))
            }
        )

        Spacer(modifier = Modifier.height(5.dp))
        RoundedCornerCard(
            content = {
                TextWithIconRow(
                    text = "LIVE VIDEO",
                    icon = Icons.Default.KeyboardArrowRight
                )
            },
            onClick = {
                context.startActivity(Intent(context, VideoLiveActivity::class.java))
            }
        )
        Spacer(modifier = Modifier.height(5.dp))
        RoundedCornerCard(
            content = {
                TextWithIconRow(
                    text = "LIVE DVR VIDEO",
                    icon = Icons.Default.KeyboardArrowRight
                )
            },
            onClick = {
                context.startActivity(Intent(context, VideoLiveDvrActivity::class.java))
            }
        )
    }
}

@Composable
fun RoundedCornerCard(
    content: @Composable () -> Unit,
    elevation: Dp = 4.dp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(color = Color(0xFF97D700))
            .clickable { onClick() },
        elevation = elevation,
        backgroundColor = Color(0xFF97D700)
    ) {
        content()
    }
}

@Composable
fun TextWithIconRow(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontSize = 16.sp, color = Color.Black)
        Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
    }
}

@Preview
@Composable
fun VideoScreenPreview() {
    Material3ComposeTheme {
        VideoScreen()
    }
}
package am.mediastre.mediastreamsampleapp.video

import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayer
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerConfig
import am.mediastre.mediastreamsampleapp.R
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.ui.PlayerView

class VideoLiveDvrActivity : AppCompatActivity() {

    private val TAG = "SampleApp"
    private lateinit var container: FrameLayout
    private lateinit var playerView: PlayerView
    private var player: MediastreamPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videoplayer)
        val config = MediastreamPlayerConfig()
        config.id = "CONTENT_ID"
        config.accountID = "ACCOUNT_ID"
        config.type = MediastreamPlayerConfig.VideoTypes.LIVE
        config.videoFormat = MediastreamPlayerConfig.AudioVideoFormat.DASH
        config.isDebug = true
        config.trackEnable = false
        config.dvr = true
        config.windowDvr = 7200
        config.trackEnable = false
        config.dvrStart = "2020-02-28T23:00:00Z"
        config.dvrEnd = "2020-02-29T00:00:00Z"
        playerView = findViewById(R.id.player_view)
        container = findViewById(R.id.main_media_frame)

        player = MediastreamPlayer(this, config, container, playerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.releasePlayer()
    }
}
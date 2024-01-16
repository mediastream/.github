package am.mediastre.mediastreamsampleapp.video

import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayer
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerConfig
import am.mediastre.mediastreamsampleapp.R
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.ui.PlayerView

class VideoOnDemandActivity : AppCompatActivity() {

    private val TAG = "SampleApp"
    private lateinit var container: FrameLayout
    private lateinit var playerView: PlayerView
    private var player: MediastreamPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videoplayer)
        val config = MediastreamPlayerConfig()
        config.accountID = "ACCOUNT_ID"
        config.id = "CONTENT_ID"
        config.type = MediastreamPlayerConfig.VideoTypes.VOD
        config.isDebug = true
        config.trackEnable = false
        config.castAvailable = true
        playerView = findViewById(R.id.player_view)
        container = findViewById(R.id.main_media_frame)

        player = MediastreamPlayer(this, config, container, playerView, supportFragmentManager)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.releasePlayer()
    }
}
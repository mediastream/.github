package am.mediastre.mediastreamsampleapp.audio

import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayer
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerConfig
import am.mediastre.mediastreamsampleapp.R
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.ui.PlayerView

class AudioEpisodeActivity : AppCompatActivity() {

    private val TAG = "SampleApp"
    private lateinit var container: FrameLayout
    private lateinit var playerView: PlayerView
    private var player: MediastreamPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episodeplayer)
        val config = MediastreamPlayerConfig()
        config.id = "CONTENT_ID"
        config.accountID = "ACCOUNT_ID"
        config.type = MediastreamPlayerConfig.VideoTypes.EPISODE
        config.videoFormat = MediastreamPlayerConfig.AudioVideoFormat.M4A
        config.isDebug = true
        config.loadNextAutomatically = true
        config.trackEnable = false
        playerView = findViewById(R.id.player_view)
        container = findViewById(R.id.main_media_frame)

        // Pass required FragmentManager if you want to show track selection dialog
        player = MediastreamPlayer(this, config, container, playerView, supportFragmentManager)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.releasePlayer()
    }
}
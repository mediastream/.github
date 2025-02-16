package am.mediastre.mediastreamsampleapp.audio

import am.mediastre.mediastreamplatformsdkandroid.MediastreamMiniPlayerConfig
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayer
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerConfig
import am.mediastre.mediastreamsampleapp.R
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.ui.PlayerView

class AudioLiveActivity : AppCompatActivity() {

    private val TAG = "SampleApp"
    private lateinit var container: FrameLayout
    private lateinit var playerContainer: FrameLayout
    private var player: MediastreamPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        val config = MediastreamPlayerConfig()
        val miniPlayerConfig = MediastreamMiniPlayerConfig()

        config.id = "CONTENT_ID"
        config.accountID = "ACCOUNT_ID"
        config.type = MediastreamPlayerConfig.VideoTypes.LIVE
        config.videoFormat = MediastreamPlayerConfig.AudioVideoFormat.DEFAULT
        config.adURL = "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/vmap_ad_samples&sz=640x480&cust_params=sample_ar%3Dpreonly&ciu_szs=300x250%2C728x90&gdfp_req=1&ad_rule=1&output=vmap&unviewed_position_start=1&env=vp&impl=s&correlator="
        config.isDebug = true
        config.trackEnable = true
        playerContainer = findViewById(R.id.playerContainer)
        container = findViewById(R.id.main_media_frame)
        player = MediastreamPlayer(this, config, container, playerContainer, miniPlayerConfig)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.releasePlayer()
    }
}
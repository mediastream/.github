package am.mediastre.mediastreamsampleapp.audio

import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayer
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerConfig
import am.mediastre.mediastreamsampleapp.R
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TimeBar

class AudioOnDemandActivity : AppCompatActivity() {

    private val TAG = AudioOnDemandActivity::class.java.name
    private lateinit var container: FrameLayout
    private lateinit var playerView: PlayerView
    private lateinit var txtScrub: TextView
    private var player: MediastreamPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_on_demand_player)
        val config = MediastreamPlayerConfig()
        config.accountID = "ACCOUNT_ID"
        config.id = "CONTENT_ID"
        config.type = MediastreamPlayerConfig.VideoTypes.VOD
        config.videoFormat = MediastreamPlayerConfig.AudioVideoFormat.MP3
        config.loadNextAutomatically = true
        config.isDebug = true
        config.trackEnable = true
        playerView = findViewById(R.id.player_view)
        container = findViewById(R.id.main_media_frame)
        txtScrub =  findViewById(R.id.txt_scrub)

        progressBarScrub()

        player = MediastreamPlayer(this, config, container, playerView)
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun progressBarScrub(){
        val exoProgress = playerView.findViewById<DefaultTimeBar>(androidx.media3.ui.R.id.exo_progress)
        exoProgress?.addListener(object : TimeBar.OnScrubListener{
            override fun onScrubStart(timeBar: TimeBar, position: Long) {
                txtScrub.text = "onScrubStart: $position"
            }

            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                txtScrub.text = "onScrubMove: $position"
            }

            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                txtScrub.text = "onScrubStop: $position, canceled: $canceled"
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.releasePlayer()
    }
}
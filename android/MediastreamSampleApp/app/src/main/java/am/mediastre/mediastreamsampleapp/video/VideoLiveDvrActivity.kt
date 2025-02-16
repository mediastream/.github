package am.mediastre.mediastreamsampleapp.video

import am.mediastre.mediastreamplatformsdkandroid.MediastreamMiniPlayerConfig
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayer
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerConfig
import am.mediastre.mediastreamsampleapp.R
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.ui.PlayerView

class VideoLiveDvrActivity : AppCompatActivity() {

    private val TAG = "SampleApp"
    private lateinit var container: FrameLayout
    private lateinit var playerContainer: FrameLayout
    private var player: MediastreamPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videolive_player)
        val config = MediastreamPlayerConfig()

        config.accountID = "64a2f7945ea2ca18c978b025"
        config.id = "67296d011d0b3cf4bd9be46c"
        config.type = MediastreamPlayerConfig.VideoTypes.LIVE
        config.videoFormat = MediastreamPlayerConfig.AudioVideoFormat.DEFAULT
        config.environment = MediastreamPlayerConfig.Environment.DEV
        config.isDebug = true
        config.trackEnable = false
        config.dvr = true
        config.windowDvr = 7200
        config.trackEnable = false
        config.dvrStart = "2025-02-16T19:00:59.165Z"
        //config.dvrEnd = "2020-02-29T00:00:00Z"
        container = findViewById(R.id.main_media_frame)
        playerContainer = findViewById(R.id.playerContainer)

        player = MediastreamPlayer(this, config, container, playerContainer, supportFragmentManager)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUserLeaveHint() {
        player?.startPiP()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        player?.onPictureInPictureModeChanged(isInPictureInPictureMode)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.releasePlayer()
    }
}
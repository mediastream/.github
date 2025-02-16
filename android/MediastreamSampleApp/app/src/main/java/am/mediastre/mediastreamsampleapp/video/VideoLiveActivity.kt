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

class VideoLiveActivity : AppCompatActivity() {

    private val TAG = "SampleApp"
    private lateinit var container: FrameLayout
    private lateinit var playerContainer: FrameLayout
    private var player: MediastreamPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videolive_player)
        val config = MediastreamPlayerConfig()

        config.accountID = "64a2f7945ea2ca18c978b025"
        config.id = "66d749422ddd253ce04c06ce"
        config.type = MediastreamPlayerConfig.VideoTypes.LIVE
        config.videoFormat = MediastreamPlayerConfig.AudioVideoFormat.DEFAULT
        config.environment = MediastreamPlayerConfig.Environment.DEV
        config.isDebug = true
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
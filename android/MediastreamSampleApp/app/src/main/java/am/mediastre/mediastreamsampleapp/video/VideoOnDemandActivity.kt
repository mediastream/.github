package am.mediastre.mediastreamsampleapp.video

import am.mediastre.mediastreamplatformsdkandroid.MediastreamMiniPlayerConfig
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayer
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerCallback
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerConfig
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerConfig.DrmData
import am.mediastre.mediastreamsampleapp.R
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.ui.PlayerView
import com.google.ads.interactivemedia.v3.api.AdError
import com.google.ads.interactivemedia.v3.api.AdEvent
import org.json.JSONObject

class VideoOnDemandActivity : AppCompatActivity() {

    private val TAG = "SampleApp"
    private lateinit var container: FrameLayout
    private lateinit var playerView: PlayerView
    private var player: MediastreamPlayer? = null
    private var msPlayerCallback: MediastreamPlayerCallback? = null

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
        val drmHeaders: MutableMap<String, String> = HashMap()
        drmHeaders["X-AxDRM-Message"] = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2ZXJzaW9uIjoxLCJjb21fa2V5X2lkIjoiYzliNGZiYzctYmJhZC00MzEyLThiOGUtYWY4MTAxNzA5NzE1IiwibWVzc2FnZSI6eyJ0eXBlIjoiZW50aXRsZW1lbnRfbWVzc2FnZSIsInZlcnNpb24iOjEsImV4cGlyYXRpb25fZGF0ZSI6IjIwMjMtMDEtMTJUMTk6NDg6MTUuNjE2WiIsImtleXMiOlt7ImlkIjoiNTBCNURFREYtNDE5Qy00NjdGLTlBMjgtOURFRUQ0RUREQjRGIn1dfX0.gBBaKBKloGE5t2aBkrkHsIhDfixfDdWdi6xA6yEWsdM"
        config.drmData = DrmData("https://1c4e622f-drm-widevine-licensing.axprod.net/AcquireLicense", drmHeaders)

        playerView = findViewById(R.id.player_view)
        container = findViewById(R.id.main_media_frame)

        player = MediastreamPlayer(this, config, container, playerView, supportFragmentManager)



        val mediaStreamPlayerCallBack = object : MediastreamPlayerCallback {
            override fun onPlay() {
                Log.d(TAG, "mediaStreamPlayerCallBack: onPlay")
            }
            override fun onPause() {
                Log.d(TAG, "mediaStreamPlayerCallBack: onPause")
            }
            override fun onReady() {
                Log.d(TAG, "mediaStreamPlayerCallBack: onReady")
            }
            override fun onEnd() {
                Log.d(TAG, "mediaStreamPlayerCallBack: onEnd")
            }
            override fun onBuffering() {}
            override fun onError(error: String?) {}
            override fun onNext() {}
            override fun onPrevious() {}
            override fun onFullscreen() {}
            override fun offFullscreen() {}
            override fun onNewSourceAdded() {}
            override fun onLocalSourceAdded() {}
            override fun onAdEvents(type: AdEvent.AdEventType) {}
            override fun onAdErrorEvent(error: AdError) {}
            override fun onConfigChange(config: MediastreamMiniPlayerConfig?) {}
            override fun onCastAvailable(state: Boolean?) {}
            override fun onCastSessionStarting() {}
            override fun onCastSessionStarted() {}
            override fun onCastSessionStartFailed() {}
            override fun onCastSessionEnding() {}
            override fun onCastSessionEnded() {}
            override fun onCastSessionResuming() {}
            override fun onCastSessionResumed() {}
            override fun onCastSessionResumeFailed() {}
            override fun onCastSessionSuspended() {}
            override fun onPlaybackErrors(error: JSONObject?) {}
            override fun onEmbedErrors(error: JSONObject?) {}
            override fun onLiveAudioCurrentSongChanged(data: JSONObject?) {}

        }

        player?.addPlayerCallback(mediaStreamPlayerCallBack)

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
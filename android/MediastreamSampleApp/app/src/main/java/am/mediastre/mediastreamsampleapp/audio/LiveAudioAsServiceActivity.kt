package am.mediastre.mediastreamsampleapp.audio

import am.mediastre.mediastreamplatformsdkandroid.MediastreamMiniPlayerConfig
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerCallback
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerConfig
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerService
import am.mediastre.mediastreamsampleapp.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.ui.PlayerView
import com.google.ads.interactivemedia.v3.api.AdError
import com.google.ads.interactivemedia.v3.api.AdEvent
import org.json.JSONObject

class LiveAudioAsServiceActivity : AppCompatActivity() {

    private val TAG = "SampleApp"
    private lateinit var container: FrameLayout
    private lateinit var playerContainer: FrameLayout
//    private var player: MediastreamPlayer? = null
    private lateinit var miniPlayerConfig: MediastreamMiniPlayerConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        val config = MediastreamPlayerConfig()
        config.id = "CONTENT_ID"
        config.accountID = "ACCOUNT_ID"
        config.type = MediastreamPlayerConfig.VideoTypes.LIVE
        config.playerType = MediastreamPlayerConfig.PlayerType.AUDIO
        //config.environment = MediastreamPlayerConfig.Environment.DEV
        config.isDebug = true
        config.trackEnable = false
        config.showControls = true
        config.appName = "MediastreamAppTest"
        playerContainer = findViewById(R.id.playerContainer)
        container = findViewById(R.id.main_media_frame)

//        player = MediastreamPlayer(this, config, container, playerView)
        startService(config)
    }

    private fun startService(config: MediastreamPlayerConfig){
        miniPlayerConfig = MediastreamMiniPlayerConfig()

        val mediaStreamPlayerCallBack = object : MediastreamPlayerCallback {
            override fun onPlay() {
                Log.d(TAG, "PLAY_EVENT")
            }

            override fun onPause() {
                Log.d(TAG, "PAUSE_EVENT")
            }

            override fun onReady() {
                Log.d(TAG, "READY_EVENT")
            }

            override fun playerViewReady(msplayerView: PlayerView?) {

            }

            override fun onEnd() {
                Log.d(TAG, "END_EVENT")
            }

            override fun onBuffering() {
                Log.d(TAG, "BUFFERING_EVENT")
            }


            override fun onError(error: String?) {
                Log.d(TAG, "ERROR_EVENT: $error")
                Toast.makeText(this@LiveAudioAsServiceActivity, error, Toast.LENGTH_LONG).show()
            }

            override fun onNext() {
                Log.d(TAG, "NEXT_EVENT")
            }

            override fun onPrevious() {
                Log.d(TAG, "PREVIOUS_EVENT")
            }

            override fun onFullscreen() {
                Log.d(TAG, "FULLSCREEN_ON_EVENT")
            }

            override fun offFullscreen() {
                Log.d(TAG, "FULLSCREEN_OFF_EVENT")
            }

            override fun onNewSourceAdded() {
                Log.d(TAG, "NEW_SOURCE_EVENT")
            }

            override fun onLocalSourceAdded() {
                Log.d(TAG, "LOCAL_SOURCE_EVENT")
            }

            override fun onAdEvents(type: AdEvent.AdEventType) {

            }

            override fun onAdErrorEvent(error: AdError) {

            }

            override fun onConfigChange(config: MediastreamMiniPlayerConfig?) {

            }

            override fun onDismissButton() {

            }

            override fun onCastAvailable(state: Boolean?) {

            }

            override fun onCastSessionStarting() {

            }

            override fun onCastSessionStarted() {

            }

            override fun onCastSessionStartFailed() {

            }

            override fun onCastSessionEnding() {

            }

            override fun onCastSessionEnded() {

            }

            override fun onCastSessionResuming() {

            }

            override fun onCastSessionResumed() {

            }

            override fun onCastSessionResumeFailed() {

            }

            override fun onCastSessionSuspended() {

            }

            override fun onPlaybackErrors(error: JSONObject?) {
                Log.d(TAG, "PLAYBACK_ERRORS_EVENT$error")
            }

            override fun onPlayerClosed() {
                TODO("Not yet implemented")
            }

            override fun onEmbedErrors(error: JSONObject?) {
                Log.d(TAG, "EMBED_ERRORS_EVENT$error")
            }

            override fun onLiveAudioCurrentSongChanged(data: JSONObject?) {
                Log.d(TAG, "CURRENT_SONG$data")
            }
        }

        MediastreamPlayerService.initializeService(
            this,
            this@LiveAudioAsServiceActivity,
            config,
            container,
            playerContainer,
            miniPlayerConfig,
            false,
            config.accountID?:"",
            mediaStreamPlayerCallBack
        )

        val intent = Intent(this, MediastreamPlayerService::class.java)
        intent.action = "$packageName.action.startforeground"
        try {
            ContextCompat.startForegroundService(this, intent)
        } catch (e: Exception) {
            println("Exception $e")
        }
    }

    override fun onBackPressed() {
        try {
            stopService(Intent(this, MediastreamPlayerService::class.java))
        } catch (e: java.lang.Exception) {
            println("Exception $e")
        }
        super.onBackPressed()
    }
}
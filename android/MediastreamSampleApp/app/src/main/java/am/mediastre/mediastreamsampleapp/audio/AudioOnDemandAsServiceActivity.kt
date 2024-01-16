package am.mediastre.mediastreamsampleapp.audio

import am.mediastre.mediastreamplatformsdkandroid.MediastreamMiniPlayerConfig
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerCallback
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerConfig
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerService
import am.mediastre.mediastreamsampleapp.R
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.media3.ui.PlayerView
import com.google.ads.interactivemedia.v3.api.AdError
import com.google.ads.interactivemedia.v3.api.AdEvent
import org.json.JSONObject

class AudioOnDemandAsServiceActivity : AppCompatActivity() {

    private val TAG = "SampleApp"
    private lateinit var container: FrameLayout
    private lateinit var playerView: PlayerView
    private lateinit var miniPlayerConfig: MediastreamMiniPlayerConfig

    private var mBound: Boolean = false
    private lateinit var mService: MediastreamPlayerService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioasserviceplayer)
        val config = MediastreamPlayerConfig()
        config.accountID = "ACCOUNT_ID"
        config.id = "CONTENT_ID"
        config.type = MediastreamPlayerConfig.VideoTypes.VOD
        config.playerType = MediastreamPlayerConfig.PlayerType.AUDIO
        config.videoFormat = MediastreamPlayerConfig.AudioVideoFormat.MP3
        config.isDebug = true
        config.trackEnable = false
        config.showControls = true
        config.appName = "MediastreamAppTest"
        playerView = findViewById(R.id.player_view)
        container = findViewById(R.id.main_media_frame)

        setupButtons()
        startService(config)
    }

    /**
     * Create our connection to the service to be used in our bindService call.
     */
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }

        /**
         * Called after a successful bind with our VideoService.
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MediastreamPlayerService.VideoServiceBinder
            mService = binder.getService()
            mBound = true
        }
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

            override fun onEnd() {
                Log.d(TAG, "END_EVENT")
            }

            override fun onBuffering() {
                Log.d(TAG, "BUFFERING_EVENT")
            }


            override fun onError(error: String?) {
                Log.d(TAG, "ERROR_EVENT: $error")
                Toast.makeText(this@AudioOnDemandAsServiceActivity, error, Toast.LENGTH_LONG).show()
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
                Log.d(TAG, "AD_EVENTS: $type")
            }

            override fun onAdErrorEvent(error: AdError) {
                Log.d(TAG, "AD_ERROR_EVENT: $error")
            }

            override fun onConfigChange(config: MediastreamMiniPlayerConfig?) {

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

            override fun onEmbedErrors(error: JSONObject?) {
                Log.d(TAG, "EMBED_ERRORS_EVENT$error")
            }

            override fun onLiveAudioCurrentSongChanged(data: JSONObject?) {
                println("<<<<<<currentSong: $data")
            }
        }

        MediastreamPlayerService.initializeService(
            this,
            this@AudioOnDemandAsServiceActivity,
            config,
            container,
            playerView,
            miniPlayerConfig,
            false,
            config.accountID?:"",
            mediaStreamPlayerCallBack
        )
        val intent = Intent(this, MediastreamPlayerService::class.java)
        intent.action = "$packageName.action.startforeground"
        try {
            ContextCompat.startForegroundService(this, intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)

        } catch (e: Exception) {
            println("Exception $e")
        }
    }

    private fun setupButtons() {
        val btnGeo1 = findViewById<Button>(R.id.geo1)
        val btnUpdateContent = findViewById<Button>(R.id.updateContent)

        btnGeo1.setOnClickListener {
            val config = MediastreamPlayerConfig()
            config.id = "CONTENT_ID"
            config.type = MediastreamPlayerConfig.VideoTypes.EPISODE
            config.videoFormat = MediastreamPlayerConfig.AudioVideoFormat.M4A
            config.trackEnable = false
            config.showControls = true
            MediastreamPlayerService.getMsPlayer()?.reloadPlayer(config)
        }

        btnUpdateContent.setOnClickListener {
            val miniPlayerConfig = MediastreamMiniPlayerConfig()
            miniPlayerConfig.songName = "Test overrideCurrentMiniPlayerConfig title"
            miniPlayerConfig.color = android.graphics.Color.BLACK
            miniPlayerConfig.albumName = "Test Album name"
            miniPlayerConfig.description = "Test description for current notification"
            miniPlayerConfig.imageUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"
            miniPlayerConfig.imageIconUrl = androidx.media3.ui.R.drawable.exo_notification_stop
            mService.overrideCurrentMiniPlayerConfig(miniPlayerConfig)
        }

    }

    override fun onBackPressed() {
        try {
            stopService(Intent(this, MediastreamPlayerService::class.java))
            if(mBound){
                unbindService(connection)
                ServiceCompat.stopForeground(mService, ServiceCompat.STOP_FOREGROUND_DETACH)
            }
        } catch (e: java.lang.Exception) {
            println("Exception $e")
        }
        super.onBackPressed()
    }
}
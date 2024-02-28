# Welcome to the Mediastream Android SDK

Hello, Android Developer! 👋

Welcome to the Mediastream SDK for Android, designed to streamline the integration of our powerful features into your applications. This SDK provides access to advanced Mediastream capabilities, allowing you to deliver exceptional multimedia experiences to your users.

## Version
- **Version:** The current version of the SDK is 9.0.0.
- **Compatibility:** Compatible with Android API level 34 (Android 14)

## Adding Mediastream Platform SDK to Your Android Project

To integrate the Mediastream Platform SDK into your Android project, add the following dependency to your project's build.gradle file:

```gradle
implementation "io.github.mediastream:mediastreamplatformsdkandroid:9.0.0"
```

### Basic Implementation

In this minimal setup, the SDK takes care of various intricate processes, leveraging the provided account ID, content ID, and content type to ensure a seamless experience. This simplicity enables you to focus on creating engaging applications without the need for extensive configurations.

### Activity
```android
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayer
import am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerConfig

class VideoActivity : AppCompatActivity() {
    private lateinit var container: FrameLayout
    private lateinit var playerView: PlayerView
    private var player: MediastreamPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_videoplayer)
      val config = MediastreamPlayerConfig()
      config.accountID = "PLATFORM_ACCOUNT_ID"
      config.id = "CONTENT_ID"
      config.type = MediastreamPlayerConfig.VideoTypes.VOD
      playerView = findViewById(R.id.player_view)
      container = findViewById(R.id.main_media_frame)
      player = MediastreamPlayer(this, config, container, playerView)
    }
}
```

### Layout
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <FrameLayout
        android:id="@+id/main_media_frame"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="0.5"
        android:background="#000000"
        android:keepScreenOn="true">
        <androidx.media3.ui.PlayerView
            android:id="@+id/player_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent">
        </androidx.media3.ui.PlayerView>

        <fragment
            android:id="@+id/castMiniController"
            class="com.google.android.gms.cast.framework.media.widget.MiniControllerFragment"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom"
            android:visibility="gone"
            android:layout_marginBottom="70dp"/>
    </FrameLayout>

</androidx.constraintlayout.widget.ConstraintLayout>
```

# MediastreamPlayerConfig: Customizing Your Playback Experience

The `MediastreamPlayerConfig` class in the Mediastream Android SDK provides a range of properties for tailoring and enhancing your playback experience. Here's an overview of the current properties:

## **Required Parameters:**

- **`id` (String):** Video, Audio, Live or Episode ID. You can get it from Mediastream Platform.
- **`account` (String):** Account ID. You can get it from Mediastream Platform.
- **`type` (MediastreamPlayerConfig.VideoTypes):** Video type. Possible values: VOD, LIVE, EPISODE. Tells the player what type of content is going to be played.

## **Optional Parameters:**

- **`adUrl` (String):** AdURL (e.g., VAST). If not specified, will play ads configured in the Mediastream Platform.
- **`accessToken` (String):** Access token for restricted videos.
- **`autoplay` (boolean):** Autoplay video if true. Default: false.
- **`videoFormat` (MediastreamPlayerConfig.AudioVideoFormat):** Type of video (e.g., DASH). Possible values: DASH, MP4, M4A, ICECAST. Default: HLS.
- **`mute` (boolean):** Player starts muted. Default: false.
- **`dvr` (boolean):** Player starts prepared to use DVR. Default: false.
- **`windowDVR` (int):** Window DVR voiced in seconds.
- **`showControls` (boolean):** Hide the controls of the player. Default: true.
- **`AdPreloadTimeoutMs` (Long):** Allows changing the default duration (in milliseconds) for which the player must buffer while preloading an ad group before that ad group is skipped. Default: 10000ms.
- **`referer` (string):** Allows setting a custom referrer for statistics.
- **`src` (string):** Arbitrary source to reproduce.
- **`loadNextAutomatically` (boolean):** Allows playing the next episode if it exists. Available only when the EPISODE type is set. Default: false.
- **`NotificationColor` (Integer):** Allows changing Notification background color when using the player as a service.
- **`NotificationImageUrl` (String):** Allows changing Notification image when using the player as a service.
- **`NotificationDescription` (String):** Allows changing Notification description when using the player as a service.
- **`NotificationSongName` (String):** Allows changing Notification song name when using the player as a service.
- **`NotificationAlbumName` (String):** Allows changing Notification album name when using the player as a service.
- **`NotificationIconUrl` (String):** Allows changing Notification icon when using the player as a service.
- **`appName` (string):** Very useful to identify traffic in platform analytics. Example: "mediastream-app-tv" or "mediastream-app-mobile".
- **`playerId` (String):** Takes player configuration from platform settings.
- **`tryToGetMetadataFromLiveWhenAudio` (boolean):** If your live content contains TPE1 and TIT2 tags on the manifest, this metadata will be parsed and sent on `onLiveAudioCurrentSongChanged` event. Default: true.
- **`fillAutomaticallyAudioNotification` (boolean):** Show the current song playing on live content audio notification if your live content contains TPE1 and TIT2 tags on the manifest. Default: true.

# Implementing Event Handling with `MediastreamPlayerCallback`

The `MediastreamPlayerCallback` interface in the Mediastream SDK serves as the contract for handling various player events. By implementing this interface, you can listen to and respond to different states and actions during playback. Here's how you can use it:

```java
import com.mediastream.MediastreamPlayerCallback;

public class YourPlayerCallback implements MediastreamPlayerCallback {

    @Override
    public void onEnd() {
        // Called when the current video has completed playback to the end.
    }

    @Override
    public void onError() {
        // Called when an error not related to playback occurs.
    }

    @Override
    public void onPause() {
        // Called when the current video pauses playback.
    }

    // Implement other methods based on your event handling needs...

}
```

In your activity or fragment, set an instance of this callback to your Mediastream instance:

```android
MediastreamPlayerCallback playerCallback = new YourPlayerCallback();
Mediastream.addPlayerCallback(playerCallback);
```

# Event Listening in Mediastream SDK

The Mediastream SDK allows you to listen to various events emitted by the player, providing valuable hooks into the playback lifecycle. Here are the available events:

1. **`onEnd():`**
   - Called when the current video has completed playback to the end of the video.

2. **`onError():`**
   - Called when an error not related to playback occurs.

3. **`onPause():`**
   - Called when the current video pauses playback.

4. **`onPlay():`**
   - Called when the current video starts playing from the beginning.

5. **`onReady():`**
   - Called when the current video resumes playing from a paused state.

6. **`onNewSourceAdded():`**
   - Called when new settings are set.

7. **`onLocalSourceAdded():`**
   - Called when a local source is set.

8. **`onAdPlay():`**
    - Called when an Ad starts to play.

9. **`onAdPause():`**
    - Called when an Ad is paused.

10. **`onAdLoaded():`**
    - Called when an Ad is loaded.

11. **`onAdResume():`**
    - Called when an Ad is in resume mode.

12. **`onAdEnded():`**
    - Called when an Ad finishes.

13. **`onAdError():`**
    - Called when an Ad fails.

14. **`onAdSkipped():`**
    - Called when an Ad is skipped.

15. **`onAdSkippableStateChanged():`**
    - Called when the skippable state of an Ad changes.

16. **`onPlaybackErrors(JsonObject error):`**
    - Called when a playback error occurs.

17. **`onEmbedErrors(JsonObject error):`**
    - Called when an embed error occurs.

28. **`onLiveAudioCurrentSongChanged(JsonObject data):`**
    - Called when a song changes on audio live content.

These events allow you to respond dynamically to various states and actions during playback.

# Player Methods

The Mediastream player provides several methods that you can use to control playback and access various functionalities. Here is an overview of the main methods available:

## `play()`

Starts playback of the current content.

## `pause()`

Pauses playback of the current content.

## `forward(amount: Long)`

Seek to time established.

## `backward(amount: Long)`

Seek to time established.

## `seekTo(position: Long)`

Seeks to a position specified in milliseconds in the current video.

## `reloadPlayer(config: MediastreamPlayerConfig)`

Allows to reload the player with a new content without kill the player instance.

## `releasePlayer()`

When you are finished using this MediastreamPlayer, make sure to call this method to kill player instance.

## `changeSpeed(playbackSpeed: Float)`

Allows you to change the content playback speed.

# Examples

In the following example, you'll find an application showcasing various uses of the Mediastream SDK for Android. This app provides practical examples of key functionalities, including audio playback, video playback, audio as a service, casting, and more. Make sure you enter the IDs corresponding to your ACCOUNT_ID and CONTENT_ID and enjoy.

[Sample](/android/MediastreamSampleApp)

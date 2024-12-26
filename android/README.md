# Welcome to the Mediastream Android SDK

Hello, Android Developer! 👋

Welcome to the Mediastream SDK for Android, designed to streamline the integration of our powerful features into your applications. This SDK provides access to advanced Mediastream capabilities, allowing you to deliver exceptional multimedia experiences to your users.

## Version
- **Version:** The current version of the SDK is 9.2.9.
- **Compatibility:** Compatible with Android API level 34 (Android 14)

## Adding Mediastream Platform SDK to Your Android Project

To integrate the Mediastream Platform SDK into your Android project, add the following dependency to your project's build.gradle file:

```gradle
implementation "io.github.mediastream:mediastreamplatformsdkandroid:9.2.9"
```

You can see fully file on the examples in this document.

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
### Manifest
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <application
        ...
        <activity
            android:name=".main.MainActivity"
            android:supportsPictureInPicture="true"
            android:configChanges="screenSize|smallestScreenSize|screenLayout|orientation" //To support Picture in Picture
            android:exported="false" />
        </activity>
        <service android:name="am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerService" />
        <meta-data android:name="com.google.android.gms.cast.framework.OPTIONS_PROVIDER_CLASS_NAME"
            android:value="androidx.media3.cast.DefaultCastOptionsProvider"/>
    </application>
```
### Settings Gradle
In order to get youbora analytics remember add nicepeople dependencies on your graddle seetings.
```java
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url = uri("https://npaw.jfrog.io/artifactory/youbora/") }
    }
}

rootProject.name = "MediastreamAndroidTVSample"
include(":app")
```

### PiP Example

```kotlin
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class YourPlayerActivity : AppCompatActivity() {
    private lateinit var mediastreamPlayer: MediastreamPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_player)

        mediastreamPlayer = MediastreamPlayer(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUserLeaveHint() {
        mediastreamPlayer.startPiP()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        mediastreamPlayer.onPictureInPictureModeChanged(isInPictureInPictureMode)
    }
}
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
- **`addAdCustomAttribute`(key <String>, value <String>):** Allows sending custom parameters in an advertising VAST. To make it work, you need to include the *custom.* query in the VAST query strings, followed by the key you want to replace. Example: *&custom.test_ca=*. To replace it, call *config.addAdCustomAttribute("test_ca", "hi")*, which will result in the final URL being: *&custom.test_ca=hi*. (Just works if adurl is comming on the config.)

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

## `startPiP()`

Available from Android O. Allows to manage the Picture in Picture functionality.

# Examples 

In the following example, you'll find an application showcasing various uses of the Mediastream SDK for Android. This app provides practical examples of key functionalities, including audio playback, video playback, audio as a service, casting, and more. Make sure you enter the IDs corresponding to your ACCOUNT_ID and CONTENT_ID and enjoy.

[Sample App](/android/MediastreamSampleApp)

[Sample AndroidTV](/android/MediastreamAndroidTVSample)

# MediastreamPlayerServiceWithSync

## Overview

The MediastreamPlayerServiceWithSync is a service that supports audio playback with synchronization capabilities, including support for Android Auto. This guide will walk you through the steps to integrate this service into your Android application.

## Prerequisites

- Minimum SDK version set to 24 or higher
- Necessary dependencies for Media3 and AndroidX libraries

## Permissions

Firstly, you need to request the FOREGROUND_SERVICE_MEDIA_PLAYBACK permission to allow your service to run as a foreground service for media playback.

```java
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
```

## Intent Queries

Add intent queries to specify that your app can handle certain intents related to media playback and control. This is necessary for Android Auto to discover and interact with your media service.

```java
<queries>
    <intent>
        <action android:name="android.media.action.DISPLAY_AUDIO_EFFECT_CONTROL_PANEL" />
    </intent>
</queries>
<queries>
    <intent>
        <action android:name="androidx.media3.session.MediaSessionService" />
    </intent>
</queries>
```

## Service Declaration

Declare your media service (MediastreamPlayerServiceWithSync) in the manifest. This service will handle media playback and will be registered as a foreground service.

```java
<service
    android:name="am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerServi ceWithSync"
    android:exported="true" android:foregroundServiceType="mediaPlayback" android:stopWithTask="false">
    <intent-filter>
        <action android:name="androidx.media3.session.MediaSessionService" /> <action android:name="androidx.media3.session.MediaLibraryService" />
        <action android:name="android.media.browse.MediaBrowserService" /> <action android:name="android.intent.action.MEDIA_BUTTON" />
        <action android:name="android.media.action.MEDIA_PLAY_FROM_SEARCH" />
    </intent-filter>
</service>
```

## Media Button Receiver

Declare a receiver for handling media button actions. This allows your app to respond to hardware media button presses.

```java
<receiver android:name="androidx.media3.session.MediaButtonReceiver" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MEDIA_BUTTON" />
    </intent-filter>
</receiver>
```

## Android Auto Metadata

Include metadata for Android Auto, specifying the resource file that describes your app's automotive capabilities.

```java
<meta-data
    android:name="com.google.android.gms.car.application"
    android:resource="@xml/automotive_app_desc" />
```

Automotive_app_desc.xml
```java
<?xml version="1.0" encoding="utf-8"?>
<automotiveApp>
    <uses name="media" />
</automotiveApp>
```

## Implement the Activity
Create your Activity/Fragment that will use the MediastreamPlayerServiceWithSync. Here is a sample implementation:

### Request Permission

Request the necessary permissions at runtime, especially for posting notifications (required from Android 13 onwards):

```java
if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
    requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), /*requestCode= */ 0)
}
```
 
### Create MediastreamPlayerConfig Object

Initialize the MediastreamPlayerConfig object with the necessary configurations for your player:

```java
private lateinit var miniPlayerConfig: MediastreamMiniPlayerConfig
private var mBound: Boolean = false
private lateinit var mService: MediastreamPlayerServiceWithSync
val config = MediastreamPlayerConfig()
config.accountID = ""
config.id = ""
config.type = MediastreamPlayerConfig.VideoTypes.VOD config.playerType = MediastreamPlayerConfig.PlayerType.
config.videoFormat = MediastreamPlayerConfig.AudioVideoFormat.
config.appName = ""
startService(config)
```

### Service Connection

Define a connection to handle binding and unbinding the service:

```java
/**
* Create our connection to the service to be used in our bindService call. */

private val connection = object : ServiceConnection {
    override fun onServiceDisconnected(name: ComponentName?) {
        mBound = false
    }
   /**
    * Called after a successful bind with our VideoService.
    */
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service is MediastreamPlayerServiceWithSync.MusicBinder) {
            mService = service.service
            mBound = true
        }
    }
}
```

### Initialize and Start the Service

Use the initializeService method to set up the service with the player configuration. This method prepares the service to handle media playback and synchronization. Define a MediastreamPlayerCallback callback interface to handle player events such as play, pause, buffering, errors, and more. This helps in managing and responding to different states of the player.

```java
val mediaStreamPlayerCallBack = object : MediastreamPlayerCallback {
    // Implement the callback methods as needed
}
miniPlayerConfig = MediastreamMiniPlayerConfig()
MediastreamPlayerServiceWithSync.initializeService(
    context,
    activityContext,
    config,
    container,
    playerView, miniPlayerConfig,
    false, config.accountID?:"", mediaStreamPlayerCallBack
)
try {
    val intent = Intent(this, MediastreamPlayerServiceWithSync::class.java) ContextCompat.startForegroundService(this, intent)
    bindService(intent, connection, BIND_AUTO_CREATE)
} catch (e: Exception) {
    println("Exception $e")
}
```

### Unbind the Service to Stop the Player

Ensure you unbind the service to stop the player properly:

```java
try {
    val serviceIntent = Intent(this, MediastreamPlayerServiceWithSync::class.java) serviceIntent.setAction("$packageName.action.stopforeground")
    try {
        startService(serviceIntent)
        unbindService(connection)
    } catch (e: java.lang.Exception) {
        e.printStackTrace() }
    }
catch (e: java.lang.Exception) {
    println("Exception $e")
}
```

### Update Content with overrideCurrentMiniPlayerConfig

Use this method to update the content displayed in the mini-player notification:

```java
private fun updateMiniPlayerConfig() {
    val miniPlayerConfig = MediastreamMiniPlayerConfig().apply {
        songName = "" color =
        albumName = "" description = "" imageUrl = "" imageIconUrl =
    }
    mService.overrideCurrentMiniPlayerConfig(miniPlayerConfig)
}
```

### Reload Player with New Content

To reload the player with a new configuration:

```java
val config = MediastreamPlayerConfig()
config.id = ""
config.type = MediastreamPlayerConfig.VideoTypes.EPISODE
config.videoFormat = MediastreamPlayerConfig.AudioVideoFormat.M4A
MediastreamPlayerServiceWithSync.getMsPlayer()?.reloadPlayer(config)
```

# Migration from old service to new service

If you are migrating from an old service to the new MediastreamPlayerServiceWithSync, note the following changes:

## Remove Action for Starting Foreground Service:
    - Old: intent.action = "$packageName.action.startforeground"
    - New: No need to set this action explicitly.

## Remove Action for Stopping Foreground Service:
    - Old:
        serviceIntent.setAction("$packageName.action.stopforeground")
    - New: No need to set this action explicitly.

These changes simplify the integration and reduce the need for manual action setting for foreground services.

### Summary
By following these steps, you can integrate the MediastreamPlayerServiceWithSync into your Android application, ensuring support for Android Auto and efficient media playback with synchronization capabilities. The migration steps also ensure a smooth transition from the old service implementation to the new one.

# Release Notes
## [Versión 9.2.9] - 2024-12-23
- Improvements on notification player.

## [Versión 9.2.8] - 2024-12-12
- Improvements have been made to the code to improve compatibility with Fire Stick devices.

## [Versión 9.2.7] - 2024-12-05
- A framelayout is allowed as a parameter instead of just a player view to generate greater customization options for the player view.
- Improvements to playback notification behavior.

## [Versión 9.2.6] - 2024-10-04
### Bug Fixes
- Removed deprecated youbora dependency


## [Versión 9.2.5] - 2024-10-04
### Bug Fixes
- Fix metadata when DAI with google crash


## [Versión 9.2.4] - 2024-09-25
### Bug Fixes
- Fix duration issues on some AOD or VOD content


## [Versión 9.2.3] - 2024-08-22
### Bug Fixes
- Fix Ads Client Side

## [Versión 9.2.2] - 2024-08-14
### Features
- Updated media3.exoplayer version from 1.3.0 to 1.4.0
- Internal improvements to bitrate management

### Bug Fixes
- Retrieve metadata for live and non DVR content to update notification 

## [Versión 9.2.1] - 2024-08-02
### Bug Fixes
- Fix android auto sync song issue

## [Versión 9.2.0] - 2024-07-13
### Features
- Added support for Android Auto
- Updated media3.exoplayer version from 1.1.0 to 1.3.0
- Added new service for notifications and background applications

## [Versión 9.1.0] - 2024-05-23
### Features
- Picture in Picture functionality is added

## [Versión 9.0.0] - 2024-02-28
### Features
- Codebase is changed to use Kotlin instead of Android

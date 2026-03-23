# Welcome to the Mediastream Android SDK

Hello, Android Developer! 👋

Welcome to the Mediastream SDK for Android, designed to streamline the integration of our powerful features into your applications. This SDK provides access to advanced Mediastream capabilities, allowing you to deliver exceptional multimedia experiences to your users.

## Version
- **Version:** The current version of the SDK is **10.0.1** (see `MediastreamPlayer.getVersion()`).
- **Compatibility:** Targets **compileSdk 35** (Android 15). **minSdk 24**. Java **17** is required for consuming projects using the same toolchain as the SDK.

## Adding Mediastream Platform SDK to Your Android Project

To integrate the Mediastream Platform SDK into your Android project, add the following dependency to your project's build.gradle file:

```gradle
implementation "io.github.mediastream:mediastreamplatformsdkandroid:10.0.1"
```

## Highlights in 10.0.1

Major themes in this release:

- **Reels:** Vertical short-form experience when the platform configures the player as Reels (`player_skin=reels` / type `REELS` in API). Includes ads (VAST/VMAP), analytics hooks, and TV/mobile refinements.
- **Next episode:** Preview overlay before the end of VOD/EPISODE, optional **manual** flow when the app supplies `nextEpisodeId` (`nextEpisodeIncoming` → `updateNextEpisode()`), and automatic flow when the API drives the next item.
- **Picture-in-Picture:** Optional **fullscreen before PiP**, **replace Activity content** so PiP shows only the video, and correct interaction with **zoom** and **controller** visibility.
- **DVR (live):** Rich timeline and scrubbing on supported setups; **programmatic API** for custom UI (`switchToDvr`, `switchToLive`, `seekInDvr`, `isInDvrMode`, etc.) when `showControls = false` or for advanced integrations.
- **TV:** Dedicated settings and subtitle/audio dialogs, D-pad handling, focus management, and safer controller behavior during ads.
- **Ads:** Improved client- and server-side ad flows (including DAI/SSAI), **autoplay** alignment with preroll, device IDs (`rdid` / `is_lat`) for CSAI and DAI tag fallbacks (including **Fire TV** AAID where applicable).
- **Analytics & partners:** **Comscore** and **In The Game (ITG)** when enabled in platform/player configuration.
- **Subtitles & UI:** Custom **ASS** styling support, **localization** of player UI (English, Spanish, Portuguese), **edge-to-edge** / window insets on Android 15+, optional **brightness** bar and **pinch-to-zoom** on the player surface.
- **Android Auto & notifications:** Continued improvements for browsing, episodes, podcasts, and sync service flows (see service section below).

## Breaking changes (upgrading from 9.x to 10.0.1)

These are the integration points that most often require code or build changes:

1. **Build toolchain** — The SDK is built with **Java 17** and targets **compileSdk 35**. Your app module should use **Java 17** (or compatible toolchain) and **compileSdk 35** (or higher) to avoid class file / API mismatches with current AndroidX and Media3 dependencies.

2. **`MediastreamPlayerCallback`** — New methods were added over the 9.x line (e.g. `playerViewReady`, `onPlayerReload`, full Cast session callbacks, `onDismissButton`, etc.). Only **`nextEpisodeIncoming`** and **`nextEpisodeLoadRequested`** have empty default implementations in Kotlin (optional overrides). **All other methods must still be implemented** in Kotlin. **`onFullscreen`** takes **`enteredForPip: Boolean`** (including for Java: `onFullscreen(boolean)`). If you upgrade from an older callback class, **add the missing overrides** or your project will fail to compile.

3. **Edge-to-edge / window insets (Android 15+)** — Padding for system bars can be applied by the SDK when **`appHandlesWindowInsets = false`** on `MediastreamPlayerConfig`. The default is **`true`**, meaning **your activity is expected to handle insets** if you use edge-to-edge. If the player draws under the status bar after upgrading, set `appHandlesWindowInsets = false` on the config **or** apply insets in your own layout.

4. **Platform-driven Reels** — When the embed API returns a Reels-type player skin, the SDK may **hand off the UI to the Reels flow** instead of the standard `PlayerView` path. Apps that share one screen for all formats should account for this (callbacks and layout may differ).

5. **Next-episode and ads** — New flows (preview overlay, manual `nextEpisodeId` + `updateNextEpisode`, preroll/autoplay ordering) can change when **`onEnd`**, **`nextEpisodeIncoming`**, and ad-related callbacks fire compared to older builds. Review any logic that assumed a single `onEnd` at content finish.

6. **Documentation correction** — The config field is **`accountID`**, not `account`. Older snippets were wrong; the API did not rename a property—only the docs were inaccurate.

If your project already used Java 17, compileSdk 35, and a complete callback implementation, you may only need dependency version and regression testing on fullscreen/insets and next-episode behavior.

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

### Layout (Custom UI)
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
### Layout (Mediastream UI)
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".audio.AudioOnDemandActivity">

    <FrameLayout
        android:id="@+id/main_media_frame"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="0.5"
        android:background="#000000"
        android:keepScreenOn="true">
        <FrameLayout
            android:id="@+id/playerContainer"
            android:layout_width="match_parent"
            android:layout_height="match_parent">
        </FrameLayout>

        <fragment
            android:id="@+id/castMiniController"
            class="com.google.android.gms.cast.framework.media.widget.MiniControllerFragment"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom"
            android:visibility="gone"
            android:layout_marginBottom="70dp"/>

    </FrameLayout>
    <Button
        android:id="@+id/my_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Reload Test"
        android:layout_margin="16dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"/>
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

The `MediastreamPlayerConfig` class in the Mediastream Android SDK provides a range of properties for tailoring and enhancing your playback experience. Many UI and behavior toggles use **`FlagStatus`**: `ENABLE`, `DISABLE`, or `NONE` (inherit from platform/API when applicable).

## **Required parameters**

- **`id` (String):** Video, audio, live, or episode content ID from Mediastream Platform.
- **`accountID` (String):** Platform account ID.
- **`type` (`MediastreamPlayerConfig.VideoTypes`):** `VOD`, `LIVE`, or `EPISODE`.

## **Core playback & environment**

- **`environment` (`Environment`):** `DEV` or `PRODUCTION` API/embed host. Default: `PRODUCTION`.
- **`videoFormat` (`AudioVideoFormat`):** e.g. `DASH`, `MP4`, `M4A`, `MP3`, `ICECAST`, or `DEFAULT` (HLS).
- **`playerType` (`PlayerType`):** `AUDIO` or `VIDEO` / `DEFAULT` — affects UI (e.g. brightness bar, background).
- **`protocol` (String):** Default `"https"`.
- **`src` (String):** Direct media URL; skips platform JSON when set.
- **`accessToken` (String):** Access token for restricted content.
- **`referer` (String):** Custom referrer for statistics (also sent as header where applicable).
- **`autoplay` (Boolean):** Default `true`.
- **`startAt` (Int):** Start position in **seconds** (negative means not set). Works with DVR/VOD per SDK rules.
- **`volume` (Float?):** `0f`–`100f`, or `-1` to use platform default.
- **`loop` (Boolean):** Loop current item.
- **`needReload` (Boolean):** Internal use when reloading with an already-built player.
- **`playlistVideoFormat` (`AudioVideoFormat?`):** Format for playlist / next-episode transitions when applicable.
- **`drmData` (`DrmData`):** Widevine license URL and optional headers.

## **DVR & live**

- **`dvr` (Boolean):** Enable DVR-capable live behavior.
- **`windowDvr` (Int):** DVR window in **minutes** (used with API/account limits).
- **`dvrStart` / `dvrEnd` (String?):** ISO8601 timestamps for DVR window (e.g. VOD-style range or custom UI).

## **Ads**

- **`adURL` (String):** Client-side ad tag (VAST/VMAP); platform ads apply if omitted.
- **`muteAds` (`FlagStatus`):** Mute ads; `NONE` follows platform.
- **`mute`:** Not a top-level property in current config; use **`volume`** or platform settings.
- **`adPreloadTimeoutMs` (Long?):** IMA ad preload timeout (ms).
- **`vastLoadTimeoutMs` (Int?):** VAST load timeout (ms).
- **`googleImaPpid` (String):** Google IMA PPID.
- **`adTagParametersForDAI` (`MutableMap<Util.AdTagParameter, String>`):** Google DAI **setAdTagParameters** map (PPID, `cust_params`, etc.).
- **`addAdCustomAttribute(key, value)`:** Adds `custom.<key>` parameters for CSAI VAST URLs (requires matching `custom.*` placeholders in the tag).
- **`fetchDeviceIdsAsync` / `waitForDeviceIdsCache`:** Cache GAID (or Amazon AAID on Fire TV) for `rdid` / `is_lat` in **getAdQueryString** and DAI fallbacks.
- **`ensureDAITagParamsFallback(platform)`:** Fills missing PPID/RDID/IDTYPE/IS_LAT for DAI from SDK cache.

## **Next / previous episode**

- **`loadNextAutomatically` (Boolean):** Auto-advance to next episode when allowed.
- **`nextEpisodeId` (String?):** Manual next id (app-driven flow; triggers confirmation callbacks).
- **`nextEpisodeTime` (Int?):** Seconds before end to show preview / callbacks (default applied by SDK if missing).
- **`nextPrevAutomatically` (Boolean):** Related next/prev behavior flags.
- **`showDismissButton` (Boolean):** Dismiss control visibility.

## **UI & device**

- **`showControls` (Boolean):** Show built-in controls. Default `true`. Set `false` for fully custom UI (still use DVR APIs if needed).
- **`showFullScreenButton` (Boolean):** Show fullscreen button where applicable.
- **`showBrightnessBar` (Boolean):** Brightness slider in fullscreen (not for `AUDIO` player type).
- **`initialHideController` (Boolean):** Start with controller hidden, then show after a short delay (when controls are enabled).
- **`enablePlayerZoom` (Boolean):** Pinch-to-zoom on the video surface (when not disabled).
- **`adaptResizeModeToOrientation` (Boolean):** Adjust resize mode on rotation. Default `true`.
- **`appHandlesWindowInsets` (Boolean):** If `true`, the SDK **does not** apply system-bar padding on the player container (your app handles edge-to-edge). Default `true` in code — set to `false` to let the SDK pad for API 35+ edge-to-edge.
- **`applyEdgeSafeMargins` (Boolean):** Extra safe margins for dismiss/cast on edge displays.
- **`customPlayerView` (`PlayerView?`):** Inject your own `PlayerView` layout.
- **`language` (`Language`):** `ENGLISH`, `SPANISH`, `PORTUGUESE` — localized strings for settings/subtitles UI.
- **`baseColor` (Int):** Accent color (`-1` = use platform/API).
- **`showSubtitles` / `speedInControlBar` / `pauseOnScreenClick` / `pip` (`FlagStatus`):** Override platform for subtitles button, speed menu, tap-to-pause, PiP.
- **`pipExpandToFullscreenFirst` (Boolean):** Enter fullscreen briefly before PiP so PiP crops only the video.
- **`pipReplaceActivityContentWithPlayer` (Boolean):** Replace Activity content with the player before PiP (requires `Activity` context).
- **`customBackgroundForAudioPlayer` (String?):** Background image URL for **audio** content when you want a custom still instead of poster-only.
- **`forceBackPressedWhenFullScreen` (Boolean):** Forward back press after closing fullscreen.
- **`showPlaybackErrorsOnScreen` (Boolean):** Map ExoPlayer errors to on-screen messages.
- **`isDebug` (Boolean):** Verbose SDK logging.

## **Reels (platform-driven)**

- **`maxAllowedReelsTags` (Int?):** Max tags shown per reel item (default `10` in config).

## **Cast, notifications, analytics**

- **`castAvailable` (Boolean):** Enable Cast integration.
- **`playerId` (String):** Player ID from platform for skin, ads, logos, etc.
- **`appName` / `appVersion`:** Sent in analytics and stream URLs.
- **`customerID` / `distributorId` / `maxProfile`:** Business and quality parameters.
- **`notificationColor`**, **`notificationSongName`**, **`notificationDescription`**, **`notificationAlbumName`**, **`notificationImageUrl`**, **`notificationIconUrl`**, **`notificationHasNext`**, **`notificationHasPrevious`:** Notification and mini-player metadata when using the service.
- **`tryToGetMetadataFromLiveWhenAudio` / `fillAutomaticallyAudioNotification`:** Live audio metadata and notification updates.

## **Resilience & quality**

- **`automaticallyReconect` (Boolean):** Retry when offline (typo preserved in API).
- **`tryToReconnectOnPlaybackError` (Boolean):** Retry or skip on recoverable playback errors.
- **`denyAdaptativeMode` / `isMaxResolutionBasedOnScreenSize` / `isForceHighestSupportedBitrateEnabled`:** ABR / quality hints.
- **`trackEnable` (Boolean):** Collector/analytics enablement.

## **Helpers**

- **`mergePersistentFrom(previous)`:** Used on reload to keep volume, language, debug flags, etc. Consistent with `reloadPlayer` / `reloadPlayerForNextAndPrevious` behavior.
- **`toDebugString()`:** Multi-line dump for logging when `isDebug` is true.

# Implementing Event Handling with `MediastreamPlayerCallback`

The `MediastreamPlayerCallback` interface is the contract for player events. Implement it in Kotlin or Java and register with `MediastreamPlayer.addPlayerCallback(callback)`.

**Playback & UI**

- **`playerViewReady(PlayerView?)`** — Player view is ready (delayed until after setup).
- **`onPlay()` / `onPause()`** — Playback started / stopped (including after seeks where applicable).
- **`onReady()`** — Player is ready (e.g. first `STATE_READY`); also used when config propagates to mini-player.
- **`onEnd()`** — Content finished (not during mid-roll ad handoff when preroll player is active).
- **`onBuffering()`** — Entering buffering.
- **`onError(String?)`** — Non-playback or configuration errors surfaced as string messages.
- **`onPlaybackErrors(JSONObject?)`** — ExoPlayer error details (code, message).
- **`onEmbedErrors(JSONObject?)`** — Embed / API JSON errors from the platform.

**Next episode (VOD / EPISODE)**

- **`nextEpisodeIncoming(nextEpisodeId: String)`** — Fires when the SDK is about to show the next-episode UI or when the user taps Next in **manual** mode; respond with `updateNextEpisode(MediastreamPlayerConfig)` to confirm.
- **`nextEpisodeLoadRequested(nextEpisodeId: String)`** — Fires when the user chooses to load the next item from the overlay (before `reload`).

**Fullscreen & PiP**

- **`onFullscreen(enteredForPip: Boolean)`** — Entered fullscreen dialog; `enteredForPip` is `true` when fullscreen was opened only as a step before PiP (`pipExpandToFullscreenFirst`).
- **`offFullscreen()`** — Left fullscreen.

**Source & lifecycle**

- **`onNewSourceAdded(MediastreamPlayerConfig)`** — New config applied (e.g. episode change).
- **`onLocalSourceAdded()`** — Playing from `config.src`.
- **`onPlayerReload()`** — After a full `reloadPlayer` teardown/rebuild.
- **`onPlayerClosed()`** — User dismissed / closed flows tied to blocking dialogs.
- **`onDismissButton()`** — Dismiss control used (e.g. close fullscreen / back).

**Ads**

- **`onAdEvents(AdEvent.AdEventType)`** — IMA ad lifecycle.
- **`onAdErrorEvent(AdError)`** — IMA ad error.

**Cast**

- **`onCastAvailable(Boolean?)`**, **`onCastSessionStarting`**, **`onCastSessionStarted`**, **`onCastSessionStartFailed`**, **`onCastSessionEnding`**, **`onCastSessionEnded`**, **`onCastSessionResuming`**, **`onCastSessionResumed`**, **`onCastSessionResumeFailed`**, **`onCastSessionSuspended`** — Cast discovery and session lifecycle.

**Other**

- **`onConfigChange(MediastreamMiniPlayerConfig?)`** — Mini-player metadata (title, artwork, next/prev availability).
- **`onLiveAudioCurrentSongChanged(JSONObject?)`** — Live audio metadata (API + ID3 where enabled).
- **`onNext()` / `onPrevious()`** — Legacy hooks for next/previous actions from the host.

Default empty implementations exist for optional methods in Kotlin; in Java implement all methods.

Example registration:

```kotlin
val callback = object : MediastreamPlayerCallback {
    override fun playerViewReady(msplayerView: PlayerView?) { }
    override fun onPlay() { }
    override fun onPause() { }
    override fun onReady() { }
    override fun onEnd() { }
    override fun onPlayerClosed() { }
    override fun onBuffering() { }
    override fun onError(error: String?) { }
    override fun onNext() { }
    override fun onPrevious() { }
    override fun onFullscreen(enteredForPip: Boolean) { }
    override fun offFullscreen() { }
    override fun onNewSourceAdded(config: MediastreamPlayerConfig) { }
    override fun onLocalSourceAdded() { }
    override fun onAdEvents(type: AdEvent.AdEventType) { }
    override fun onAdErrorEvent(error: AdError) { }
    override fun onConfigChange(config: MediastreamMiniPlayerConfig?) { }
    override fun onCastAvailable(state: Boolean?) { }
    override fun onCastSessionStarting() { }
    override fun onCastSessionStarted() { }
    override fun onCastSessionStartFailed() { }
    override fun onCastSessionEnding() { }
    override fun onCastSessionEnded() { }
    override fun onCastSessionResuming() { }
    override fun onCastSessionResumed() { }
    override fun onCastSessionResumeFailed() { }
    override fun onCastSessionSuspended() { }
    override fun onPlaybackErrors(error: JSONObject?) { }
    override fun onEmbedErrors(error: JSONObject?) { }
    override fun onLiveAudioCurrentSongChanged(data: JSONObject?) { }
    override fun onDismissButton() { }
    override fun onPlayerReload() { }
}
player.addPlayerCallback(callback)
```

# Player Methods

The Mediastream player exposes playback control, fullscreen, PiP, Cast, next-episode helpers, DVR (live), TV key handling, and accessors. Names below match the Kotlin API.

## Playback

- **`play()` / `pause()`** — Start or pause (respects Cast, Reels, preroll, network).
- **`forward(amount: Long)` / `backward(amount: Long)`** — Seek by delta (ms) on VOD-like timelines.
- **`seekTo(position: Long)`** — Seek to position in milliseconds.
- **`changeSpeed(playbackSpeed: Float)`** — Playback speed (e.g. 1.0f).

## Reload & lifecycle

- **`reloadPlayer(config: MediastreamPlayerConfig)`** — Full re-init (destroy + setup); use when changing type/DVR/src incompatibilities.
- **`reloadPlayerForNextAndPrevious(config)`** — Lighter reload for episode changes when the SDK can reuse the same player stack.
- **`releasePlayer()`** — Release all resources; **must** be called when the screen is destroyed.
- **`updateMsConfig(config)`** — Update in-memory config reference only (does not reload media by itself).

## Picture-in-picture & fullscreen

- **`startPiP()`** — Android O+; respects `pip`, `pipExpandToFullscreenFirst`, `pipReplaceActivityContentWithPlayer`.
- **`onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean)`** — Call from `Activity` to sync controller and PiP content swap.
- **`enterFullscreen()` / `exitFullscreen()`** — Programmatic fullscreen dialog.
- **`dismissButton()`** — Dismiss UX (brightness reset, callbacks, optional back).

## Next episode

- **`updateNextEpisode(config: MediastreamPlayerConfig)`** — Confirm next episode in **manual** mode or refresh next metadata.
- **`handleNextButtonClick()`** — Use if you wire a custom Next button to the same logic as the built-in control.
- **`playNext()` / `playPrev()`** — Load next/previous episode when configured.

## DVR (live, `type == LIVE` with DVR enabled)

- **`switchToDvr(startTime: String, endTime: String? = null)`** — Jump to DVR by ISO8601 window (custom UI).
- **`switchToDvrByOffset(secondsAgo: Long, durationSeconds: Long? = null)`** — Convenience around `switchToDvr`.
- **`switchToLive()`** — Return from DVR to live when in DVR mode.
- **`isInDvrMode()`** — Whether the internal DVR state machine is active.
- **`getCurrentDvrPosition()`** — Current position (ms) in DVR window.
- **`getDvrDuration()` / `getDvrWindowDurationSeconds()`** — Window size helpers.
- **`seekInDvr(positionMs: Long)`** — Seek inside DVR stream.
- **`seekBackward(seekBackMs: Long = 10_000)` / `seekForward(seekForwardMs: Long = 10_000)`** — Live DVR ±10s style navigation (also handles non-live as simple seek where applicable).

## TV

- **`handleTVKeyEvent(keyCode: Int, event: KeyEvent): Boolean`** — Optional dispatch from `Activity` for D-pad when the default listener is not enough.
- **`showSettingsMenu()`** — Opens track/speed settings (`FragmentManager` constructor required).
- **`showSubtitleAudioMenuForTV()`** — TV subtitle/audio picker.

## Cast & Chrome

- **`showChromeCastDialog()`** — Opens Cast device picker.
- **`SendCurrentItemToCast()`** — Reload current item on Cast.

## Introspection

- **`getVersion()`** — SDK version string (e.g. `"10.0.1"`).
- **`getPlayerView()`**, **`getCurrentUrl()`**, **`getCurrentMediaConfig()`**, **`getMediaTitle()`**, **`getMediaPoster()`**, **`getCurrentPosition()`**, **`getDuration()`**, **`getContentDuration()`**, **`getResolution()`**, **`getBitrate()`**, **`getBandwidth()`**, **`getCurrentMsPlayer()`** — Debug and UI integration helpers.

## Other

- **`addPlayerCallback` / callbacks list** — Register `MediastreamPlayerCallback`.
- **`getMediastreamCallbacks()`** — Access registered callbacks.

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
    android:name="am.mediastre.mediastreamplatformsdkandroid.MediastreamPlayerServiceWithSync"
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

## [Version 10.0.1] - 2025-03-23
### Fixes:
- **Live Service Audio Sync:** Fixed an issue where the player wouldn't start due to audio sync problems.

## [Version 10.0.0] - 2025-03-23
### Features
- **Reels V2:** Vertical feed when platform configures Reels skin/type; ads, analytics, Cast, and playback stability improvements.
- **Next episode:** Preview overlay, `nextEpisodeIncoming` / `nextEpisodeLoadRequested`, manual confirmation via `updateNextEpisode`, and `mergePersistentFrom`-aware reloads.
- **PiP:** Fullscreen-first PiP, replace-activity-content mode, zoom reset, `onFullscreen(enteredForPip)`.
- **DVR:** Programmatic `switchToDvr` / `switchToLive` / `seekInDvr` and TV timeline behavior; live configuration for video streams.
- **TV:** Settings and subtitle/audio dialogs, D-pad handling, focus and ad-interaction safeguards.
- **Ads:** CSAI/SSAI/DAI improvements, device IDs for VAST/DAI (`fetchDeviceIdsAsync`), autoplay alignment with preroll.
- **Analytics:** Comscore and ITG integrations when enabled in platform config.
- **Subtitles & UI:** ASS color support, English/Spanish/Portuguese UI strings, edge-to-edge / window insets (API 35+), optional brightness bar and pinch zoom.
- **Config:** `FlagStatus` toggles, `customBackgroundForAudioPlayer`, `adaptResizeModeToOrientation`, `appHandlesWindowInsets`, `vastLoadTimeoutMs` / `adPreloadTimeoutMs`, `maxAllowedReelsTags`, and expanded `getAdQueryString` / DAI helpers.

### Notes
- **compileSdk 35**, **minSdk 24**, **Java 17**; dependency coordinates `io.github.mediastream:mediastreamplatformsdkandroid:10.0.1`.

## [Versión 9.3.3] - 2025-01-31
- Ad tag replacement for google dai

## [Versión 9.3.2] - 2025-01-31
- Allow to send custom google ima ppid.

## [Versión 9.3.1] - 2025-01-24
- Added Focus actions for TV

## [Versión 9.3.0] - 2025-01-03
- Google DAI with IMASDK instead Google DAI API.
- Improvements on UI.

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

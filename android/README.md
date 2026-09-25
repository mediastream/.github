# Welcome to the Mediastream Android SDK

Hello, Android Developer! 👋

Welcome to the Mediastream SDK for Android, designed to streamline the integration of our powerful features into your applications. This SDK provides access to advanced Mediastream capabilities, allowing you to deliver exceptional multimedia experiences to your users.

## Version
- **Version:** The current version of the SDK is **11.5.0** (see `MediastreamPlayer.getVersion()`).
- **Compatibility:** Targets **compileSdk 35** (Android 15). **minSdk 24**. Java **17** is required for consuming projects using the same toolchain as the SDK.
- **Coming from 10.0.x?** Read [Breaking changes (upgrading from 10.0.x to 11.x)](#breaking-changes-upgrading-from-100x-to-11x) first. One of them breaks the build of **every** consumer, whether or not you use the feature behind it: from **11.1.0** the SDK depends on EaseLive, and its Maven repository has to be declared in your `settings.gradle`.

## Adding Mediastream Platform SDK to Your Android Project

To integrate the Mediastream Platform SDK into your Android project, add the following dependency to your project's build.gradle file:

```gradle
implementation "io.github.mediastream:mediastreamplatformsdkandroid:11.5.0"
```

> **From 11.1.0 this dependency alone is not enough to resolve.** Add the EaseLive Maven
> repository to your `settings.gradle` / `settings.gradle.kts` as well — see
> [Settings Gradle](#settings-gradle).

## What's new in 11.5.0 — screen reader support, live latency, TV seek

- **11.5.0 — The three players (main, vertical/microdramas and reels) are usable with a screen reader (TalkBack).** Icon buttons now carry localized names that follow the player's configured language, not the device's. Revealing hidden controls works with a TalkBack double-tap and lands the cursor on play/pause. Auto-hide is suspended while the reader's cursor is on the player chrome. Loading spinners and inert seek buttons no longer appear as phantom stops, and disabled previous/next buttons report themselves as disabled. No public API or `MediastreamPlayerConfig` default changes.
  - If you pass a `customPlayerView` that already has an `AccessibilityDelegate`, the SDK now restores it on release instead of clearing it.
- **11.5.0 — Live latency no longer grows over the session.** On every live stream the SDK set the start position to 0, which Media3 treats as a requested live offset at the start of the sliding window (about a minute behind the edge). Playback speed control then spent the whole session drifting toward it, pinned at 0.9x. Live now leaves the start position unset. VOD and episodes keep position 0, and `startAt` is unchanged.
- **11.5.0 — Low-latency events follow the manifest's `PART-HOLD-BACK`.** When the Mediastream API marks an event as low latency, the SDK no longer forces its fixed 5-second live target, so each channel plays at the latency it was packaged for. Events without the flag behave exactly as before.
- **11.5.0 — Android TV: holding D-pad left/right on the progress bar seeks in manageable steps.** It used to jump `duration / 20` per key repeat (minutes on a long VOD). It now moves 10 s, then 30 s after holding for 2 s, and 60 s after 5 s. Mobile/tablet behaviour is unchanged.

## What's new in 11.4.0 — Spanish (Spain)

- **11.4.0 — New UI language: `MediastreamPlayerConfig.Language.SPANISH_SPAIN`** ("Español (España)", BCP-47 `es-ES`). Select it like any other language, `config.language = MediastreamPlayerConfig.Language.SPANISH_SPAIN`, including at runtime through `reloadPlayer(newConfig)`. It uses Spain's terminology: the live indicator reads **"Directo"** and the TV button **"Volver a directo"**. `SPANISH` (`es`, Latin American Spanish) is unchanged.
  - The name and the `es-ES` code are the same across the four Mediastream SDKs (Android, iOS, Apple TV and Roku).

## What's new in 11.3.0 — dual-render capability

- **11.3.0 — The SDK now declares whether the device can sustain two simultaneous video pipelines.** Mediastream's streaming service reads that capability to choose between server-guided ad insertion (SGAI), which stitches the ad pod on the device, and classic Google DAI; without the signal every native session is pinned to DAI. The capability travels on **both** the content configuration request and the playback URL, because the service evaluates it again when it serves the manifest — sending it on only one leaves the configuration announcing SGAI while the manifest comes back as DAI.
  - **Android is the only Mediastream SDK that resolves the default at runtime.** It comes from the device's UI mode: handhelds report `1`, Android TV / Fire TV report `0`. The scarce resources live on the TV side — a single hardware decoder, a single secure decoder for Widevine L1, one video plane — and claiming a capability the device lacks breaks playback, while withholding it only keeps today's behaviour.
  - **Cast sessions always report `0`**, whatever the local device is: the receiver does the rendering, so the local answer does not describe it.
  - The new optional **`dualRenderSupported`** config field overrides the detection — see [Ads](#ads). Leave it unset unless you are forcing a value in QA.
  - **No behaviour change in this version.** The service gates on an explicit opt-in, so `0` and an absent parameter were already equivalent, and SGAI is not enabled in production on the server side yet.

## What's new in 11.2.x

- **11.2.2 — Cast VOD: MP4 fallback when the HLS rendition fails on the receiver.** Casting a VOD whose HLS playback failed on the receiver used to die with no recovery: the cast screen stayed up, with no visible error and no retry. The SDK now watches the remote player state during **VOD** cast sessions and, on the first playback error, retries once with the MP4 rendition the backend already published for that content. It is **one-shot per session** (so it does not loop if MP4 fails too) and **does not apply to Live**, which has no MP4 equivalent — that path is unchanged.
- **11.2.1 — PlayAnywhere: the control bar auto-hides again.** With the overlay in use on mobile/tablet, tapping it gave the underlying `WebView` real view focus, which the SDK read as the user navigating the control bar: the auto-hide timer was cancelled and stayed cancelled for the whole PlayAnywhere session, leaving the bar pinned over the video. Focus landing inside the overlay container is now excluded from that check. TV was never affected.
- **11.2.0 — Live video CSAI: content resumes at the live edge after the pre-roll.** IMA plays the pre-roll in the same ExoPlayer while the live window keeps sliding, so content used to come back displaced (sometimes at a negative position), and the only recovery was a 1s timer meant for live audio, which arrived late and showed a jump. A one-shot catch-up now returns to the live edge when it detects the drift, hiding the surface until the seek settles. Applies to **live video without DVR/DAI** only — not VOD, audio, DVR or SSAI.
  - **Limitation:** on streams with no `#EXT-X-PROGRAM-DATE-TIME` tag the player cannot report a live offset, so the catch-up only has the negative-position signal to work with and does not cover "paused but still inside the window". Fixing that is a packaging change, not a player setting.
- **11.2.0 — CSAI `ads.map`: correct `ms_device` on Android TV / Fire TV.** The embed usually bakes `ms_device=android` into the map based on the media-info request's user agent, and the ad map uses that value — not `platformType` — to pick the Android TV custom params. The SDK now writes `ms_device` with the device it actually detected (`android` / `androidtv` / `firetv`), replacing the embed's value.

## What's new in 11.1.0 — PlayAnywhere

- **PlayAnywhere (EaseLive)** — optional interactive overlay on top of the player for **Live** content, served by [EaseLive](https://easelive.tv). It turns on only when the platform returns a `playAnywhere` block in the content's config; without that block the SDK initialises nothing and behaves exactly as before.
  - New public API on `MediastreamPlayer`: `togglePlayAnywhere()`, `isPlayAnywhereActive()`, `onBackPressed()`, `playAnywhereStatusListener` and `playAnywhereAdBlockedListener`.
  - The toggle lives **inside the control bar** (`playanywhere_btn`), on TV and on mobile — it is not a separate floating button.
  - `alwaysVisible` mode is configured in the account, not by your app: the overlay stays visible with no toggle button. On TV, the remote's back redirects D-pad focus to the native control bar instead of closing the overlay (it cannot be closed in this mode); a second back with no interaction in between leaves the screen normally.
  - The overlay hides itself automatically during ad breaks, both CSAI and DAI/SSAI.
  - **⚠️ It adds a mandatory dependency for every consumer**, whether or not you use PlayAnywhere: `tv.easelive:easelivesdk:2.16.0`. You must declare the EaseLive Maven repository — see [Settings Gradle](#settings-gradle) — or dependency resolution fails on your next build.
- **11.1.0 — ITG is now also blocked during DAI/SSAI ad breaks.** Previously ITG was only blocked in CSAI. **This is a functional change if you use ITG with DAI:** your interactive overlays now go inert during server-side breaks. In CSAI the unblock happens a few milliseconds earlier than before; the end state is the same.
- **11.1.0 — PlayAnywhere stayed invisible after a DVR mode change during an ad break** — the internal "blocked by ad" flag was only cleared by an IMA unblock event, and switching DVR mode replaces the media item and re-prepares, discarding that pending event. The state is now reconciled against the player's real `isPlayingAd` on every ready state.

## What's new in 11.0.x

- **11.0.3 — The device sleeps again.** The screen never dimmed, not even with the player paused: four SDK layouts had `keepScreenOn="true"` hardcoded in XML, and Android ORs that flag across the whole attached view hierarchy, so the SDK's own toggle never had any effect. Worst case was the logo, which is never removed once added — with a logo enabled the screen never slept, whatever playback was doing. Reported by Amazon on Fire TV / FireOS 14. Also fixed in the same release: the screen could switch off *during* buffering or startup (the toggle keyed off `isPlaying`, which is `false` while buffering, resolving DRM or waiting on the API), it stayed on after `releasePlayer()` for hosts using `customPlayerView`, and Reels kept it on indefinitely after a single ad.
- **11.0.2 — CSAI: unsupported ad renditions are filtered out.** IMA picked whatever `MediaFile` the VAST response listed first, including formats ExoPlayer cannot decode, so those creatives failed to render and the slot was wasted. All four client-side ad loaders now declare the formats the player actually supports, so IMA picks a playable rendition instead. Applies to the main player (CSAI and the DAI pre-roll path) and to Reels alike.
- **11.0.1 — `noAds` (config):** New `noAds: Boolean` on `MediastreamPlayerConfig` that suppresses **client-side** ads — both `adURL` and the ad map returned by the API. It does **not** affect DAI/SSAI, which is stitched server-side and keeps playing. Default `false`, and the policy propagates to next/previous episode configs, since it applies to the whole playlist.
- **11.0.1 — Picture-in-Picture on API 31+ triggers from the Recents button.** `onUserLeaveHint()` is inconsistent across OEMs for the transition to Recents/Overview (confirmed on Samsung One UI), which left audio playing with no PiP window. The SDK now arms auto-enter PiP once the player is prepared, so the system handles Home, Recents and gestures on API 31+. Manual entry via `onUserLeaveHint()` is unchanged for API 26–30. Not armed in vertical mode, where PiP is disabled.
- **11.0.1 — Android Auto:** several fixes — a 403 when switching content (the previous content's access token leaked into the new URL), episodes losing the transport arrows after a live, auto-advance hanging between episodes, and the stream restarting from zero when a controller re-bound to playback already in progress. New: `AndroidAutoContentChangedEvent` (sticky EventBus event published when Android Auto picks new content, so your UI can follow what the car is playing) and `MediastreamPlayerServiceWithSync.reinitializePlayerIfStale()`.
- **11.0.1 — `isReleased()` and `removePlayerCallback()`:** to tell whether an instance is a shell after `releasePlayer()`, and to unregister a callback when your client dies before the player.
- **11.0.1 — Live audio notification artwork:** for audio lives the `preview_thumbnail` is a frame grab of a stream that has no frames; the station logo is now preferred when there is no poster. Video lives keep their previous poster behaviour.
- **11.0.0 — Vertical player and the Reels/ViewPager rework**, plus the `vpmute` ad tag parameter. The vertical experience has its own guide in the SDK repository: [`VERTICAL_PLAYER_GUIDE.md`](https://github.com/mediastream/MediastreamPlatformSDKAndroid/blob/master/VERTICAL_PLAYER_GUIDE.md). Note its documented limitations — **no DRM, no PiP and no Chromecast in vertical mode**; a DRM config now logs and reports `onError` instead of showing a black screen.
- **11.0.0 — Cast: Activity leak on every session.** A `RemoteMediaClient` listener with six empty overrides held the player (and through it your Activity) for the life of the process when you left the app while still casting. It is gone, and the client reference is cleared on release and on session end.
- **11.0.0 — `vpmute` was sent under the wrong name for wrapped GAM tags**, so it was silently dropped for exactly the wrapper setup it was added to support. See [Breaking changes (upgrading from 10.0.x to 11.x)](#breaking-changes-upgrading-from-100x-to-11x) for the two API changes in this release.

## Previous major: what's new in the 10.0.x line

The sections below cover the **10.0.x** line, which is no longer the current one. They are kept
for apps still on 10.0.x and for anyone reading an older integration. Production releases
**10.0.7 through 10.0.15** shipped after 10.0.6 and are documented in the
[Release Notes](#release-notes) rather than here.

### What's new in 10.0.6

- **10.0.6 — Konodrac analytics:** New analytics partner integration. When the platform player config includes `tracking.konodrac.enabled = true` and a `dataset_id`, the SDK automatically sends playback events to Konodrac (play, pause, seek, fullscreen, mute, end, heartbeat, DVR/catchup transitions, and dispose). No code change required for most apps — the integration is platform-configured.
- **10.0.6 — `konodracChannel` (config):** New optional `konodracChannel: String?` on `MediastreamPlayerConfig`. Overrides the channel identifier sent to Konodrac. Falls back to `appName` if not set, then defaults to `"mdstrm-android-player"`.

### What's new in 10.0.5

- **10.0.5 — `onFullscreenOnClick` (config):** New `onFullscreenOnClick: Consumer<MediastreamPlayer>?` property on `MediastreamPlayerConfig`. When set, this consumer fires instead of the default `enterFullscreen()` when the fullscreen-on button is tapped. Designed for the same React Native / bridge use cases as `onFullscreenOffClick`.
- **10.0.5 — TV remote debounce seek:** D-pad left/right on TV remotes now debounces seeks for both VOD and Live+DVR streams. Prevents multiple rapid seek events when the user holds or quickly taps the D-pad direction keys, improving seek accuracy on TV devices.
- **10.0.5 — Notification next/previous buttons (service):** Fixed `NEXT_BUTTON` and `PREVIOUS_BUTTON` custom session commands in `MediastreamPlayerServiceWithSync` that were incorrectly disabled. The next-button handler now requires both `loadNextAutomatically == true` and `msMiniPlayerConfig.setStateNext == true` to trigger auto-advance, aligning the notification behavior with the player config. `onConfigChange` now preserves `songName` and `imageUrl` in the mini-player config when `loadNextAutomatically` is `false`, so the notification stays current on content changes.
- **10.0.5 — Notification update fix:** `UpdateNotificationEvent` now correctly refreshes notification actions (previously the action list could become stale after a config update).

### What's new in 10.0.4

- **10.0.4 — Profile ID (`profileID`):** New analytics field on `MediastreamPlayerConfig`. Pass a viewer or subscriber profile identifier to include it in platform analytics calls.
- **10.0.4 — `onFullscreenOffClick` (config):** New `onFullscreenOffClick: Consumer<MediastreamPlayer>?` property on `MediastreamPlayerConfig`. When set, this consumer fires instead of the default `exitFullscreen()` when the fullscreen-off button is tapped — useful for React Native or other bridge environments where a direct synchronous call avoids event-delay issues.
- **10.0.4 — Google DAI DASH support:** The SDK now resolves the correct asset key for **DASH** DAI streams using the platform-supplied `google_dai_assetKey_dash` field. Format selection (HLS vs. DASH) is driven automatically by `videoFormat` / `playlistVideoFormat`.
- **10.0.4 — Google DAI + DRM fix:** Widevine DRM is now correctly applied to DAI/SSAI streams during DVR transitions. `CustomMediaSourceFactory` uses a DRM supplier so the license is preserved even when the IMA-based MediaItem carries no DRM in its `localConfiguration`.
- **10.0.4 — DVR seeking (TV):** Fixed an intermittent DVR seeking failure on TV devices.
- **10.0.4 — NPAW/Youbora upgrade to v7.3:** The SDK migrated from the old Youbora v6 library (`com.nicepeopleatwork:media3-adapter`) to **NPAW Plugin v7.3** (`com.npaw.plugin:plugin:7.3.25`). If you pin the Youbora Maven repository in your project, **update the URL** (see Settings Gradle section below). Ad events (quartiles, skip, click, pause/resume, ad-break boundaries) are now forwarded to the analytics SDK.

### What's new in 10.0.3 and 10.0.2

Patch releases on top of **10.0.1** (no new public API breaks documented below):

- **10.0.3 — Volume:** Volume is **persisted for the session** across reloads and transitions where the SDK merges config.
- **10.0.3 — Local source (`config.src`):** Initial **setup skips unnecessary network/embed** work when the item is played from a direct local URL.
- **10.0.3 — Ads & DVR:** **AdsLoader** is reset when **DVR mode** is activated to avoid stale ad state.
- **10.0.3 — Google DAI + VOD:** Fix for **indefinite loading** on some VOD items using **Google DAI**.
- **10.0.3 — Preroll & audio background:** Fixes for **end of preroll** handoff; **background image** sizing and **fullscreen** resize behavior for the audio player background.
- **10.0.3 — Tracks UI:** Clearer **audio track labels** in the track selection dialog.
- **10.0.3 — Metadata:** Correct handling when **metadata from the content source** was incorrectly overridden (custom/local media source path).
- **10.0.2 — Display:** `PlayerView` **`keepScreenOn`** follows **actual playback** (`onIsPlayingChanged`), including **preroll** listeners, so the device is less likely to dim or sleep while content or ads are playing.

### Highlights in the 10.0.x line

Major themes in the **10.0** release family (see release notes for patch details):

- **Reels:** Vertical short-form experience when the platform configures the player as Reels (`player_skin=reels` / type `REELS` in API). Includes ads (VAST/VMAP), analytics hooks, and TV/mobile refinements.
- **Next episode:** Preview overlay before the end of VOD/EPISODE, optional **manual** flow when the app supplies `nextEpisodeId` (`nextEpisodeIncoming` → `updateNextEpisode()`), and automatic flow when the API drives the next item.
- **Picture-in-Picture:** Optional **fullscreen before PiP**, **replace Activity content** so PiP shows only the video, and correct interaction with **zoom** and **controller** visibility.
- **DVR (live):** Rich timeline and scrubbing on supported setups; **programmatic API** for custom UI (`switchToDvr`, `switchToLive`, `seekInDvr`, `isInDvrMode`, etc.) when `showControls = false` or for advanced integrations.
- **TV:** Dedicated settings and subtitle/audio dialogs, D-pad handling, focus management, and safer controller behavior during ads.
- **Ads:** Improved client- and server-side ad flows (including DAI/SSAI), **autoplay** alignment with preroll, device IDs (`rdid` / `is_lat`) for CSAI and DAI tag fallbacks (including **Fire TV** AAID where applicable).
- **Analytics & partners:** **Comscore** and **In The Game (ITG)** when enabled in platform/player configuration.
- **Subtitles & UI:** Custom **ASS** styling support, **localization** of player UI (English, Spanish, Spanish (Spain), Portuguese), **edge-to-edge** / window insets on Android 15+, optional **brightness** bar and **pinch-to-zoom** on the player surface.
- **Android Auto & notifications:** Continued improvements for browsing, episodes, podcasts, and sync service flows (see service section below).

## Breaking changes (upgrading from 10.0.x to 11.x)

Four items in the **11.x** line need action from an integrator. Everything else in 11.x is
additive or internal.

1. **⚠️ EaseLive Maven repository, from 11.1.0 — affects every consumer.** The SDK now depends on
   `tv.easelive:easelivesdk:2.16.0`, which is not on Maven Central. **Whether or not you use
   PlayAnywhere**, you must add the EaseLive repository to your `settings.gradle` /
   `settings.gradle.kts` (see [Settings Gradle](#settings-gradle)) or dependency resolution fails
   on your next build. The EaseLive manifest also merges the `ACCESS_NETWORK_STATE` permission and
   a `PermissionActivity` into your app. It does **not** raise the SDK's `minSdk`.

2. **Java hosts must implement five new `MediastreamPlayerCallback` methods (11.0.0).**
   `onSwipeToItem`, `onEndReached`, `onLockedEpisode`, `onVerticalShare` and `onVerticalLike` were
   added for the vertical player. They have Kotlin default bodies, but the module is not compiled
   with `-Xjvm-default=all`, so those defaults live in `DefaultImpls` and **Java implementors do
   not inherit them** — a Java class implementing this interface must provide all five to compile.
   **Kotlin implementors are unaffected.**

3. **`onEpisodeInfoClick` was removed (11.0.0)**, both overloads. The episode-counter badge that
   fired it had already been deleted in the 11.0.0 UI pass, so the callback had no call site
   anywhere in the SDK — a host implementing it never received an event. Remove the override.

4. **ITG is now blocked during DAI/SSAI ad breaks (11.1.0).** Until then ITG was only blocked in
   CSAI, because the DAI stream manager never emits the CSAI pause event. This is the intended
   behaviour — it makes DAI match what CSAI already did — but it **is a functional change if you
   use ITG with DAI**: your interactive overlays now go inert during server-side breaks.

One more thing worth checking, from the 10.0.14 metadata work: if your app relied on **selective
field persistence across `reloadPlayer()`** (for example `imageUrl` carrying over without being
re-injected), you must now re-supply all mini-player fields on each reload via
`onNewSourceAdded()` / `UpdateNotificationEvent`. `reloadPlayer()` creates a fresh
`MediastreamMiniPlayerConfig` with every field cleared.

## Breaking changes (upgrading from 9.x to 10.0.x)

These are the integration points that most often require code or build changes when moving from **9.x** to **10.0.x**. Patch releases **10.0.2** and **10.0.3** are **bugfix and behavior refinements** relative to **10.0.1**; they do not add new breaking items to this list.

1. **Build toolchain** — The SDK is built with **Java 17** and targets **compileSdk 35**. Your app module should use **Java 17** (or compatible toolchain) and **compileSdk 35** (or higher) to avoid class file / API mismatches with current AndroidX and Media3 dependencies.

2. **`MediastreamPlayerCallback`** — New methods were added over the 9.x line (e.g. `playerViewReady`, `onPlayerReload`, full Cast session callbacks, `onDismissButton`, etc.). Only **`nextEpisodeIncoming`** and **`nextEpisodeLoadRequested`** have empty default implementations in Kotlin (optional overrides). **All other methods must still be implemented** in Kotlin. **`onFullscreen`** takes **`enteredForPip: Boolean`** (including for Java: `onFullscreen(boolean)`). If you upgrade from an older callback class, **add the missing overrides** or your project will fail to compile.

3. **Edge-to-edge / window insets (Android 15+)** — Padding for system bars can be applied by the SDK when **`appHandlesWindowInsets = false`** on `MediastreamPlayerConfig`. The default is **`true`**, meaning **your activity is expected to handle insets** if you use edge-to-edge. If the player draws under the status bar after upgrading, set `appHandlesWindowInsets = false` on the config **or** apply insets in your own layout.

4. **Platform-driven Reels** — When the embed API returns a Reels-type player skin, the SDK may **hand off the UI to the Reels flow** instead of the standard `PlayerView` path. Apps that share one screen for all formats should account for this (callbacks and layout may differ).

5. **Next-episode and ads** — New flows (preview overlay, manual `nextEpisodeId` + `updateNextEpisode`, preroll/autoplay ordering) can change when **`onEnd`**, **`nextEpisodeIncoming`**, and ad-related callbacks fire compared to older builds. Review any logic that assumed a single `onEnd` at content finish.

6. **Documentation correction** — The config field is **`accountID`**, not `account`. Older snippets were wrong; the API did not rename a property—only the docs were inaccurate.

If your project already used Java 17, compileSdk 35, and a complete callback implementation, you may only need to bump the dependency to the latest **10.0.x** and run regression tests (ads, DVR, local `src`, metadata, and audio background in fullscreen).

7. **NPAW/Youbora v7.3 (10.0.4)** — The SDK replaced the Youbora v6 library with the **NPAW Plugin v7.3** dependency. If your project explicitly added the old `https://npaw.jfrog.io/artifactory/youbora/` Maven repository, **update it** to `https://artifact.plugin.npaw.com/artifactory/plugins/android`. Apps that did not manually pin the Youbora repo (most projects using the AAR from Maven Central) are not affected.

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
Two repositories beyond `google()` and `mavenCentral()` are needed: **NPAW/Youbora** (the analytics
library, upgraded to v7.3 in **10.0.4**) and **EaseLive** (a mandatory dependency of the SDK from
**11.1.0**, whether or not you use PlayAnywhere). Add both to your `settings.gradle` /
`settings.gradle.kts`:
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
        maven { url = uri("https://artifact.plugin.npaw.com/artifactory/plugins/android") }
        maven { url = uri("https://sdk.easelive.tv/maven") }
    }
}

rootProject.name = "MediastreamAndroidTVSample"
include(":app")
```

> **Upgrading to 11.1.0 or later?** The `https://sdk.easelive.tv/maven` entry is **required** even if your app never enables PlayAnywhere: from 11.1.0 `tv.easelive:easelivesdk` is a dependency of the SDK itself, and it is not published on Maven Central. Without the repository the build fails at dependency resolution.

> **Migrating from 10.0.3 or earlier?** Replace the old Youbora repository URL (`https://npaw.jfrog.io/artifactory/youbora/`) with the one above. The `jcenter()` entry is no longer needed for the SDK's analytics dependency.

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

- **`environment` (`Environment`):** `DEV`, `PRODUCTION` or `EU` API/embed host. Default: `PRODUCTION`.
- **`isEurope` (Boolean):** Routes API and CDN calls to the **European** zone. Default `false`. When `true` and `environment` is `PRODUCTION`, the effective environment resolves to `EU` (`https://eu.mdstrm.com`); an explicit `DEV` is left alone.
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
- **`noAds` (Boolean, from 11.0.1):** Suppresses **client-side** ads (CSAI) — both `adURL` and the ad map returned by the API. It does **not** affect DAI/SSAI, which is stitched server-side and keeps playing. Default `false`. The policy propagates to next/previous episode configs, because it applies to the whole playlist and not only to the content it was set on.
- **`muteAds` (`FlagStatus`):** Mute ads; `NONE` follows platform.
- **`mute`:** Not a top-level property in current config; use **`volume`** or platform settings.
- **`adPreloadTimeoutMs` (Long?):** IMA ad preload timeout (ms).
- **`vastLoadTimeoutMs` (Int?):** VAST load timeout (ms).
- **`googleImaPpid` (String):** Google IMA PPID.
- **`adTagParametersForDAI` (`MutableMap<Util.AdTagParameter, String>`):** Google DAI **setAdTagParameters** map (PPID, `cust_params`, etc.).
- **`addAdCustomAttribute(key, value)`:** Adds `custom.<key>` parameters for CSAI VAST URLs (requires matching `custom.*` placeholders in the tag).
- **`fetchDeviceIdsAsync` / `waitForDeviceIdsCache`:** Cache GAID (or Amazon AAID on Fire TV) for `rdid` / `is_lat` in **getAdQueryString** and DAI fallbacks.
- **`ensureDAITagParamsFallback(platform)`:** Fills missing PPID/RDID/IDTYPE/IS_LAT for DAI from SDK cache.
- **`dualRenderSupported` (`Boolean?`, from 11.3.0):** Declares whether the device can sustain two simultaneous video pipelines. **Leave it unset** — the default is already correct. Unlike the other Mediastream SDKs, Android resolves that default **at runtime** from the device's UI mode: handhelds report `dual_render=1`, Android TV / Fire TV report `0`. The scarce resources live on the TV side — a single hardware decoder, a single secure decoder for Widevine L1, one video plane — and claiming a capability the device lacks breaks playback, while withholding it only keeps today's behaviour. The SDK sends the capability on **both** the content configuration request and the playback URL, because Mediastream's streaming service evaluates it again when it serves the manifest; it reads that signal to decide between server-guided ad insertion (SGAI) and classic Google DAI. Set it explicitly only to force the reported value, which is primarily useful in QA. **Cast sessions always report `0`** whatever you set here: the receiver does the rendering, so the local device's answer does not describe it.

> **SGAI is not enabled in production on the server side yet**, so setting `dualRenderSupported` does not change playback behaviour today — it only changes the capability the SDK reports.

## **Next / previous episode**

- **`loadNextAutomatically` (Boolean):** Auto-advance to next episode when allowed.
- **`nextEpisodeId` (String?):** Manual next id (app-driven flow; triggers confirmation callbacks).
- **`nextEpisodeTime` (Int?):** Seconds before end to show preview / callbacks (default applied by SDK if missing).
- **`nextPrevAutomatically` (Boolean):** Related next/prev behavior flags.
- **`showDismissButton` (Boolean):** Dismiss control visibility.

## **UI & device**

- **`showControls` (Boolean):** Show built-in controls. Default `true`. Set `false` for fully custom UI (still use DVR APIs if needed).
- **`showFullScreenButton` (Boolean):** Show fullscreen button where applicable.
- **`onFullscreenOnClick` (`Consumer<MediastreamPlayer>?`):** When set, this consumer is called instead of the built-in `enterFullscreen()` when the fullscreen-on button is tapped. Intended for React Native bridges or custom integrations.
- **`onFullscreenOffClick` (`Consumer<MediastreamPlayer>?`):** When set, this consumer is called instead of the built-in `exitFullscreen()` when the fullscreen-off button is tapped. Intended for React Native bridges or custom integrations where a synchronous consumer avoids callback-delay issues.
- **`showBrightnessBar` (Boolean):** Brightness slider in fullscreen (not for `AUDIO` player type).
- **`initialHideController` (Boolean):** Start with controller hidden, then show after a short delay (when controls are enabled).
- **`enablePlayerZoom` (Boolean):** Pinch-to-zoom on the video surface (when not disabled).
- **`adaptResizeModeToOrientation` (Boolean):** Adjust resize mode on rotation. Default `true`.
- **`appHandlesWindowInsets` (Boolean):** If `true`, the SDK **does not** apply system-bar padding on the player container (your app handles edge-to-edge). Default `true` in code — set to `false` to let the SDK pad for API 35+ edge-to-edge.
- **`applyEdgeSafeMargins` (Boolean):** Extra safe margins for dismiss/cast on edge displays.
- **`customPlayerView` (`PlayerView?`):** Inject your own `PlayerView` layout.
- **`language` (`Language`):** `ENGLISH` (`en`), `SPANISH` (`es`, Latin American Spanish), `SPANISH_SPAIN` (`es-ES`, "Español (España)", from 11.4.0), `PORTUGUESE` (`pt`) — localized strings for the player UI (live indicator, settings, track and subtitle menus). Default `ENGLISH`; it does not follow the device locale.
- **`baseColor` (Int):** Accent color (`-1` = use platform/API).
- **`showSubtitles` / `speedInControlBar` / `pauseOnScreenClick` / `pip` (`FlagStatus`):** Override platform for subtitles button, speed menu, tap-to-pause, PiP.
- **`pipExpandToFullscreenFirst` (Boolean):** Enter fullscreen briefly before PiP so PiP crops only the video.
- **`pipReplaceActivityContentWithPlayer` (Boolean):** Replace Activity content with the player before PiP (requires `Activity` context).
- **`customBackgroundForAudioPlayer` (String?):** Background image URL for **audio** content when you want a custom still instead of poster-only.
- **`forceBackPressedWhenFullScreen` (Boolean):** Forward back press after closing fullscreen.
- **`showPlaybackErrorsOnScreen` (Boolean):** Map ExoPlayer errors to on-screen messages.
- **`isDebug` (Boolean):** Verbose SDK logging.

## **PlayAnywhere (platform-driven, from 11.1.0)**

There is no configuration flag for PlayAnywhere on `MediastreamPlayerConfig`: the overlay turns on
only when the platform returns a `playAnywhere` block in the content's config, and `alwaysVisible`
is an account setting. What your app *does* have is the API to drive it —
`togglePlayAnywhere()`, `isPlayAnywhereActive()`, `onBackPressed()` — and two listeners on
`MediastreamPlayer`:

- **`playAnywhereStatusListener: ((status: String) -> Unit)?`** — EaseLive's app status.
- **`playAnywhereAdBlockedListener: ((blocked: Boolean) -> Unit)?`** — whether the overlay is
  currently hidden because an ad break is running (CSAI or DAI/SSAI).

It applies to **Live** content and requires the EaseLive Maven repository in your
`settings.gradle` — see [Settings Gradle](#settings-gradle).

## **Reels (platform-driven)**

- **`maxAllowedReelsTags` (Int?):** Max tags shown per reel item (default `10` in config).

## **Vertical player (platform-driven, from 11.0.0)**

The vertical / short-form experience is documented separately in the SDK repository:
[`VERTICAL_PLAYER_GUIDE.md`](https://github.com/mediastream/MediastreamPlatformSDKAndroid/blob/master/VERTICAL_PLAYER_GUIDE.md).
Its documented limitations are worth reading before you plan a screen around it: **no DRM, no
Picture-in-Picture and no Chromecast** in vertical mode. A config carrying `drmData` logs the
limitation and reports it through `onError` rather than showing a black screen.

## **Cast, notifications, analytics**

- **`castAvailable` (Boolean):** Enable Cast integration.
- **`playerId` (String):** Player ID from platform for skin, ads, logos, etc.
- **`appName` / `appVersion`:** Sent in analytics and stream URLs.
- **`konodracChannel` (String?):** Optional channel override for Konodrac analytics. Falls back to `appName`, then `"mdstrm-android-player"`. Only used when Konodrac is enabled via platform config.
- **`customerID` / `distributorId` / `maxProfile`:** Business and quality parameters.
- **`profileID` (String?):** Optional viewer / subscriber profile identifier forwarded to platform analytics.
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

**Vertical player (from 11.0.0)**

- **`onSwipeToItem(currentId: String)`** — The viewer swiped to another item; carries the item now on screen.
- **`onEndReached()`** — The end of the vertical playlist was reached.
- **`onLockedEpisode(id: String)`** — The viewer reached an episode they are not entitled to.
- **`onVerticalShare(id: String)`** — The share control was used on an item.
- **`onVerticalLike(id: String, isLiked: Boolean, episodeId: String? = null)`** — The like control was used; `isLiked` is the resulting state.

**Other**

- **`onConfigChange(MediastreamMiniPlayerConfig?)`** — Mini-player metadata (title, artwork, next/prev availability).
- **`onLiveAudioCurrentSongChanged(JSONObject?)`** — Live audio metadata (API + ID3 where enabled).
- **`onNext()` / `onPrevious()`** — Legacy hooks for next/previous actions from the host.

**Removed in 11.0.0**

- **`onEpisodeInfoClick`** (both overloads) is gone. The episode-counter badge that fired it was
  deleted in the 11.0.0 UI pass, so the callback had no call site left in the SDK and a host
  implementing it never received an event. Remove the override.

Default empty implementations exist for optional methods in Kotlin — `nextEpisodeIncoming`,
`nextEpisodeLoadRequested` and the five vertical-player methods above. **In Java, implement every
method:** the module is not compiled with `-Xjvm-default=all`, so Kotlin's default bodies live in
`DefaultImpls` and a Java implementor does not inherit them.

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
- **`isReleased(): Boolean`** (from 11.0.1) — Whether this instance is a shell after `releasePlayer()`. Useful for bridges that can outlive the player.
- **`removePlayerCallback(callback: MediastreamPlayerCallback?)`** (from 11.0.1) — Unregister a callback; for clients that die before the player does.

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

## PlayAnywhere (Live, from 11.1.0)

- **`togglePlayAnywhere()`** — Show or hide the overlay from your own UI. The SDK also renders a toggle inside the control bar (`playanywhere_btn`).
- **`isPlayAnywhereActive(): Boolean`** — Whether the overlay is currently on screen.
- **`onBackPressed(): Boolean`** — Give the overlay first refusal on the back gesture; returns `true` when it consumed the event. Only needed if you want to replicate the SDK's behaviour from your own `OnBackPressedCallback`.

## Cast & Chrome

- **`showChromeCastDialog()`** — Opens Cast device picker.
- **`SendCurrentItemToCast()`** — Reload current item on Cast.

## Introspection

- **`getVersion()`** — SDK version string (e.g. `"11.5.0"`).
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

> **`reinitializePlayerIfStale()` (from 11.0.1)** — recreates the service's player when the one it
> holds no longer matches the staged config. `onCreate()` is the only place that builds a new
> player, and it only runs if the service is (re)created — so when another client keeps the binding
> alive (Android Auto does exactly that), `STOP_SERVICE` does not destroy it and a freshly bound
> view would adopt the previous, already released player. **Main thread only** (`@MainThread`): it
> builds views and reassigns the session's player. Off-main calls are re-posted, and a failure
> reaches your app through `onError` instead of staying in logcat.
>
> **Android Auto also publishes `AndroidAutoContentChangedEvent`** (sticky, on EventBus) when the
> car picks new content, so your UI can follow what is playing. Subscribe with `sticky = true` if
> you need the last selection when registering late. It is cleared when the service is destroyed.

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

## [Version 11.5.0] - 2026-09-24
Screen reader support, a live latency fix and Android TV seek steps. No public API changes; minor because screen reader support is a new capability.

### Features
- **Screen reader (TalkBack) support in the main, vertical/microdramas and reels players.** Localized names on icon buttons (following the configured player language), double-tap to reveal the controls with the cursor landing on play/pause, auto-hide suspended while the reader is on the player chrome, item text in vertical/reels exposed as its own reading stops, phantom stops removed, and selected state exposed on the TV subtitle/audio options. A host `AccessibilityDelegate` on `customPlayerView` is now restored on release.

### Fixes
- **Live latency grew continuously during the session.** Setting the start position to 0 on a live stream made Media3 target a live offset at the start of the sliding window, and speed control drifted toward it at 0.9x for the whole session. Live no longer sets a start position; measured drift went from +10 % to converging within about 1 s of the target.
- **Low-latency events now honour the manifest's `PART-HOLD-BACK`.** With the Mediastream API's low-latency flag set, the SDK no longer forces its fixed live target or minimum offset. Without the flag, the offsets are exactly what they were.
- **Android TV D-pad seek on the progress bar.** A held key now moves 10 s, 30 s after 2 s, and 60 s after 5 s, instead of `duration / 20` per repeat. TV only.

## [Version 11.4.0] - 2026-09-23
New UI language. The public API only gains one enum value, so this is a minor.

### Features
- **`Language.SPANISH_SPAIN` (`es-ES`, "Español (España)").** Selected through `config.language` like the other languages, also on `reloadPlayer(newConfig)`. `SPANISH` (`es`) keeps the Latin American strings.

### Fixes
- **The SDK now builds the UI locale from the full language tag.** It used to create it with `Locale(code)`, which takes a tag like `es-ES` as a bare language and drops the region, so region-specific strings would never load. It now uses `Locale.forLanguageTag(code)`; `en`, `es` and `pt` resolve exactly as before.

## [Version 11.3.0] - 2026-09-21
Preparation for SGAI. The public API only gains one optional field, so this is a minor. **No behaviour change:** the streaming service gates on an explicit opt-in, so `0` and an absent parameter were already equivalent, and SGAI is not enabled in production on the server side yet — what changes today is only the capability the SDK reports.

### Features
- **`dualRenderSupported` (`Boolean?`): the SDK declares whether the device can sustain two simultaneous video pipelines.** The capability is sent as `dual_render` on **both** the content configuration request and the playback URL, because the streaming service evaluates it again when it serves the manifest; sending it on only one leaves the session inconsistent. The default is resolved **at runtime** from the device's UI mode — handhelds report `1`, Android TV / Fire TV report `0` — which makes Android the only Mediastream SDK where this value is not a constant. Leave the field unset unless you need to force a value in QA.
- **Cast URLs always report `dual_render=0`.** A cast session renders on the receiver, not on this device, so the local capability answer does not describe it; reporting it would claim, on no evidence, that the receiver sustains two pipelines.

## [Version 11.2.2] - 2026-09-02
### Fixes
- **Cast (VOD): MP4 fallback when the HLS rendition fails on the receiver.** The receiver reports the failure as an idle state with an error reason, and that was the end of it — the SDK loaded the HLS cast URL and never observed the remote state, so the cast screen hung with no visible error and no retry. **For VOD only**, the SDK now registers a remote-media callback and, on the first playback error, retries once with the MP4 rendition.
  - The MP4 URL comes from the source list the backend already published for that format, not from swapping the extension on the cast URL: its query string is signed for its own rendition, so rewriting it is not safe.
  - **One-shot per session**, so it does not loop if MP4 fails too. The latch resets when the session connects, when it ends, and on `SendCurrentItemToCast()`.
  - **Does not apply to Live**, which has no MP4 equivalent: there the callback is not registered at all, so that path is byte-for-byte what it was.
  - The `MediaInfo` content type is inferred from the effective URL, so the retry travels as `video/mp4`.

## [Version 11.2.1] - 2026-08-28
Validated in QA as `11.3.0-dev.4`. That build went out before the cycle was decided to be a patch rather than a minor, so **whoever tested `11.3.0-dev.4` was testing the contents of 11.2.1**.

### Fixes
- **PlayAnywhere: the control bar stopped auto-hiding and stayed pinned over the video.** The EaseLive overlay is a `WebView` mounted inside the player view's own overlay container, so it passed the SDK's "is this focus inside the player?" check. Touching it gives it real view focus — the `WebView`'s default behaviour, not keyboard or D-pad navigation — and the SDK read that as the user navigating the control bar: it cancelled the auto-hide timer and left it cancelled for as long as the overlay held focus, i.e. the whole PlayAnywhere session. Focus landing inside the overlay container is now excluded. **Mobile/tablet only** — that listener is not installed on TV.

## [Version 11.2.0] - 2026-08-26
### Fixes
- **Live video CSAI: content resumed behind the live edge after the pre-roll.** IMA plays the pre-roll in the same ExoPlayer while the live window keeps sliding, so when the ad ended the content was displaced — sometimes at a negative position — and the only recovery was a 1s timer meant for live audio, which arrived late and showed a visible jump. In **live video without DVR/DAI**, a one-shot catch-up now returns to the live edge when it detects the drift, hiding the surface until the seek settles. Does not apply to VOD, audio, DVR or SSAI.
  - **Does not apply to lives with Google DAI**, where the pre-roll runs in a separate ExoPlayer and the stream starts at the default position, so there is no drift to correct. Time jumps reported on DAI channels are pre-existing (they reproduce on 10.0.12) and come from the window reset on rebuffer, not from this path.
  - **Limitation on streams with no `#EXT-X-PROGRAM-DATE-TIME`:** without that tag the player never reports a live offset, so the catch-up has only the negative-position signal and does not cover "paused but still inside the window". Resolving that is a packaging change; no player-side live configuration replaces the missing anchor.
- **CSAI `ads.map`: wrong `ms_device` on Android TV / Fire TV.** The embed usually bakes in `ms_device=android` based on the media-info request's user agent, and the ad map uses that value — not `platformType` — to choose the Android TV custom params. The SDK now writes `ms_device` with the detected device (`android` / `androidtv` / `firetv`), replacing the embed's value.

## [Version 11.1.0] - 2026-08-21
PlayAnywhere (EaseLive) for **Live** content. Validated in QA as `11.1.0-qa202608182000`. The public API only gains methods, which is why this is a minor and not a `12.0.0` — but the mandatory `easelivesdk` dependency forces **every** consumer to add the EaseLive Maven repository to `settings.gradle` before their next build compiles.

The `keepScreenOn` fix shipped in **11.0.3** and is not part of this cycle.

### Features
- **PlayAnywhere (EaseLive)** — optional interactive overlay on top of the player for **Live** content, served by [EaseLive](https://easelive.tv). It only activates when the platform returns a `playAnywhere` block in the content's config; without it the SDK initialises nothing and behaves exactly as before.
  - New public API on `MediastreamPlayer`: `togglePlayAnywhere()`, `isPlayAnywhereActive()`, `onBackPressed()` (for hosts that want to replicate the same behaviour from their own `OnBackPressedCallback`), `playAnywhereStatusListener` and `playAnywhereAdBlockedListener`.
  - `alwaysVisible` mode is configured in the account, not by your app: the overlay stays visible with no toggle button. On TV the remote's back redirects D-pad focus to the native control bar instead of closing the overlay (it cannot be closed in this mode); a second back with no interaction in between leaves the screen normally.
  - The toggle lives inside the control bar (`playanywhere_btn`) on TV and on mobile — not as a separate floating button.
  - The overlay hides itself automatically during ad breaks, CSAI and DAI/SSAI alike.
  - **⚠️ New mandatory dependency for every consumer of the SDK**, whether or not you use PlayAnywhere: `tv.easelive:easelivesdk:2.16.0`. Add the EaseLive Maven repository to your `settings.gradle` / `settings.gradle.kts`:
    ```kotlin
    maven { url = uri("https://sdk.easelive.tv/maven") }
    ```
    Without it, dependency resolution fails on your next build regardless of whether your content uses PlayAnywhere. The `easelivesdk` manifest also merges the `ACCESS_NETWORK_STATE` permission and a `PermissionActivity` into your app. It does not raise the SDK's `minSdk`.

### Fixes
- **PlayAnywhere stayed invisible for the rest of the session after a DVR mode change during an ad break.** The internal "blocked by ad" flag was only cleared by an IMA unblock event, and `enableDvrMode()` / `returnToLiveMode()` replace the media item and re-prepare, discarding that pending event. On Live + DVR + PlayAnywhere the overlay and its button disappeared until the player was recreated, with nothing in the logs. The state is now reconciled against the player's real `isPlayingAd` on every ready state.

### Changed
- **⚠️ ITG is now also blocked during DAI/SSAI ad breaks.** Mapping the DAI ad-break events so PlayAnywhere would hide brought the ITG block along with them — until now ITG was only blocked in CSAI, because the DAI stream manager never emits the CSAI content-pause event. This is the intended behaviour (it makes DAI match what CSAI already did), but it **is a functional change for clients using ITG with DAI**: their interactive overlays now go inert during breaks. In CSAI the unblock moves a few milliseconds earlier; the end state is the same.

## [Version 11.0.3] - 2026-08-20
One theme: the device never let the screen sleep. Reported by Amazon on Fire TV / FireOS 14.

### Fixes
- **The screen never slept, not even with the player paused.** The 10.0.2 fix had added the right toggle in code, but Android computes the window flag as an OR across every attached view in the hierarchy: any child with `keepScreenOn="true"` fixed in XML wins over whatever the parent sets. Four SDK layouts had it hardcoded, so the toggle never had any effect. The worst case was the logo — once added to the player view it is never removed, so with the logo enabled the screen never slept at all, whatever playback was doing. Verified on a real Fire TV Stick and on mobile.
- **The screen could switch off during buffering, rebuffering or startup.** Removing the XML flag exposed a gap it had been masking: the toggle keyed off `isPlaying`, which is `false` while the player buffers, resolves the DRM licence or waits on the API. On a bad network, a live rebuffer or a slow start could exceed the device's screen timeout mid-session. It is now computed from `playWhenReady` + playback state, and covers both the main player and the SSAI pre-roll player.
- **The screen stayed on after releasing the player when the host uses `customPlayerView`.** `ExoPlayer.release()` does not emit a playing-state change, and the only reason the flag stopped applying was that the SDK detached its own view from the hierarchy — which integrators passing their own `PlayerView` never go through. `releasePlayer()` now resets the flag explicitly.
- **Reels kept the screen on indefinitely.** Its player view set `keepScreenOn = true` unconditionally when mounting an ad and never reset it, and because the pager recycles those views, seeing one ad was enough to keep the screen on for every following page, even paused.

## [Version 11.0.2] - 2026-08-11
Single-fix release: the one that missed the 11.0.1 cut.

### Fixes
- **CSAI ads failed on unsupported `MediaFile` formats.** IMA picked whatever `MediaFile` the VAST response listed first, including formats ExoPlayer cannot decode, so those creatives failed to render and the ad slot was wasted. All four client-side ad loaders now declare the formats the player actually supports, letting IMA pick a playable rendition: H.263, WebM, Ogg, MP4, DASH and HLS in both spellings. Applied to the two loaders in the main player (CSAI and the DAI pre-roll path) and the two in Reels, so both behave the same.
  - Validated in QA as `11.0.1-qa202608052147`. That build missed the 11.0.1 cut, so **whoever tested `11.0.1-qa202608052147` was testing the contents of 11.0.2**, not 11.0.1.

## [Version 11.0.1] - 2026-08-11
First release on top of 11.0.0. Consolidates Android Auto synchronisation with the host and state correction between contents, Picture-in-Picture auto-enter on API 31+, and a lint fix that had `./gradlew build` broken.

### Features
- **`MediastreamPlayerConfig.noAds`** — suppresses **client-side** ads (CSAI): both `adURL` and the ad map the API returns. Does not affect DAI/SSAI, which is stitched server-side and keeps playing. Default `false`, so existing hosts do not change behaviour. The policy propagates to next/previous episode configs, because it applies to the whole playlist and not only to the content where it was set.
- **`AndroidAutoContentChangedEvent`** — sticky EventBus event the SDK publishes when Android Auto selects new content, so your UI can sync with what the car is playing. Cleared when the service is destroyed; subscribe with `sticky = true` if you need the last selection when registering late.
- **`MediastreamPlayerServiceWithSync.reinitializePlayerIfStale()`** — recreates the service's player when the one it holds no longer matches the staged config. **Main thread only** (`@MainThread`); off-main calls are re-posted, and a failure reaches your app through `onError` instead of only logcat.
- **`MediastreamPlayer.isReleased()`** and **`removePlayerCallback()`** — to tell whether an instance is a shell after `releasePlayer()`, and to unregister a callback when your client dies before the player.

### Fixes
- **PiP did not trigger from the Recents button (API 31+).** `onUserLeaveHint()` is inconsistent across OEMs for the transition to Recents/Overview (confirmed on Samsung One UI), leaving audio playing with no PiP window. Auto-enter PiP is now armed once the player is prepared and PiP is enabled, so the system handles Home, Recents and gestures on API 31+. The hint is re-armed after rotation and after fullscreen transitions so it reflects the player's current bounds, and disarmed in `releasePlayer()`. Manual entry via `onUserLeaveHint()` is unchanged for API 26–30, and nothing is armed in vertical mode, where PiP is disabled.
- **`./gradlew build` failed on lint.** A ViewPager2 annotation combination makes lint reject any named constant for `offscreenPageLimit`. Suppressed at the statement level so the rule stays active everywhere else in the SDK.
- **Android Auto — 403 when switching content.** The URL is built from the SDK's config, so without copying `accessToken` and `type` from the incoming config, the previous content's token and type leaked into the new one. Access tokens are per content.
- **Android Auto — the episode lost its transport arrows.** Going live → episode, the episode inherited the live's next/prev state, which has neither. Transport state now always comes from the incoming content, even though display metadata for an adopted playback is still preserved.
- **Android Auto — auto-advance hung between episodes.** The service's startup path does not go through `preparePlayer()`, the only place that turns repeat off, so episodes looped instead of reporting the end of content.
- **Android Auto — the stream restarted from zero on re-bind.** A controller joining playback already in progress no longer re-prepares. The guard requires both a media item **and** a state other than idle/ended, because the media item survives an error and the end of content, and skipping prepare there left the stream dead.
- **Live audio notification with no usable artwork.** For audio lives the `preview_thumbnail` is a frame grab of a stream that has no frames; the station logo is now preferred when there is no poster. Video lives keep their previous poster behaviour unchanged.
- **The top-level `logo` node failed to deserialize** when it carried neither `enabled` nor `isSVG`. Those fields now have defaults.

### Improvements
- The registered callbacks list is now a `CopyOnWriteArrayList`. It is iterated in dozens of places while `addPlayerCallback` can mutate it, which was a latent `ConcurrentModificationException`.
- Notification and Android Auto metadata is now resolved from the config passed in when creating the media item, rather than the player's own config, so a caller loading different content does not drag the previous title and description along.

### Notes
- **Ads are disabled in Android Auto as a workaround**, not as policy: audio ad breaks are broken server-side and content chosen in the car has no host config to inherit from. It will be reverted when the backend is fixed.

## [Version 11.0.0] - 2026-08-05
Consolidates the whole `11.0.0-alpha01` → `11.0.0-qa14` cycle — vertical player, Reels/ViewPager rework, skin loaders, the `vpmute` ad tag parameter — plus everything released on the 10.0.x line up to 10.0.15, notably the IMA SDK 3.39.0 pin that fixes the per-ad `WebView` memory leak.

### ⚠️ Breaking changes
- **Java host apps must implement five new callbacks.** `MediastreamPlayerCallback` gained `onSwipeToItem`, `onEndReached`, `onLockedEpisode`, `onVerticalShare` and `onVerticalLike`. They have Kotlin default bodies, but the module is not compiled with `-Xjvm-default=all`, so those defaults live in `DefaultImpls` and **Java implementors do not inherit them** — a Java class implementing this interface must provide all five to compile. Kotlin implementors are unaffected. (This was already true earlier in the 11.0.0 line; it is called out here because it had never been stated.)
- **`onEpisodeInfoClick` removed**, both overloads. The episode-counter badge that fired it was deleted in the qa04 UI pass, so the callback had no call site anywhere in the SDK — a host implementing it never received an event. If you need the badge back it has to be rebuilt and wired to a listener.

### Features
- **Vertical player** and the Reels/ViewPager rework, with its own guide in the SDK repository: [`VERTICAL_PLAYER_GUIDE.md`](https://github.com/mediastream/MediastreamPlatformSDKAndroid/blob/master/VERTICAL_PLAYER_GUIDE.md). **No DRM, no PiP and no Chromecast** in this mode — a config carrying `drmData` now logs the limitation and reports `onError` instead of failing inside ExoPlayer with an opaque licence error and a black screen. Reported rather than fatal on purpose: a host reusing one config across a DRM screen and a vertical screen keeps working for clear content.
- **`vpmute` ad tag parameter**, telling the ad server whether the ad starts muted.

### Fixes
- **Cast — Activity leak on every session.** A `RemoteMediaClient` listener whose six overrides were all empty held the player, and through it your Activity and context, for the life of the process if you left the app while still casting: the listener was only removed when the session ended. It is gone entirely — it did nothing — and the client reference is now cleared in `releasePlayer()` and on session end.
- **Vertical — the host Activity stayed locked to portrait after a show switch.** The handler is reused across shows and had already forced portrait, so from the second show onward it saved portrait as the "original" orientation and restored that on release. It is now captured only on the first activation.
- **Vertical — a confirmed like reverted on swipe-away and back.** The confirmed value is now written into the queue item instead of only the holder, which also fixes a slow like API answering after the user had already swiped.
- **Vertical / Reels — the active item position was corrupted by a failed prepend.** The "no active holder" sentinel was shifted like a real position and could not be restored on rollback, leaving the adapter treating position 0 as active.
- **`vpmute` was sent under the wrong name for wrapped GAM tags.** The direct-GAM check substring-matched the whole URL, so a Mediastream VMAP carrying a url-encoded GAM tag in a query parameter counted as "direct" and the parameter went out under the name the Mediastream ads endpoint does not read — silently dropped for exactly the wrapper setup it was added to support. The host is now matched against the URL authority only.
- **Vertical — the per-episode config was requested without auth**, so any auth-gated media-info failed. It now sends the same headers as the playlist request.
- **Vertical — a refresh could silently do nothing**, with no callback and no error, when it landed on a busy manager; on the activation path the loading overlay would then never hide.

### Improvements
- Vertical: pending runnables are cancelled on teardown, and the loading overlay is removed from the container instead of just hidden (one hidden overlay used to accumulate per show switch).
- Cast: an empty `castUrl` is now logged, so "nothing to cast" is distinguishable from a receiver-side failure.
- `VerticalPipController` is documented as inactive — nothing instantiates it — so it no longer reads as a shipped feature.

### Notes
- **Needs device QA, not covered by tests:** Chromecast with the rewritten media-info builder — VOD and LIVE, with and without subtitles. That change replaced shared logic for every casting integrator, and the subtitle tracks in particular were not verified by inspection.

## [Version 10.0.15]
### Fixes
- **WebView memory leak on every ad playback (IMA SDK).** Media3 1.9.0 pulls IMA 3.38.0, which leaks a `WebView` each time an ad plays. Because those WebViews live in a separate sandbox process, the leak never showed up in the app's own `meminfo` and went unnoticed. The SDK now pins IMA `3.39.0` explicitly, which fixes the leak when a manager is destroyed — the one whose symptom is `Attempted to send bridge message after cleanup` in logcat.
  - Measured on a Sony BRAVIA VU31 (Android TV, 2 GB RAM) over 5 identical cycles of playing an SSAI channel and going back, forcing GC before each reading: WebViews at cycle 5 dropped from 11 to 2, live Views from 1,314 to 592, and the `bridge message after cleanup` occurrences from 1,760 to 0.
  - No public API change for host apps.

## [Version 10.0.14]
### Improvements
- **Metadata authority tracking on reload.** `reloadPlayer()` now creates a fresh `MediastreamMiniPlayerConfig` with every field cleared (`songName`, `description`, `albumName`, `imageUrl`, `setStateNext`, `setStatePrev`, `color`), so each content session starts from a clean slate; `onConfigChange()` checks the "host-provided" flag before overwriting `songName` for VOD, and `UpdateNotificationEvent` explicitly marks metadata as host-provided.
  - **⚠️ Behaviour change:** host apps that relied on selective field persistence across `reloadPlayer()` — for example `imageUrl` carrying over without being re-injected — must now re-supply all fields via `onNewSourceAdded()` / `UpdateNotificationEvent` on each reload.

## [Version 10.0.13]
### Fixes
- **Artwork and metadata lag across content transitions (VOD, LIVE, LIVE+DVR, Audio).** Artwork and host-provided metadata were not displaying correctly when switching between content types and episodes. The release adds metadata authority tracking (distinguishing API-sourced from host-provided metadata so API callbacks stop overwriting yours), support for protocol-relative artwork URLs (`//domain.com/image.png`, normalised to `https://`), a metadata clear when the content **type** changes so nothing stale is retained as host-provided, selective preservation between VOD episodes, URL validation that rejects incomplete artwork URLs instead of clearing a valid image, and an earlier config update in `reloadPlayer()` that removes a race between callbacks when leaving live audio.
  - Backward compatible: an existing `onNewSourceAdded()` implementation keeps working, and can optionally inject metadata via `UpdateNotificationEvent`.

## [Version 10.0.12] - 2026-07-22
### Fixes
- **WCAG keyboard focus border on Android TV.** The keyboard-focus indicator added for WCAG compliance was also drawn on Android TV, where focus is already shown by the platform's own D-pad highlighting. It is now skipped entirely on TV devices.
- **Notification / Android Auto artwork crash.** The service parsed the mini-player artwork URL unconditionally, crashing when ad events arrived before `onConfigChange` had populated the config. The URL is now validated (null, non-HTTP schemes and empty hosts are rejected) with a fallback to the current media item's artwork.
- **Artwork fetch race condition.** An in-flight artwork fetch is cancelled before starting a new one, so stale artwork can no longer overwrite the correct image when tracks change quickly.
- **Artwork caching.** The last successfully fetched URL and bytes are cached, avoiding redundant fetches when the same artwork repeats across consecutive tracks.
- **`image_src` null crash in audio radio.** A missing key in the current-song payload no longer throws.

## [Version 10.0.11]
### Fixes
- **Comscore ANR.** Comscore analytics initialisation ran on the main thread, causing ANRs and freezes. All Comscore SDK calls now run on a background thread, and `android-analytics` was bumped from 6.12.0 to 6.13.0 to fix ANRs inside the Comscore library itself.

## [Version 10.0.10]
### Fixes
- **Google DAI stream initialisation failure.** When a DAI asset key was invalid or the stream was inactive, IMA's ad error was only logged and forwarded, leaving the player buffering forever with no recovery. The DAI ad-error listener now detects stream load failures and falls back to plain (non-DAI) content playback.

## [Version 10.0.9] - 2026-07-08
### Fixes
- **Next-episode overlay playback.** Video no longer pauses when the next-episode overlay appears; it keeps playing behind it, with repeat mode switched off so it does not loop back to the start. A guard prevents `playNext()` firing twice when a real end-of-content arrives after the overlay's own auto-transition timer already triggered it, and the overlay's pending timers and animations are cancelled if the video reaches its real end first.
- **Comscore app name consistency.** The app name reported to Comscore was read from the per-content measurement URL, so it could vary across media loads. Comscore's app-level configuration is now initialised once per process, with the app name resolved in priority order: explicit `MediastreamPlayerConfig.appName` → the app's real package label → a generic default.

## [Version 10.0.8-hotfix-01] - 2026-07-01
### Fixes
- **Android Auto / notification artwork reliability.** Artwork for the media notification and Android Auto is now fetched asynchronously, downscaled (max 512px) and cached per URL, falling back to the last known good artwork or the default poster when the fetch fails or no URL is present. Fixes a bug where an **empty** `image_src` was treated as present and produced a malformed artwork URL. Android Auto also refreshes the displayed artwork immediately when the head unit connects mid-playback, instead of only on the next metadata update.

## [Version 10.0.7 / 10.0.8]
### Features
- **Trick-play / thumbnail preview.** The seekbar shows frame thumbnails while scrubbing when the content has preview metadata. New helpers: `MediastreamTimeBar`, `ThumbnailPreviewLoader`.
- **EU / Europe CDN zone.** New `isEurope` flag on `MediastreamPlayerConfig` (with a matching `environment = EU`) that redirects API and CDN calls to the European zone.
- **Cast mini-player notification redirect.** Tapping the Cast notification during an active session navigates back to the player screen instead of opening a blank activity.

### Fixes
- **Thumbnail preview layout shift.** Player controls were permanently displaced upward whenever thumbnail previews were configured, because attaching the preview view reserves layout space. Controls now only shift while a thumbnail is actually visible.
- **Subtitle VTT MIME type detection.** VTT tracks served with no `Content-Type` header failed to load; the MIME type is now detected by file extension first.
- **VOD end-of-playback reliability.** `onEnd` and `nextEpisodeIncoming` not firing reliably, video restarting instead of stopping, and unintended looping in manual repeat mode.
- **Android Auto artwork fallback.** A default poster is shown when the content has no artwork URL, instead of a blank media card.

### Improvements
- **Keyboard / D-pad accessibility (WCAG 2.4.7).** The time bar and live indicator show a visible focus ring when navigated by keyboard or D-pad, with general D-pad focus improvements across the controls.
- **Audio repeat mode** now accounts for the audio media type, avoiding unintended looping.

## [Version 10.0.6] - 2026-05-21
### Features
- **Konodrac analytics:** New analytics partner integration activated via platform player config (`tracking.konodrac.enabled = true` + `dataset_id`). The SDK tracks play, pause, seek, fullscreen, mute, end, heartbeat (every 50 s), DVR/catchup mode transitions, and dispose events to `marker.konograma.com/track`. No code change required for most integrations.
- **`konodracChannel` (config):** New `konodracChannel: String?` on `MediastreamPlayerConfig` to override the Konodrac channel identifier. Falls back to `appName` if not set, then `"mdstrm-android-player"`.

## [Version 10.0.5] - 2026-05-21
### Features
- **`onFullscreenOnClick` (config):** New `Consumer<MediastreamPlayer>?` on `MediastreamPlayerConfig` — overrides the built-in `enterFullscreen()` for the fullscreen-on button tap. Complements the existing `onFullscreenOffClick`. Designed for React Native bridges and custom integrations.
- **TV remote debounce seek:** D-pad left/right on TV remotes now debounces seeks for VOD and Live+DVR streams. Prevents rapid-fire seek events on key-repeat, improving seeking accuracy on Android TV / Fire TV devices.

### Fixes
- **Notification next/previous buttons:** Re-enabled `NEXT_BUTTON` and `PREVIOUS_BUTTON` custom session commands in `MediastreamPlayerServiceWithSync` that were inadvertently disabled. Next-button auto-advance now requires both `loadNextAutomatically == true` and `msMiniPlayerConfig.setStateNext == true`.
- **Notification `onConfigChange`:** When `loadNextAutomatically` is `false`, `songName` and `imageUrl` are now updated in the mini-player config so the notification reflects the current playing content.
- **Notification actions stale on update:** `UpdateNotificationEvent` now correctly refreshes the notification action list after a config change.

## [Version 10.0.4] - 2026-04-30
### Features
- **Profile ID (`profileID`):** New `MediastreamPlayerConfig.profileID` field forwarded to platform analytics to identify viewer / subscriber profiles.
- **`onFullscreenOffClick` (config):** New `Consumer<MediastreamPlayer>?` on config — overrides the built-in `exitFullscreen()` for the fullscreen-off button tap. Designed for React Native bridges to avoid callback delays.
- **Google DAI — DASH support:** The SDK now reads `google_dai_assetKey_dash` from the platform config and automatically selects HLS or DASH IMA content type based on `videoFormat`.

### Fixes
- **Google DAI + DRM:** DRM (Widevine) is now correctly preserved for DAI/SSAI streams when entering DVR mode. `CustomMediaSourceFactory` receives DRM via a supplier so the license is not lost when the IMA MediaItem carries no `localConfiguration` DRM.
- **DVR seeking (TV):** Fixed an intermittent DVR seek failure on Android TV devices.

### Dependency upgrade
- **NPAW/Youbora v7.3:** Migrated from `com.nicepeopleatwork:media3-adapter:6.8.7` to `com.npaw.plugin:plugin:7.3.25` + `com.npaw.plugin:plugin-media3-exoplayer:1.9.0-7.3.25`. Ad-event quartiles, skip, click, pause/resume, and ad-break events are now forwarded to the NPAW SDK. Update your Maven repository URL if you pinned the old Youbora endpoint (see Settings Gradle section).

## [Version 10.0.3] - 2026-04-13
### Fixes and improvements
- **Volume:** Persist volume during the session when merging/reloading config.
- **Local source:** Skip unnecessary embed/network setup when `config.src` is set (local playback).
- **Ads + DVR:** Clean up **AdsLoader** when entering DVR mode.
- **Google DAI:** Fix infinite loading on some **VOD + Google DAI** combinations.
- **Preroll & audio UI:** End-of-preroll fixes; restore and improve **background image** dimensions and **fullscreen** resize behavior.
- **Tracks:** Improved **audio track labels** in `TrackSelectionDialog`.
- **Metadata:** Avoid incorrectly overriding metadata from the content source (`CustomMediaSourceFactory` / player path).

## [Version 10.0.2] - 2026-03-24
### Fixes
- **Screen on while playing:** Set `msplayerView.keepScreenOn` from `onIsPlayingChanged` on main and preroll player listeners so the screen stays on consistently during content and ads.

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
- **compileSdk 35**, **minSdk 24**, **Java 17**; current coordinates: `io.github.mediastream:mediastreamplatformsdkandroid:10.0.6`.

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

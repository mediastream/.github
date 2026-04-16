# Welcome to the Mediastream Apple SDK

Hello, Apple Developer! 👋

Welcome to the Mediastream SDK for iOS and Apple TV, designed to streamline the integration of our powerful features into your applications. This SDK provides access to advanced Mediastream capabilities, allowing you to deliver exceptional multimedia experiences to your users.

## Version iOS
- **Version:** The current version of the SDK is 3.0.0.
- **Compatibility:** Compatible with Swift Version > 5.9

## Version Apple TV
- **Version:** The current version of the SDK is 0.3.6.
- **Compatibility:** Compatible with Swift Version > 5.9


## Adding Mediastream Platform SDK to Your iOS Project

To integrate the Mediastream Platform SDK into your iOS project, add the following dependency to your project's pod file:

```swift
pod 'MediastreamPlatformSDKxC', '~> 3.0.0'
```

## Adding Mediastream Platform SDK to Your Apple TV Project

To integrate the Mediastream Platform SDK into your tvOS project, add the following dependency to your project's pod file:

```swift
pod 'MediastreamPlatformSDKAppleTV', '~> 0.3.6'
```


### Basic Implementation

In this minimal setup, the SDK takes care of various intricate processes, leveraging the provided account ID, content ID, and content type to ensure a seamless experience. This simplicity enables you to focus on creating engaging applications without the need for extensive configurations.

First of all, don't forget to select "YES" in "Allow Arbitrary Loads", which is inside the "App Transport Security" property. Also, allow "Required Background mode" by selecting the option as in the image.

![Allow](/images/disable_app_transport_security_and_required_background_modes.png)

### View
```swift
import UIKit
import MediastreamPlatformSDK

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        let playerConfig = MediastreamPlayerConfig()
        let mdstrm = MediastreamPlatformSDK()

        playerConfig.accountID = "ACCOUNT_ID"
        playerConfig.id = "CONTENT_ID"
        playerConfig.type = MediastreamPlayerConfig.VideoTypes.VOD

        self.addChild(mdstrm)
        self.view.addSubview(mdstrm.view)
        mdstrm.didMove(toParent: self)

        mdstrm.view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            mdstrm.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            mdstrm.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            mdstrm.view.topAnchor.constraint(equalTo: view.topAnchor),
            mdstrm.view.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])

        mdstrm.setup(playerConfig)
        // `play()` is optional if `playerConfig.autoplay` is true (default in the SDK).
        mdstrm.play()
    }
}
```
# MediastreamPlayerConfig: Customizing Your Playback Experience

The `MediastreamPlayerConfig` class in the Mediastream iOS|Apple TV SDK provides a range of properties for tailoring and enhancing your playback experience. Here's an overview of the current properties:

## **Required Parameters:**

- **`id` (String):** Video, Audio, Live or Episode ID. You can get it from Mediastream Platform.
- **`accountID` (String):** Account ID. You can get it from Mediastream Platform.
- **`type` (MediastreamPlayerConfig.VideoTypes):** Video type. Possible values: VOD, LIVE, EPISODE. Tells the player what type of content is going to be played.

## **Optional Parameters:**

### Playback and content

- **`accessToken` (String):** Access token for restricted videos.
- **`autoplay` (Bool):** When `true`, the SDK starts playback once the asset is ready. **Default: `true`.** Set to `false` if you want to call `play()` yourself after `ready`.
- **`videoFormat` (MediastreamPlayerConfig.AudioVideoFormat):** Manifest or file type to prefer: **HLS**, **M4A** (mp4), **MP3**. Default: **HLS**.
- **`startAt` (Int):** Start position in **seconds** for VOD/episode (and related live flows where supported).
- **`src` (URL / NSURL):** Play a custom URL instead of loading metadata from the platform (`id`/`accountID` still apply for analytics in many setups).
- **`volume` (Int):** Initial volume 0–100. Values below 0 mean “unset” so a previous volume can be restored on `reloadPlayer` merges.
- **`environment` (MediastreamPlayerConfig.Environments):** **PRODUCTION** or **DEV** API base. Default: production.
- **`playerId` (String):** Use a player configuration stored in the Mediastream Platform (skin, behavior, tracking flags in JSON).
- **`customerID`**, **`appName`**, **`appVersion`**, **`distributorId`**, **`maxProfile`**, **`referer`**, **`protocoL`:** Analytics, auth, and request tuning (see SDK for query composition).
- **`tryToGetMetadataFromLiveWhenAudio` (Bool):** When live HLS carries `TIT2` / `TPE1` style metadata, the SDK maps it and may hit the `currentSong` API; fires `onLiveAudioCurrentSongChanged`. Default: **`true`**.

### UI and player chrome

- **`customUI` (Bool):** Use Mediastream custom controls overlay instead of system `AVPlayerViewController` controls. Default: **`false`** (native controls when `showControls` is true).
- **`showControls` (Bool):** For **native** UI (`customUI == false`), whether system playback controls are visible. Default: **`true`**.
- **`showTitle`**, **`showSubtitles`:** `ENABLE` / `DISABLE` / `NONE` (inherit from platform JSON when `NONE`).
- **`showDismissButton`**, **`showFullScreenButton`**, **`forceBackPressedWhenFullScreen`**, **`showBackgroundOnTitleAndControls`**, **`defaultOrientation`:** Custom UI layout and dismiss behavior.
- **`showCastButton`**, **`useCustomCastButton`:** Chromecast-style button; optional custom `UIButton` host.
- **`showReplayView` (Bool):** After VOD/episode ends, show in-player replay UI when enabled.
- **`language` (MediastreamPlayerConfig.Language):** SDK strings (e.g. LIVE, settings). **ENGLISH**, **SPANISH**, **PORTUGUESE**.
- **`enablePlayerZoom` (Bool):** Pinch zoom on video (custom UI only). Default: **`false`**.
- **`showBrightnessBar` (Bool):** Brightness slider in fullscreen (video custom UI). Default: **`true`**.
- **`customBackgroundForAudioPlayer` (String):** Image URL behind audio when using **custom UI** (replaces the default placeholder).

### DVR, live windows, and PiP

- **`dvr` (Bool):** Enable DVR-style behavior for live where the platform supports it. Default: **`false`**.
- **`windowDvr` (Int):** Caps the seekable DVR window versus the account default: values are in **minutes** and must not exceed the platform window (`0` = no cap). When `type == LIVE` and `dvr` is enabled, the same integer may be sent as the `dvrOffset` query fragment on media URLs—confirm the unit with your Mediastream integration if you rely on that parameter.
- **`dvrStart` / `dvrEnd` (String):** ISO-like UTC strings for fixed-window DVR / “DVR as VOD” style ranges when configured on the account.
- **`canStartPictureInPictureAutomaticallyFromInline` (Bool):** Matches `AVPlayerViewController` / PiP; when `true`, the SDK also tries to start PiP on background for custom UI. Default: **`true`**.

### Ads and DRM

- **`adURL` (String):** Override ad tag (VAST, etc.). Platform ads are used when unset unless `hasAds()` is used.
- **`googleImaPPID` (String):** Publisher-provided ID for IMA.
- **`googleImaLanguage` (String):** IMA UI language (default **en**).
- **`hasAds()`:** Call after setting `adURL` so the player treats ads as enabled from config alone.
- **`addAdCustomAttribute`(_ key:, value:):** Same as documented VAST `cust.*` replacement behavior (works when your ad URL is driven from config).
- **`adTagParametersForDAI` ([AdRequestParam: String]):** Google DAI ad-tag query parameters (`AdRequestParam` enum keys such as `ppid`, `rdid`, `cust_params`, …).
- **`ensureDAITagParamsFallbackForDAI(ppidFallback:)`:** Fills missing DAI tag params (ppid, idtype, rdid, is_lat) from config / device; the SDK calls this for DAI requests, but apps may call it when building custom flows.
- **`drmUrl`**, **`addDrmHeader`(_:, value:):** FairPlay / DRM asset licensing when applicable.
- **`appCertificateUrl`:** Related FairPlay certificate URL when required.

### Lock screen, CarPlay, notifications

- **`updatesNowPlayingInfoCenter` (Bool):** Publish now-playing metadata and remote commands. Default: **`false`**.
- **`notificationSongName`**, **`notificationDescription`**, **`notificationAlbumName`**, **`notificationImageUrl`:** Override artwork/title text for Now Playing.

### Episodes and reels

- **`loadNextAutomatically` (Bool):** Auto-advance episode when applicable. Default: **`false`**.
- **`nextEpisodeId`**, **`nextEpisodeTime`:** Manual next episode and UI countdown (seconds before end).
- **`maxAllowedReelsTags` (Int):** Reels feed tag limit. Default: **10**.

### Debugging and tracking

- **`debug` (Bool):** Verbose logging inside the SDK.
- **`trackEnable` (Bool):** Platform collector / tracking toggle.
- **`analyticsCustom` (String):** Custom analytics payload hook where supported.
- **`addYouboraExtraParams`(_ value:):** Extra Youbora dimensions when Youbora is enabled in the player JSON.

# Implementing event handling (`EventManager`)

The player exposes **`mdstrm.events`** (`EventManager`). Subscribe with `listenTo(eventName:action:)`; there is no separate `MediastreamPlayerCallback` protocol in current iOS builds. Example:

```swift
let mdstrm = MediastreamPlatformSDK()

mdstrm.events.listenTo(eventName: "play", action: {
    NSLog("Player is playing")
})

mdstrm.events.listenTo(eventName: "pause", action: {
    NSLog("Player is paused")
})

mdstrm.events.listenTo(eventName: "error", action: { (information: Any?) in
    if (information != nil) {
        if let info = information as? String {
            NSLog("ERROR: \(info)")
        }
    }
})

mdstrm.events.listenTo(eventName: "onDAIAdEvent", action: { (information: Any?) in
    if (information != nil) {
        print("onDAIAdEvent: \(information)")
    }
})
```
# Reminder

`MediastreamPlatformSDK` is a `UIViewController` that owns an **`AVPlayer`** exposed as **`mdstrm.player`**. You can use standard `AVPlayer` APIs (AirPlay route picker, external playback, etc.) on that instance when it is non-nil.

Example of Airplay Button on Custom UI or No Controls Enabled:

```swift
    func CreateAirPlayButton() {
        let airPlayButton = AVRoutePickerView(frame: CGRect(x: 20, y: 50, width: 40, height: 40))
        airPlayButton.activeTintColor = .blue
        airPlayButton.tintColor = .gray
        mainContainer.addSubview(airPlayButton)
    }
```

# Event Listening in Mediastream SDK

The Mediastream SDK allows you to listen to various events emitted by the player, providing valuable hooks into the playback lifecycle. Here are the available events:

1. **`finish:`**
   - Called when the current video has completed playback to the end of the video.

2. **`error:`**
   - Called when an error not related to playback occurs.

3. **`pause:`**
   - Called when the current video pauses playback.

4. **`play:`**
   - Called when the current item **starts or resumes** playback (the SDK avoids duplicate emissions in some ad, buffering, and casting paths).

5. **`ready:`**
   - Called when the asset is **ready to play** (first successful load for the current item, not “unpaused”).

6. **`onAdPlay:`**
    - Called when an Ad starts to play.

7. **`onAdPause:`**
    - Called when an Ad is paused.

8. **`onAdLoaded:`**
    - Called when an Ad is loaded.

9. **`onAdResume:`**
    - Called when an Ad is in resume mode.

10. **`onAdEnded:`**
    - Called when an Ad finishes.

11. **`onAdError:`**
    - Called when an Ad fails.

12. **`onAdSkipped:`**
    - Called when an Ad is skipped.

13. **`conectionStablished:`**
    - Called whenever the SDK is connected to internet.

14. **`conectionLost:`**
    - Called whenever the SDK lost internet connection.

15. **`seek:`**
    - Called when the user is seeking.
16. **`onDAIAdEvent`**
    - Called when DAI AD START, AD is IN PROGRESS or is COMPLETED.

17. **`pipStarted` / `pipStopped` / `pipRestoreFailed`**
    - Picture in Picture started, stopped, or failed to restore (for example after reload or when the system requires a user gesture).

18. **`nextEpisodeIncoming` / `nextEpisodeLoadRequested`**
    - Hooks for the next-episode countdown / user action vs. when the SDK is about to load the next episode.

19. **`newsourceadded`**
    - Emitted when `reloadPlayer` begins loading a new source on the existing player instance.

20. **`currentTimeUpdate` / `durationUpdated`**
    - Periodic time label updates for custom UI integrations.

These events allow you to respond dynamically to various states and actions during playback.

# Player Methods

The Mediastream player provides several methods that you can use to control playback and access various functionalities. Here is an overview of the main methods available:

## `play()`

Starts playback of the current content.

## `pause()`

Pauses playback of the current content.

## `fordward(_ time: Double)` / `backward(_ time: Double)`

Seek **forward** or **backward** by `time` in **seconds** (relative jump). Note: the public API name is `fordward` (typo preserved for binary compatibility).

## `seekTo(_ time: Double)`

Seeks to an **absolute** position in **seconds** from the start of the current item (or stores the value if the player is not ready yet).

## `startPiP()`

Launches picture in picture functionality if available on the device.

## `stopPiP()`

Stopped picture in picture functionality.
## `changeSpeed(_ speed: Float)`

Sets `AVPlayer.rate` for the current content.

## `reloadPlayer(_ config: MediastreamPlayerConfig)`

Loads new content, **reusing** the existing `AVPlayer` when possible (smoother episode changes and PiP). Persistent UI settings are merged from the previous session (see `mergePersistentFrom` in the SDK).

## `releasePlayer()`

Tears down observers, ads, PiP, and the player. Call when you remove the player from your UI hierarchy for good.

## Other useful APIs

- **`setCastingModeEnabled(_ enabled: Bool)`** / **`isCastingModeEnabled`:** Local player stays paused; `play()` / `pause()` / seek emit events for an external Cast implementation.
- **`skipAdAndResumeContent()`:** Skip the current IMA ad and resume main content (e.g. video ad on audio).
- **`playNext()`** / **`playPrev()`:** Jump to configured next/previous episode (`reloadPlayer` under the hood).
- **`updateNextEpisode(_ config:)`:** Supply the next episode `MediastreamPlayerConfig` when you handle `nextEpisodeIncoming` yourself.
- **`enterFullscreen(fullscreen: Bool)`:** Programmatic fullscreen for **custom UI** flows (also emits `onFullscreen` / `offFullscreen` unless suppressed internally).
- **`getCurrentTime() -> Int64`:** Playback position in **milliseconds** for analytics-style APIs.
- **`getCurrentPosition() -> Int`:** Position in **whole seconds** (floor).
- **`getDuration()`**, **`getLiveDuration()`**, **`isAudioContent()`**, **`isLocalFile()`:** Introspection helpers.



# Examples

In the following example, you'll find an application showcasing various uses of the Mediastream SDK for iOS. This app provides practical examples of key functionalities, including audio playback, video playback, audio as a service, casting, and more. Make sure you enter the IDs corresponding to your ACCOUNT_ID and CONTENT_ID and enjoy.

Remember do a `pod install` before run the example.

[Sample](/apple/Sample)

# Release Notes iOS
## [Versión 3.0.0] - 2026-04-15
### Features
- **Reload without tearing down the player:** `reloadPlayer(config:)` can reuse the same `AVPlayer` / `AVPlayerViewController` and replace only the current item, which keeps PiP and UI transitions smoother (especially for next episode).
- **Picture in Picture:** Large refactor for PiP with reload, optional automatic PiP when entering background when `canStartPictureInPictureAutomaticallyFromInline` is enabled (including custom UI), and restoring PiP after ads when it was active. New events: `pipStarted`, `pipStopped`, and `pipRestoreFailed` when programmatic PiP cannot start.
- **Playback events:** More consistent `play` / `pause` emission (e.g. after ads, casting, and rate changes) to avoid duplicates.
- **LIVE playback:** Smarter live-edge handling (`resumeLivePlayback`) to reduce repeated seeks and stuttering on linear live streams.
- **Episodes / UI:** `nextEpisodeLoadRequested` event; next-episode controls and notification center behavior refined; optional custom background image for audio with custom UI; optional in-player zoom (`enablePlayerZoom`).
- **Ads & analytics:** Improvements around preroll/overlay, autoplay with ads, DAI stream lifecycle; Comscore support when enabled in platform config; `skipAdAndResumeContent()` for cases such as video ads on audio content.
- **Reels:** Reels-style playback path and related controls/ads behavior.
- **Subtitles:** Inclusive / external subtitle handling fixes.
- **Casting:** `setCastingModeEnabled(_:)` / `isCastingModeEnabled` so local play/pause can forward events only while Cast drives playback.
### Bug Fixes
- VOD / EPISODE end-of-playback: avoid unwanted resume loops after finish (explicit pause, seek to start, `isOnEnd` handling) and finish when seeking near the end of the asset.
- Fullscreen events suppressed correctly on DVR → live (DAI) transitions where the player is rebuilt without leaving fullscreen custom UI.
- `getCurrentPosition` / volume edge cases that could crash in some configurations.
- “Go to live” / DVR window seek and slider behavior; autoplay and controls after reload; audio quality selector label; IMA “no ads” / failed ad tag paths starting content correctly.

## [Versión 2.3.0] - 2025-02-18
### Features
- Update Google IMA SDK to 3.24.0 version
### Bug Fixes
- NSRange Exception when move faster on timeline

# Release Notes AppleTV
## [Versión 0.3.4] - 2025-02-12
### Features
- Google DAI replace ad tag parameters option.

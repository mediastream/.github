# Welcome to the Mediastream Apple SDK

Hello, Apple Developer! 👋

Welcome to the Mediastream SDK for iOS and Apple TV, designed to streamline the integration of our powerful features into your applications. This SDK provides access to advanced Mediastream capabilities, allowing you to deliver exceptional multimedia experiences to your users.

> ### ⚠️ CocoaPods is being deprecated — both SDKs move to Swift Package Manager
>
> **CocoaPods trunk becomes read-only on December 2, 2026.** After that date no new
> versions can be published there by anyone. Both Apple SDKs are therefore distributed
> through **Swift Package Manager**, and CocoaPods no longer receives new versions.
>
> | | Last CocoaPods version | New Swift Package |
> |---|---|---|
> | iOS | `MediastreamPlatformSDKxC` **5.0.1** | [MediastreamPlatformSDKiOS-spm](https://github.com/mediastream/MediastreamPlatformSDKiOS-spm) |
> | Apple TV | `MediastreamPlatformSDKAppleTV` **2.0.0-qa.02** | [MediastreamPlatformSDKAppleTV-spm](https://github.com/mediastream/MediastreamPlatformSDKAppleTV-spm) |
>
> Versions already published to CocoaPods stay installable, but **they will not receive
> fixes**. Migrating requires **no code changes**: the public API and the `import` are the
> same. See [Migrating from CocoaPods](#migrating-from-cocoapods) below.

## Version iOS
- **Version:** 6.0.0, distributed through Swift Package Manager.
- **Requirements:** **iOS 13.0** or later, **Xcode 16** or later, Swift 5.9 or later.
- **Note:** 6.0.0 raises the deployment floor from iOS 12 to iOS 13 and needs Xcode 16 to
  resolve the package. Both come from EaseLive, the dependency behind PlayAnywhere. Xcode 16 is
  a requirement for your build machine, not for your users' devices. Upgrading needs **no code
  changes**; apps that must keep supporting iOS 12 have to stay on `5.2.0`.

## Version Apple TV
- **Version:** 2.1.0, distributed through Swift Package Manager.
- **Requirements:** **tvOS 15.0** or later, Xcode 15 or later, Swift 5.9 or later.
- **Note:** The deployment floor moved from tvOS 14 to tvOS 15. This is not a preference —
  Google's IMA SDK for tvOS requires it from version 4.16.0 onward. Apps that must keep
  supporting tvOS 14 have to stay on the CocoaPods version.

## Adding Mediastream Platform SDK to Your iOS Project

In Xcode, choose **File → Add Package Dependencies…** and paste:

```
https://github.com/mediastream/MediastreamPlatformSDKiOS-spm.git
```

Pick **Up to Next Major Version** from `6.0.0` and add the `MediastreamPlatformSDKiOS`
product to your app target. Or, in a `Package.swift`:

```swift
dependencies: [
  .package(
    url: "https://github.com/mediastream/MediastreamPlatformSDKiOS-spm.git",
    from: "6.0.0"
  )
]
```

Then:

```swift
import MediastreamPlatformSDKiOS
```

Full installation guide, dependency version ranges and per-release compatibility table:
<https://github.com/mediastream/MediastreamPlatformSDKiOS-spm#readme>

**If your app uses Chromecast**, note that Google does not publish a Swift Package for the
Cast SDK. Keep `google-cast-sdk` on CocoaPods — both dependency managers coexist in the same
project — or add the Cast `.xcframework` manually. Details in
[CAST_INTEGRATION.md](https://github.com/mediastream/MediastreamPlatformSDKiOS-spm/blob/master/CAST_INTEGRATION.md).

## Adding Mediastream Platform SDK to Your Apple TV Project

In Xcode, choose **File → Add Package Dependencies…** and paste:

```
https://github.com/mediastream/MediastreamPlatformSDKAppleTV-spm.git
```

Pick **Up to Next Major Version** from `2.1.0` and add the
`MediastreamPlatformSDKAppleTV` product to your app target. Or, in a `Package.swift`:

```swift
dependencies: [
  .package(
    url: "https://github.com/mediastream/MediastreamPlatformSDKAppleTV-spm.git",
    from: "2.1.0"
  )
]
```

Then:

```swift
import MediastreamPlatformSDKAppleTV
```

Full installation guide and per-release compatibility table:
<https://github.com/mediastream/MediastreamPlatformSDKAppleTV-spm#readme>

Chromecast does not apply on tvOS: an Apple TV is a Cast *receiver*, not a sender, and
Google's sender SDK is not published for tvOS.

## Migrating from CocoaPods

The same three steps on both platforms, and **no code changes**:

1. Remove the pod from your `Podfile` — `MediastreamPlatformSDKxC` on iOS,
   `MediastreamPlatformSDKAppleTV` on Apple TV — and run `pod install`.
2. Add the Swift Package as shown above.
3. Build. The `import` and the public API are unchanged.

If your project has no other pods left, you can delete the `Podfile` and the
`.xcworkspace` and go back to opening the `.xcodeproj` directly.

Two things improve on the way out, and both were imposed on your app by the pod:

- **Apple TV: the tvOS simulator works on Apple Silicon again.** The published podspec set
  `EXCLUDED_ARCHS[sdk=tvossimulator*] = arm64` on *your* target, not just its own, which
  left anyone on an M-series Mac unable to run a tvOS simulator. The Swift Package ships an
  `arm64` simulator slice and excludes nothing.
- **Your build no longer needs CocoaPods to resolve our SDK**, which removes `pod install`
  from CI for projects that only used it for this dependency.

**If your build environment restricts outbound network access**, allow `bitbucket.org`:
NPAW distributes YouboraLib from there and it is a transitive dependency. If you filter
outbound traffic, also allow `a-fds.youborafds01.com`, which is where Youbora moved its
configuration endpoint. On iOS, from 6.0.0, add `github.com/ease-live` and `sdk.easelive.tv`
as well — EaseLive's package and its runtime.


### Basic Implementation

In this minimal setup, the SDK takes care of various intricate processes, leveraging the provided account ID, content ID, and content type to ensure a seamless experience. This simplicity enables you to focus on creating engaging applications without the need for extensive configurations.

First of all, don't forget to select "YES" in "Allow Arbitrary Loads", which is inside the "App Transport Security" property. Also, allow "Required Background mode" by selecting the option as in the image.

![Allow](/images/disable_app_transport_security_and_required_background_modes.png)

### View
```swift
import UIKit
import MediastreamPlatformSDKiOS

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
- **`showAirplayButton` (Bool):** Show a native `AVRoutePickerView` (AirPlay button) in the custom UI controls bar. Default: **`false`**. Only applies when `customUI = true`.
- **`useCustomAirplayButton` (UIButton?):** Supply your own button to host the AirPlay route picker overlay when using custom UI. When set, the SDK attaches the `AVRoutePickerView` to your button's frame instead of the built-in control.
- **`showReplayView` (Bool):** After VOD/episode ends, show in-player replay UI when enabled.
- **`language` (MediastreamPlayerConfig.Language):** SDK strings (e.g. LIVE, settings). **ENGLISH**, **SPANISH**, **PORTUGUESE**.
- **`enablePlayerZoom` (Bool):** Pinch zoom on video (custom UI only). Default: **`false`**.
- **`showBrightnessBar` (Bool):** Brightness slider in fullscreen (video custom UI). Default: **`true`**.
- **`customBackgroundForAudioPlayer` (String):** Image URL behind audio when using **custom UI** (replaces the default placeholder).

### PlayAnywhere (interactivity overlay) — iOS, from 6.0.0

- **`playAnywhere` (PlayAnywhereConfig?):** Turns on the EaseLive interactivity overlay. Built
  with `PlayAnywhereConfig(accountId:programId:projectId:env:alwaysVisible:)`: `accountId` and
  `programId` are required, `projectId` and `env` select a non-default EaseLive project or
  environment, and `alwaysVisible` keeps the overlay up instead of making the viewer toggle it.
  Default **`nil`**, and then nothing is built — no overlay, no observers, no change in
  behaviour. When you leave it unset, the SDK still picks the configuration up from the content
  if the platform provides one.
- **Requires `customUI = true`.** With the native `AVPlayerViewController` controls the
  configuration is ignored and a line is logged: the overlay covers the whole player and is
  interactive, so it would swallow the system controls' touches, and the toggle button is part
  of the custom UI.
- Companion APIs: **`togglePlayAnywhere()`** shows or hides the overlay (a no-op under
  `alwaysVisible`), **`isPlayAnywhereActive()`** reports whether it is on screen right now, and
  the **`playAnywhereButton`** outlet on `MediastreamCustomUIView` lets you restyle or hide the
  built-in button.

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

21. **`externalPlaybackActiveChanged`**
    - Fired when AirPlay external playback state changes (video handoff via `AVPlayer.isExternalPlaybackActive` or system audio route switches to/from AirPlay). The `information` payload is a dictionary: `["external_playback_active": Bool]`. Emissions are coalesced to avoid redundant callbacks during AirPlay teardown.

22. **`playAnywhereStatusChanged`** (iOS, from 6.0.0)
    - PlayAnywhere's status changed. The `information` payload is `["status": String]`:
      `enabled` while EaseLive's web app is interactive, `disabled` when it is not, and
      `hidden` when the SDK is holding the overlay down for an ad break.

23. **`playAnywhereError`** (iOS, from 6.0.0)
    - A PlayAnywhere error, with payload `["message": String, "fatal": Bool]`. A fatal error
      releases the overlay and reports `disabled` rather than taking playback down with it.

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

- **`isPlayerExternalPlaybackActive: Bool`** (read-only): `true` when AirPlay video (`AVPlayer.isExternalPlaybackActive`) **or** the system audio route is AirPlay. Useful to check state before the player is initialized (e.g. audio re-entering while the TV is still paired).
- **`setCastingModeEnabled(_ enabled: Bool)`** / **`isCastingModeEnabled`:** Local player stays paused; `play()` / `pause()` / seek emit events for an external Cast implementation.
- **`skipAdAndResumeContent()`:** Skip the current IMA ad and resume main content (e.g. video ad on audio).
- **`playNext()`** / **`playPrev()`:** Jump to configured next/previous episode (`reloadPlayer` under the hood).
- **`updateNextEpisode(_ config:)`:** Supply the next episode `MediastreamPlayerConfig` when you handle `nextEpisodeIncoming` yourself.
- **`enterFullscreen(fullscreen: Bool)`:** Programmatic fullscreen for **custom UI** flows (also emits `onFullscreen` / `offFullscreen` unless suppressed internally).
- **`getCurrentTime() -> Int64`:** Playback position in **milliseconds** for analytics-style APIs.
- **`getCurrentPosition() -> Int`:** Position in **whole seconds** (floor).
- **`getDuration()`**, **`getLiveDuration()`**, **`isAudioContent()`**, **`isLocalFile()`:** Introspection helpers.
- **`togglePlayAnywhere()`** / **`isPlayAnywhereActive()`** (iOS, from 6.0.0): Show or hide the
  PlayAnywhere overlay from your own UI, and read whether it is on screen.



# Examples

In the following example, you'll find an application showcasing various uses of the Mediastream SDK for iOS. This app provides practical examples of key functionalities, including audio playback, video playback, audio as a service, casting, and more. Make sure you enter the IDs corresponding to your ACCOUNT_ID and CONTENT_ID and enjoy.

Open `MediastreamSampleApp.xcodeproj` and build. There is no dependency manager step: Xcode
resolves the Swift Package on its own the first time you open the project. The sample resolves
`MediastreamPlatformSDKiOS` with **Up to Next Major Version** from `6.0.0`, exactly as a
consumer app would, and its `Package.resolved` records the dependency versions it was last
verified against.

[Sample](/apple/Sample)

# Release Notes iOS
## [Versión 6.0.0] - 2026-09-02
### Breaking
- **The deployment floor moves from iOS 12.0 to iOS 13.0, and resolving the package now
  requires Xcode 16 or later.** Both come from EaseLive, the dependency behind PlayAnywhere:
  its package declares iOS 13, and its manifest uses `swift-tools-version:6.0`, which an older
  Xcode cannot read — and that failure takes down the resolution of the whole dependency graph,
  not just the EaseLive part. Xcode 16 is a requirement for your build machine, not for your
  users' devices.
- **No code changes are needed to upgrade.** The public API is unchanged and nothing that
  worked on 5.2.0 was removed or renamed. Apps that must keep supporting iOS 12 stay on
  `5.2.0`.
### Features
- **PlayAnywhere, the EaseLive interactivity overlay, is now part of the SDK.** Set
  `config.playAnywhere = PlayAnywhereConfig(accountId:programId:)` and the overlay is created
  with the player, toggled from a button in the custom UI, and torn down with it. `EaseLiveSDK`
  ships as a dependency of the Swift Package, so there is nothing extra to install and no
  opt-in module to add. Requires `customUI = true`. Leaving `playAnywhere` unset keeps the SDK
  behaving exactly as before.
- New events **`playAnywhereStatusChanged`** and **`playAnywhereError`**, and new methods
  **`togglePlayAnywhere()`** and **`isPlayAnywhereActive()`**. A fatal EaseLive error releases
  the overlay and reports `disabled` instead of taking playback down with it.
- The overlay follows EaseLive's status in both directions, so a web app that goes quiet
  between interactive segments has its overlay hidden and shown again instead of the session
  ending at the first `disabled`.
- Playback commands coming from the overlay — play, pause, seek, rate, volume, mute — go
  through the SDK's own controls rather than straight to `AVPlayer`, so they respect casting
  mode, keep the volume slider in agreement, and do not move the content player during an ad
  break.
### Notes
- **If your build environment restricts outbound network access**, allow
  `github.com/ease-live` and `sdk.easelive.tv`.
- Your app receives `EaseLiveSDK.framework` embedded alongside the SDK.

## [Versión 5.2.0] - 2026-08-26
### Bug Fixes
- **No more spurious `onAdLoadingError` on every playback with ads.** Requesting ads while the
  player's view had no window made IMA reject the request outright. A later retry succeeded and
  playback was never affected, but the error reached your app in every ad session, so anyone
  forwarding ad errors to monitoring saw a 100% error rate. The request now waits until the ad
  container is on screen, with a three-second watchdog that abandons the ad and lets the content
  through if it never gets there.
- **Mid-rolls and post-rolls play again when ads are requested before the player exists.** IMA
  captures the content playhead when the ads are requested and tracks progress through it to
  decide when a VMAP break is due; it used to receive `nil` in that window, which lost every
  break except the pre-roll — invisible in a short test run, since a pre-roll starts at `start`.
### Features
- Client-side ads are requested as soon as the ad tag is known, instead of after analytics
  initialise and the Google DAI branch runs, which removes the wait that sat between resolving
  the tag and asking IMA for it.

## [Versión 5.1.0] - 2026-08-19
### Distribution
- **The SDK is now distributed through Swift Package Manager.** `MediastreamPlatformSDKxC`
  5.0.1 is the last version published to CocoaPods. Migrating needs no code changes — see
  [Migrating from CocoaPods](#migrating-from-cocoapods).
- Installation, dependency ranges and a per-release compatibility table are now public in
  the [distribution repository](https://github.com/mediastream/MediastreamPlatformSDKiOS-spm#readme).
### Analytics
- Youbora updated from 6.3.9 to 6.7.23. Nothing stopped being reported; `/init` grew from 16
  to 21 fields.
- **Behaviour change to be aware of:** Youbora 6.7 reports recoverable playback stalls as
  fatal errors, which 6.3 ignored entirely. On the dashboard a transient hiccup now counts as
  an error and splits one view into two sessions. This is a change in NPAW's adapter, not a
  regression in the SDK.
- Youbora's configuration endpoint moved to `a-fds.youborafds01.com`. Allow it if you filter
  outbound traffic.
### Notes
- `getVersion()` now reads the version from the framework bundle instead of a hardcoded
  constant, so it always matches the binary you are running.

## [Versión 3.0.2] - 2026-04-30
### Notes
- Pod-only release. No code changes from 3.0.1. Resolves a CocoaPods publishing issue that prevented 3.0.1 from being consumed correctly.

## [Versión 3.0.1] - 2026-04-25
### Features
- **AirPlay native button on Custom UI:** New `showAirplayButton` config property adds a native `AVRoutePickerView` to the custom UI controls bar without any host-app code. Use `useCustomAirplayButton` to supply your own `UIButton` as the host for the route picker instead.
- **External playback state property:** `isPlayerExternalPlaybackActive` (read-only) combines `AVPlayer.isExternalPlaybackActive` with the system audio route so you can check AirPlay state even before the player is initialized.
- **`externalPlaybackActiveChanged` event:** Fires with a `["external_playback_active": Bool]` payload whenever AirPlay video or audio routing changes. Coalesced to avoid redundant callbacks during teardown.
- **AirPlay metrics:** Internal metrics now include an `ext_pb=1` flag in the analytics query when external playback is active.
### Bug Fixes
- **DAI + DRM (FairPlay):** Fixed streams that combine Google DAI with FairPlay. IMA `loadStream` uses a plain `https://` asset (no custom loader), then the SDK hands off to `initAssetLoader` once the stitched URL is known, ensuring `play()` is triggered correctly after the handoff.
- **DVR + DRM:** `AssetLoaderDelegate` for DVR streams is now held with a strong reference, preventing early deallocation during live/DVR mode switches.
- **DVR start+end window:** Fixed slider and current-time display when `asset.duration` is indefinite (live HLS); the SDK now uses the seekable range duration instead. DVR start+end streams automatically seek to the window start on load.

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
## [Versión 2.1.0] - 2026-08-19
First production release of this SDK, and the first distributed through Swift Package
Manager. Previous versions never left QA.

### Fixed
- **Playback did not work at all on tvOS 26: black screen, no events and no error.** Starting
  with that tvOS version, an `AVPlayerItem` whose `AVPlayer` has no output attached to the
  view hierarchy never finishes loading. The SDK created its player *after* the content
  reported ready, so it asked the item to load without ever giving it a screen.

  **If your app targets Apple TV and any of your users have updated to tvOS 26, playback is
  currently broken for them.** This was not introduced by this release — the previous code
  fails the same way on tvOS 26 and works on tvOS 17 — so upgrading to 2.1.0 is the fix.

### Distribution
- **The SDK is now distributed through Swift Package Manager.**
  `MediastreamPlatformSDKAppleTV` 2.0.0-qa.02 is the last version published to CocoaPods.
  Migrating needs no code changes — see
  [Migrating from CocoaPods](#migrating-from-cocoapods).
- **The deployment floor moves from tvOS 14 to tvOS 15.** Required by Google's IMA SDK for
  tvOS from 4.16.0. Apps that must support tvOS 14 have to stay on the CocoaPods version.
- **The tvOS simulator works on Apple Silicon again.** The published podspec forced
  `EXCLUDED_ARCHS[sdk=tvossimulator*] = arm64` onto your target as well as its own, which
  prevented running a tvOS simulator on any M-series Mac.

### Analytics
- Youbora updated from 6.3.9 to 6.7.23, with the same behaviour change and endpoint move
  described in the iOS 5.1.0 notes above.

### Notes
- `getVersion()` now reads the version from the framework bundle instead of a hardcoded
  constant, so it always matches the binary you are running.

## [Versión 0.3.4] - 2025-02-12
### Features
- Google DAI replace ad tag parameters option.

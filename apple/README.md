# Welcome to the Mediastream Apple SDK

Hello, Apple Developer! 👋

Welcome to the Mediastream SDK for iOS and Apple TV, designed to streamline the integration of our powerful features into your applications. This SDK provides access to advanced Mediastream capabilities, allowing you to deliver exceptional multimedia experiences to your users.

## Version iOS
- **Version:** The current version of the SDK is 2.3.0.
- **Compatibility:** Compatible with Swift Version > 5.9

## Version Apple TV
- **Version:** The current version of the SDK is 0.3.4.
- **Compatibility:** Compatible with Swift Version > 5.9


## Adding Mediastream Platform SDK to Your iOS Project

To integrate the Mediastream Platform SDK into your iOS project, add the following dependency to your project's pod file:

```swift
pod 'MediastreamPlatformSDKxC', '~> 2.3.0'
```

## Adding Mediastream Platform SDK to Your Apple TV Project

To integrate the Mediastream Platform SDK into your tvOS project, add the following dependency to your project's pod file:

```swift
pod 'MediastreamPlatformSDKAppleTV', '~> 0.3.4'
```


### Basic Implementation

In this minimal setup, the SDK takes care of various intricate processes, leveraging the provided account ID, content ID, and content type to ensure a seamless experience. This simplicity enables you to focus on creating engaging applications without the need for extensive configurations.

First at all, don't forget to select "YES" in "Allow Arbitrary Loads", which is inside the "App Transport Security" property. Also, allow "Required Background mode" by selecting the option as in the image.

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

        self.addChildViewController(mdstrm)
        self.view.addSubview(mdstrm.view)

        mdstrm.setup(playerConfig)
        mdstrm.play()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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

- **`adUrl` (String):** AdURL (e.g., VAST). If not specified, will play ads configured in the Mediastream Platform.
- **`accessToken` (String):** Access token for restricted videos.
- **`autoplay` (boolean):** Autoplay video if true. Default: false.
- **`videoFormat` (MediastreamPlayerConfig.AudioVideoFormat):** Type of video (e.g., HLS). Possible values: HLS, M4A. Default: HLS.
- **`dvr` (boolean):** Player starts prepared to use DVR. Default: false.
- **`windowDVR` (int):** Window DVR voiced in seconds.
- **`showControls` (boolean):** Hide the controls of the player. Default: true.
- **`referer` (string):** Allows setting a custom referrer for statistics.
- **`src` (string):** Arbitrary source to reproduce.
- **`loadNextAutomatically` (boolean):** Allows playing the next episode if it exists. Available only when the EPISODE type is set. Default: false.
- **`appName` (string):** Very useful to identify traffic in platform analytics. Example: "mediastream-app-tv" or "mediastream-app-mobile".
- **`playerId` (String):** Takes player configuration from platform settings.
- **`tryToGetMetadataFromLiveWhenAudio` (boolean):** If your live content contains TPE1 and TIT2 tags on the manifest, this metadata will be parsed and sent on `onLiveAudioCurrentSongChanged` event. Default: true.
- **`googleImaPPID` (String):** Allows to set PPID (Publisher Provided Identification) to IMA SDK.
- **`googleImaLanguage` (String):** Allows to set language to IMA SDK UI. Default: en.
- **`addAdCustomAttribute`(key <String>, value <String>):** Allows sending custom parameters in an advertising VAST. To make it work, you need to include the *cust.* query in the VAST query strings, followed by the key you want to replace. Example: *&cust.test_ca=*. To replace it, call *config.addAdCustomAttribute("test_ca", "hi")*, which will result in the final URL being: *&cust.test_ca=hi*. (Just works if adurl is comming on the config.)
- **`adTagParameters`([AdRequestParam: String]):** Allows you to set the parameters of the url tag when there is google dai, any field that appears in the enum will be appended or replaced in the original url tag.

# Implementing Event Handling with `MediastreamPlayerCallback`

The `MediastreamPlayerCallback` interface in the Mediastream SDK serves as the contract for handling various player events. By implementing this interface, you can listen to and respond to different states and actions during playback. Here's how you can use it:

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

MediastreamPlayer is an instance of AVPlayer so it has all its features, it is extendable to other features such as AirPlay Cast, where it is only necessary to generate an instance of the button and add it to the player layer and it will work without problems.

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
   - Called when the current video starts playing from the beginning.

5. **`ready:`**
   - Called when the current video resumes playing from a paused state.

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


These events allow you to respond dynamically to various states and actions during playback.

# Player Methods

The Mediastream player provides several methods that you can use to control playback and access various functionalities. Here is an overview of the main methods available:

## `play()`

Starts playback of the current content.

## `pause()`

Pauses playback of the current content.

## `forward(amount: Double)`

Seek to time established.

## `backward(amount: Double)`

Seek to time established.

## `seekTo(position: Double)`

Seeks to a position specified in milliseconds in the current video.

## `startPiP()`

Launches picture in picture functionality if available on the device.

## `stopPiP()`

Stopped picture in picture functionality.
## `changeSpeed(playbackSpeed: Float)`

Allows you to change the content playback speed.

## `reloadPlayer(config: MediastreamPlayerConfig)`

Allows to reload the player with a new content without kill the player instance.

## `releasePlayer()`

When you are finished using this MediastreamPlayer, make sure to call this method to kill player instance.



# Examples

In the following example, you'll find an application showcasing various uses of the Mediastream SDK for iOS. This app provides practical examples of key functionalities, including audio playback, video playback, audio as a service, casting, and more. Make sure you enter the IDs corresponding to your ACCOUNT_ID and CONTENT_ID and enjoy.

Remember do a `pod install` before run the example.

[Sample](/apple/Sample)

# Release Notes iOS
## [Versión 2.3.0] - 2025-02-18
### Features
- Update Google IMA SDK to 3.24.0 version
### Bug Fixes
- NSRange Exception when move faster on timeline

# Release Notes AppleTV
## [Versión 0.3.4] - 2025-02-12
### Features
- Google DAI replace ad tag paraemeters option.

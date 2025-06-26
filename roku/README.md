# Welcome to the Mediastream ROKU SDK

Hello, Roku Developer! 👋

Welcome to the Mediastream SDK for Roku, designed to streamline the integration of our powerful features into your applications. This SDK provides access to advanced Mediastream capabilities, allowing you to deliver exceptional multimedia experiences to your users.

## Version
- **Version:** The current version of the SDK is 7.0.14.

## Adding Mediastream Platform SDK to Your Roku Project

To integrate the Mediastream Platform SDK into your Roku project, you need to download the package form the following links:

Latest Version:
```brightscript
https://player.cdn.mdstrm.com/roku_sdk/MediaStreamPlayer.pkg
```

Specific Version:
```brightscript
https://player.cdn.mdstrm.com/roku_sdk/7.0.14/MediaStreamPlayer.pkg
```

### Basic Implementation

Once downloaded we need to add it to our project. To do this we need to create a folder called source at the base of our project and inside this folder create a new folder called packageFile and place MediaStreamPlayer.pkg inside it.

![Sample](/images/AddingMediastreampkg.png)

Then the sdk must be included to the channel. To do this we need to incorporate the following into your MainScene.xml

```xml
  <interface>
    <field id="msPlayerVideoStatus" type="assocarray" alwaysNotify="true"/>
    <field id="msPlayerVideoPosition" type="assocarray" alwaysNotify="true"/>
  </interface>
  <children>
    <Group id="msRokuPlayerContainer"></Group>
  </children>
```

Now it's time to initialize the SDK, to do it we need:
* Set `m.SDKPath` with the path of where Mediastream.pkg is located
* Create an object of type `MediastreamPlayer`
* Attach the created object to the `msRokuPlayerContainer` container
* Set the `msPlayerVideoPosition`, `SDKStatus` and `msPlayerVideoStatus` observers with functions that allow us to handle these events.

For example, we create a function initPlayer and set Observers to exemplify:

```brightscript
function initPlayer()
    print "MainScene : initPlayer"
    m.SDKPath = "pkg:/source/packageFile/MediaStreamPlayer.pkg"
    if(m.mediaStreamPlayer <> invalid)
       m.msRokuPlayerContainer.removeChild(m.mediaStreamPlayer)
       m.mediaStreamPlayer = invalid
    end if
    m.mediaStreamPlayer = CreateObject("roSGNode", "MediaStreamPlayer")
    m.mediaStreamPlayer.id = "mediaStreamPlayer"
    m.mediaStreamPlayer.callFunc("initializeSDK", m.SDKPath)
    m.msRokuPlayerContainer.appendChild(m.mediaStreamPlayer)
end function
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
- **`dvr` (boolean):** Player starts prepared to use DVR. Default: false.
- **`windowDVR` (int):** Window DVR voiced in seconds.
- **`appName` (string):** Very useful to identify traffic in platform analytics. Example: "mediastream-app-tv" or "mediastream-app-mobile".
- **`playerId` (String):** Takes player configuration from platform settings.
- **`startAt`(Number):** Skip or seek at starting, used in keep watching so this starts the video at the same point where the user left it.

## **Debug Parameters:**
- **`setAdsDebugOutput` (boolean):** Enables or disables verbose logging for the ad system. When `true`, the SDK will print detailed information about ad events, ad break scheduling, and any errors encountered during ad playback. Useful for debugging ad-related issues during development. (Default false)
- **`setAdMeasurements` (boolean):** Enables or disables ad measurement tracking integrations (e.g., Nielsen, Moat, etc.). When true, the SDK will activate measurement pings and tracking events required by third-party measurement vendors. Set to false if measurement is not needed or during debugging to avoid sending real data. (Default false)
- **`setJITPods` (boolean):** Enables Just-In-Time (JIT) ad pod insertion. When true, the SDK will request and insert ad pods dynamically at playback time, rather than pre-fetching them all in advance. This is typically used to ensure fresher ad content or in environments with dynamic ad decisions. (Default false)
- **`enableNielsenDAR` (boolean):** Enables Nielsen Digital Ad Ratings (DAR) integration. When true, the SDK will include DAR-specific tracking events during ad playback to support Nielsen audience measurement. This should be enabled only if the client requires DAR reporting.
- **`nielsenAppId` (boolean):** Sets the unique application identifier used by Nielsen for audience measurement. This ID is provided by Nielsen and must be configured correctly to ensure accurate tracking and reporting.
- **`contentGenres` (string):** Specifies the content genre for the current playback session. This value is used by third-party measurement services like Nielsen to categorize content appropriately (e.g., "GV" for General Viewing, "CL" for Children).
- **`nielsenProgramId`(string):** Sets the program identifier used for Nielsen tracking. Internally, the SDK uses this value to call setNielsenProgramId, which helps Nielsen attribute viewership to specific programs or episodes.

# Implementing Event Handling

You can subscribe to different events with the Mediastream SDK for Roku, for example:

```brightscript
function initPlayer()
    print "MainScene : initPlayer"
    m.SDKPath = "pkg:/source/packageFile/MediaStreamPlayer.pkg"
    if(m.mediaStreamPlayer <> invalid)
       m.msRokuPlayerContainer.removeChild(m.mediaStreamPlayer)
       m.mediaStreamPlayer = invalid
    end if
    m.mediaStreamPlayer = CreateObject("roSGNode", "MediaStreamPlayer")
    m.mediaStreamPlayer.id = "mediaStreamPlayer"
    m.mediaStreamPlayer.callFunc("initializeSDK", m.SDKPath)
    m.msRokuPlayerContainer.appendChild(m.mediaStreamPlayer)
    m.mediaStreamPlayer.observeField("SDKStatus", "onSDKStatusChanged")
end function
```

```brightscript
sub setObservers()
    m.top.observeField("msPlayerVideoPosition", "onMsPlayerVideoPositionChanged")
    m.top.observeField("msPlayerVideoStatus", "onMsPlayerVideoStatusChanged")
end sub
```

```brightscript
function onMsPlayerVideoStatusChanged(eventData as dynamic)
    print "MainScene : onMsPlayerVideoStatusChanged " eventData.getData()
end function

function onMsPlayerVideoPositionChanged(eventData as dynamic)
    print "MainScene : onMsPlayerVideoPositionChanged " eventData.getData()
end function

function onSDKStatusChanged(event as Dynamic)
    payload = event.getData()
    print "MainScene : onSDKStatusChanged : payload : " payload
    if payload.status = "Loaded" then m.isSDKLoaded = true
end function
```

# Event Listening in Mediastream SDK

The Mediastream SDK allows you to listen to various events emitted by the player, providing valuable hooks into the playback lifecycle. Here are the available events:

1. **`playing:`**
   - Called whenever the video starts playing.

2. **`paused:`**
   - Called whenever the video stops playing.

3. **`finished:`**
   - Called whenever the video ends playing.

4. **`buffering:`**
   - Called when player enters buffering state.

These events allow you to respond dynamically to various states and actions during playback.

# Examples

In the following example, you'll find an application showcasing various uses of the Mediastream SDK for Roku. This app provides practical examples of key functionalities, including audio playback, video playback, audio as a service, casting, and more. Make sure you enter the IDs corresponding to your ACCOUNT_ID and CONTENT_ID and enjoy.

[Sample](/roku/MediastreamRokuSample)

# Welcome to the Mediastream ROKU SDK

Hello, Roku Developer! 👋

Welcome to the Mediastream SDK for Roku, designed to streamline the integration of our powerful features into your applications. This SDK provides access to advanced Mediastream capabilities, allowing you to deliver exceptional multimedia experiences to your users.

## Version
- **Version:** The current version of the SDK is 7.0.1.

## Adding Mediastream Platform SDK to Your Roku Project

To integrate the Mediastream Platform SDK into your Roku project, you need to download the package form the following links:

Latest Version:
```brightscript
https://player.cdn.mdstrm.com/roku_sdk/MediaStreamPlayer.pkg
```

Specific Version:
```brightscript
https://player.cdn.mdstrm.com/roku_sdk/7.0.1/MediaStreamPlayer.pkg
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
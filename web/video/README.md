# Player API

## [Overview](#api-overview)

This API allows you to embed and control your VOD and Live Stream player on your website with a simple JavaScript interface.

The API will load a player inside an `iframe` and will work on all the devices supported by our platform (Mac, Windows, iOS, Android, SmartTV).

## [Requirements](#api-requirements)

The API requires to be loaded in a browser with JavaScript enabled and support of the HTML5 feature `postMessage`. Nearly all modern browsers support this feature, including Internet Explorer 8+.

---

# Usage

## [Getting started](#usage-getting-started)

Basic example of loading and usage

```html
<!doctype html>
 <html>
 <body>
  <!-- Load the JavaScript library -->
  <script src="https://platform-static.cdn.mdstrm.com/js/player_api.js"></script>

  <!-- Create the element that will contain the iframe. You will use the #ID later -->
  <div id="mdstrm-player"></div>

  <!-- Create a new player using the JavaScript API -->
  <script>

   // Options:
   var playerOptions = {
    width: 640, // Width in pixels
    height: 360, // Height in pixels
    type: "media", // Video type. Possible values: "media", "live"
    id: "53453a861536b4526c9bd16e", // Video ID
    autoplay: false, // Enable autoplay. Possible values: true, false
    events: { // Callbacks to be triggered when certain actions are executed by the player. All optional.
     onPlayerReady: function() { // Optional callback to be triggered as soon as the player has finished loading
      console.log("Player is ready");
     },
     onPlay: function() { // Optional callback to be triggered as soon as the player starts playing
      console.log("Playing...");
     },
     onVideoEnd: function() { // Optional callback to be triggered when the video ends playing
      console.log("Video just ended");
     },
     onVideoStop: function() { // Optional callback to be triggered when the user stops or pauses the video
      console.log("User stopped or paused the video");
     },
     onVideoError: function() { // Optional callback to be triggered when there's a playback error
      console.log("There was an error while loading the video");
     },
     onVolumeChange: function(volume) { // Optional callback to be triggered when volume is changed (0-100)
      console.log("Volume was changed to " + volume);
     },
     onTimeUpdate: function(time) { // Optional callback to be triggered when time is updated
       console.log("Current time is " + time);
     },
     onFullscreenChange: function(fullscreen) { // Optional callback to be triggered when fullscreen status change
       console.log("Is fullscreen " + fullscreen);
     }
    }
   };

   // The class MediastreamPlayer is used to create a new player instance
   // First argument is the #ID of the containing element. Second argument is an Object of options
   var player = new MediastreamPlayer("mdstrm-player", playerOptions);

   // Player can be controlled using the API's methods
   player.videoPlay();
  </script>

 </body>
 </html>
```

## [Options](#usage-options)

| Name | Type   | Mandatory | Description |
| --- |-------| --- | ---
| id | String | Yes | Platform Video ID. Alternatively, a YouTube Video ID can be used with the `youtube:<Video ID>` format. Example: `youtube:dQw4w9WgXcQ` |
| type | String | Yes | Video type. Possible values: `media`, `live`, `dvr` |
| width | Number | Yes | Embed width in pixels |
| height | Number | Yes | Embed height in pixels |
| autoplay | Boolean | | Enable autoplay |
| pip | Boolean | | Enable picture and picture. Default is true.|
| controls | Boolean | | Enable player controls |
| volume | Number | | Sets initial volume (from 0 to 100) |
| starttime | Number | | Playback will start at the specified number of seconds (video starts at 0 seconds) |
| endtime | Number | | Playback will start at the specified number of seconds (video starts at 0 seconds) |
| start | Date   | | DVR start time |
| end | Date   | | DVR end time |
| title | String | | Video title |
| show_title | Boolean | | Shows video title bar |
| access_token | String | | Generated token in case of closed access |
| customer | String | | Customer ID |
| distributor | String | | Distributor ID |
| debug | Boolean | | Enable debug info on browser console |
| ads | Object | | Forces the display of a specific Ad (will override Platform's Ad) |
|  |        | | `ads.map` VAST or VMAP URL with Ad settings |
|  |        | | `ads.overlay` VAST URL with overlay Ad settings |
|  |        | | `ads.volume` Sets ads volume (from 0 to 100) |
| style | Object | | Changes UI colors. Valid only when embedding a YouTube Video |
|  |        | | `style.basecolor` Player Base color in HEX format. Example: `bfd62e` |
|  |        | | `style.backgroundcolor` Controls Base color in HEX format. Example: `07141e` |
| youbora | Array  | | Allows customization of Youbora's integration |
|  |        | | `youbora.extra` Array containing 'extra params' to be sent. Example: `"youbora": {"extra": ["Example 1", "Example 2"]}}` For Youbora this will mean: `{'content.customDimension.1':'Example 1', 'content.customDimension.2':'Example 2'}`|
| pause_ad_on_click | Boolean |  | Enable pause on AD playback |
| show_controls_on_ad | Boolean |  | Enable player controls (play, pause, volumen, fullscreen) on AD playback |
| pause_on_screen_click | Boolean |  | Enable pause when the user clicks on the screen |
| player | String |  | Platform Player ID. Force to use a specific Platform Player [Player Settings](https://platform.mediastream/settings/player "Player Settings"). |
| mse | Boolean | | Sets HTML5 MSE |
| mse_buffer_size | Number | | Sets player buffer size in MB. Default: `60` |
| mse_buffer_length | Number | | Sets player buffer length in seconds. Default: `30` |
| custom_related | Array  | | Define media ids to be used as custom relateds |
| custom | Object | | Sends custom variables to player. Example: `{"foo": "bar", "apple": "orange"}` |
| events | Object | | Event callbacks (see list of events) |
| watermark | Object | | Allows show a watermarking in different positions at the screen |
|  |        | | `watermark.show_watermark` Enabled use of watermarking. Possible values: `true`, `false`|
|  |        | | `watermark.position` Set a position of watermark. Possible values: `top-right`, `top-left`, `bottom-right`, `bottom-left`. Default: `top-right`|
|  |        | | `watermark.content` Allows set any text on watermarkin content. Default: `playback-id`|
| controls_bar | Object | No | Change player controls visibility flags (`true`, `false`). Possible attributes: `showPlayPause`, `showBackward`, `showForward`, `showVolume`, `showTimeline`, `showSettings`, `showFullscreen`
| googleImaPpid | String | No | Sets the Publisher provided identifier (PPID)

## [Events](#usage-events)

Events are functions triggered by the API

| Function name | Description |
| --- | --- |
| onPlayerReady | Triggered when the API is ready to play video. |
| onPlay | Triggered when video starts playing. |
| onVideoEnd | Triggered when the video has ended. |
| onVideoStop | Triggered when the user has stopped or paused video. |
| onVideoError | Triggered when the player has encountered an error playing video. |
| onVolumeChange | Triggered when the user changes volume. Returns new volume (from 0 to 100). |
| onTimeUpdate | Triggered when time changes. Returns current time.|
| onFullscreenChange | Triggered when the user changes full screen mode. Returns current state (Boolean). |
| onSeeking | Triggered when the user is seeking. Returns seeking position. |
| onSeeked | Triggered after the user has seeked throught the media. Returns seeked position. |
| onReplay | Triggered when the user starts playing video after media end. |
| onBuffering | Triggered when player enters buffering state. |
| onMetadata | Triggered when player is ready. Return additional information line title, duration, etc. |

Ads Events triggers

| Function name | Description |
| --- | --- |
| onAdsAdBreakReady | Triggered when an ad rule or a VMAP ad break would have played if autoPlayAdBreaks is false.|
| onAdsAdMetadata | Triggered when an ads list is loaded. |
| onAdsAllAdsCompleted | Triggered when the ads manager is done playing all the ads. |
| onAdsClick | Triggered when the ad is clicked. |
| onAdsComplete | Triggered when the ad completes playing. |
| onAdsContentPauseRequested | Triggered when content should be paused. This usually happens right before an ad is about to cover the content. |
| onAdsContentResumeRequested | Triggered when content should be resumed. This usually happens when an ad finishes or collapses. |
| onAdsDurationChange | Triggered when the ad's duration changes. |
| onAdsFirstQuartile | Triggered when the ad playhead crosses first quartile. |
| onAdsImpression | Triggered when the impression URL has been pinged. |
| onAdsLinearChanged | Triggered when the displayed ad changes from linear to nonlinear, or vice versa. |
| onAdsLoaded | Triggered when ad data is available. |
| onAdsLog | Triggered when a non-fatal error is encountered. The user need not take any action since the SDK will continue with the same or next ad playback depending on the error situation. |
| onAdsMidpoint | Triggered when the ad playhead crosses midpoint. |
| onAdsPaused| Triggered when the ad is paused. |
| onAdsResumed | Triggered when the ad is resumed. |
| onAdsSkippableStateChanged | Triggered when the displayed ads skippable state is changed.|
| onAdsSkipped | Triggered when the ad is skipped by the user. |
| onAdsStarted | Triggered when the ad starts playing. |
| onAdsThirdQuartile | Triggered when the ad playhead crosses third quartile. |
| onAdsUserClose | Triggered when the ad is closed by the user. |
| onAdsVolumeChanged | Triggered when the ad volume has changed.|
| onAdsVolumeMuted | Triggered when the ad volume has been muted. |



## [Methods](#usage-methods)

| Method | Description |
| --- | --- |
| isReady | Indicates if API is ready to be used. Returns `Boolean` |
| isPlaying | Indicates if player is playing video. Returns `Boolean` |
| getCurrentTime | Gets current playback time. Accepts callback `function(currentTime)` |
| videoPlay | Starts video playback. Returns `void` |
| videoStop | Stops video playback. Returns `void`|
| seekTo | Seeks video to specified position. Accepts `Number` representing desired video position in seconds |
| requestAds | Starts playback of ads. Accepts `String` representing VAST URL |
| setVolume | Changes the video volume as specified. Accepts `Number` representing desired volume (from 0 to 100) |
| setAdsVolume | Changes the ads volume as specified. Accepts `Number` representing desired volume (from 0 to 100) |
| playbackLevels | Returns an `Array` that represent available qualities present in the stream |
| playbackLevel | Change the quality to be played. Accepts a `Number` that represent the quality index |
| toggleFullScreen | Changes the current full screen mode. |

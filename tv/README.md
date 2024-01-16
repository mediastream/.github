# Tizen Player API

## [Overview](#api-overview)

This API allows you to embed and control your VOD and Live Stream player on your TV APP with a simple JavaScript usin AVPlay Samsung Interface.

## [Limitations](#api-limitations)

This player does not support the reproduction of advertising of any kind. It also has no DRM support.

## [Requirements](#api-requirements)

The API requires to be loaded in a browser with JavaScript enabled.

## [Versioning](#api-versioning)

Currently the API allow you to select a specific version in url if you want. Latest version will be used unless specified.

### Resource:
  `https://player.cdn.mdstrm.com/tizen_player/<VERSION>/player.js`
### Example:
Using latest: `https://player.cdn.mdstrm.com/tizen_player/player.js` Using 1.0.0 `https://player.cdn.mdstrm.com/tizen_player/1.0.0/player.js`

# Usage

## [Getting started](#usage-getting-started)

Basic example of loading and usage

```html
<!doctype html>
 <html>
 <body>
  <!-- Load the JavaScript library -->
  <script src="https://player.cdn.mdstrm.com/tizen_player/player.js"></script>

  <!-- Create the element that will contain the iframe. You will use the #ID later -->
  <div id="mdstrm-player"></div>

  <!-- Create a new player using the JavaScript API -->
  <script>
   // Options:
   var playerOptions = {
    type: "media", // Video type. Possible values: "media", "live"
    id: "CONTENT_ID", // Video ID
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
     onTimeUpdate: function(time) { // Optional callback to be triggered when time is updated
       console.log("Current time is " + time);
     },
     onBufferingStart: function() {
      console.log("Init buffering")
     },
     onBufferingStop: function() {
	    console.log("End buffering")
	   }
    }
   };

   // The class MediastreamPlayer is used to create a new player instance
   // First argument is the #ID of the containing element. Second argument is an Object of options
   var player = new MediastreamPlayer("mdstrm-player", playerOptions);
  </script>

 </body>
 </html>
```

## [Options](#usage-options)

| Name | Type   | Mandatory | Description |
| --- |-------| --- | ---
| id | String | Yes | Platform Video ID. |
| type | String | Yes | Video type. Possible values: `media`, `live`|
| autoplay | Boolean | No | Enable autoplay |
| controls | Boolean | No | Enable player controls |
| title | String | No | Video title |
| access_token | String | No | Generated token in case of closed access |
| customer | String | No | Customer ID |
| distributor | String | No | Distributor ID |
| events | Object | No | Event callbacks (see list of events) |

## [Events](#usage-events)

Events are functions triggered by the API

| Function name | Description |
| --- | --- |
| onPlayerReady | Triggered when the API is ready to play video. |
| onPlay | Triggered when video starts playing. |
| onVideoEnd | Triggered when the video has ended. |
| onVideoStop | Triggered when the user has stopped or paused video. |
| onVideoError | Triggered when the player has encountered an error playing video. |
| onTimeUpdate | Triggered when time changes. Returns current time.|
| onBufferingStart | Triggered when player enters buffering state. |
| onBufferingStop | Triggered when player comes out buffering state. |

## [Methods](#usage-methods)

| Method | Description |
| --- | --- |
| isReady | Indicates if API is ready to be used. Returns `Boolean` |
| isPlaying | Indicates if player is playing video. Returns `Boolean` |
| getCurrentTime | Gets current playback time. Accepts callback `function(currentTime)` |
| videoPlay | Starts video playback. Returns `void` |
| videoStop | Stops video playback. Returns `void`|
| seekTo | Seeks video to specified position. Accepts `Number` representing desired video position in seconds |
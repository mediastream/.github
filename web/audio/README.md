# API Mediastream Lightning Player

## [Overview](#api-overview)

This API allows you to embed and control your Audio player on your website with a simple JavaScript interface.

## [Requirements](#api-requirements)

The API requires to be loaded in a browser with JavaScript enabled. Nearly all modern browsers support this feature, including Internet Explorer 9+.

## [Versioning](#api-versioning)

Currently the API allow you to select a specific version in url if you want. Latest version will be used unless specified.

#### Resource:

`https://player.cdn.mdstrm.com/lightning_player/<VERSION>/api.js`

#### Example:

Using latest: `https://player.cdn.mdstrm.com/lightning_player/api.js`
Using v0.0.128 `https://player.cdn.mdstrm.com/lightning_player/v0.0.128/api.js`

---

# Usage

## [Getting started](#usage-getting-started)

There are many ways to load the player API. You should choose the one that best fits your particular implementation needs.

#### [Loading with playerloaded event](#loading-with-playerloaded-event)

Player will be rendered where the script is placed unless combined with `data-container` attribute.
You can attatch a `playerloaded` event to the script to know when it has been loaded.
NOTE: The event structure has a `detail` property which corresponds to the player API instance.

```html
<!doctype html>
<html>
  <body>
    <script
      src="https://player.cdn.mdstrm.com/lightning_player/api.js"
      data-type="media"
      data-id="5f7f563e34cd7e08221af329"
      data-app-name="appName"
      id="playerScript"
    ></script>
    <script type="text/javascript">
      document.getElementById('playerScript').addEventListener('playerloaded', ({ detail: player }) => {
        /**
         * Here you can use player api
         */
        console.log('*** player loaded', player)
      })
    </script>
  </body>
</html>
```

#### [Loading with loaded attribute](#loading-with-loaded-attribute)

Player will be rendered where the script is placed unless combined with `data-container` attribute.
You can attatch a `data-loaded` attribute referencing the name of a global (window scope) function to be called when loaded.

```html
<!doctype html>
<html>
  <head>
    <script type="text/javascript">
      function playerLoaded (player) {
        /**
         * Here you can use player api
         */
        console.log('*** player loaded', player)
      }
    </script>
  </head>
  <body>
    <script
      src="https://player.cdn.mdstrm.com/lightning_player/api.js"
      data-type="media"
      data-id="5f7f563e34cd7e08221af329"
      data-app-name="appName"
      id="playerScript"
      data-loaded="playerLoaded"
    ></script>
  </body>
</html>
```

#### [Loading with data-container](#loading-with-data-container)

Using the `data-container` attribute when loading script tag you can inform where the content should be rendered, thus eliminating the need to place the script itself inside the page `body`.
You will need to use either the `playerloaded` event or the `data-loaded` attribute to know when the player has been loaded.

```html
<!doctype html>
<html>
  <head>
    <script type="text/javascript">
      function playerLoaded (player) {
        /**
         * Here you can use player api
         */
        console.log('*** player loaded', player)
      }
    </script>
    <script
      src="https://player.cdn.mdstrm.com/lightning_player/api.js"
      data-container="player-div"
      data-type="media"
      data-id="5f7f563e34cd7e08221af329"
      data-app-name="appName"
      id="playerScript"
      data-loaded="playerLoaded"
    ></script>
  </head>
  <body>
    <div id="player-div"></div>
  </body>
</html>
```

#### [Loading with javascript method](#loading-with-javascript-method)

A `loadMSPlayer` function will be attatched to the `window` scope when no `data-id` attribute exists on the script tag. This allows for the API to be instanciated manually when needed.
The `loadMSPlayer` function returns a `Promise` that will resolve with the player API instance.
NOTE: Options passed as second parameter for this function are the same supported as tag attributes when loading with the script tag directly, but they are camelCased instead of dash-cased. For example, the equivalense for `data-ads-map` would be `adsMap`.

```html
<!doctype html>
<html>
  <head>
    <script src="https://player.cdn.mdstrm.com/lightning_player/api.js"></script>
  </head>
  <body>
    <div id="player-div"></div>
    <script>
      loadMSPlayer('player-div', {
        type: 'media',
        id: '5f7f563e34cd7e08221af329',
        volume: 0,
        appName: 'appName'
      }).then(player => {
        /**
         * Here you can use player api
         */
        console.log('** player loaded', player)
      }).catch(console.error)
    </script>
  </body>
</html>
```

## [Script Tag Attributes](#script-tags-attributes)

You can specify many attributes when loading the script. Those attributes will be taken into account when constructing the player. If the value of the attribute looks like `$value` it will be read from `window[value]` when loading.

| Name | loadMSPlayer option | Mandatory | Type | Description |
| --- | --- | --- | --- | --- |
| src | N/A | Yes | `String` | Source of player javascript file. |
| data-type | type | Yes | `String` | Content type. Possible values: `media`, `live`, `dvr`, `episode`. |
| data-id | id | Yes | `String` | Id of content to load. |
| data-width | N/A | No | `String` | Embed width, it can be any unit css this set the style on div. |
| data-height | N/A | No | `String` | Embed height, it can be any unit css this set the style on div. |
| data-autoplay | autoplay | No | `Boolean` | Enable / Disable autoplay. Defaults to Platform player config. |
| data-volume | volume | No | `Number` | Sets initial volume (from 0 to 1). |
| data-container | N/A | No | `String` | DOM element id to search for and insert player in. The player will load where the script is places unless provided. When using the `loadMSPlayer` function, container id is passed as the first parameter. |
| data-player | player | No | `String` | Platform player id to be used. Correct default player will be inferred from content passed if not provided. |
| data-dnt | dnt | No | `Boolean` | Disable analytics tracking. Default: `false`. |
| data-access-token | accessToken | No | `String` | Content playback access token. |
| data-app-name | appName | No | `String` | Custom name for app player. Default: `lightning-player`. |
| data-app-version | appVersion | No | `String` | Useful to identify you app version. Default: Current lightning player version. |
| data-app-type | appType | No | `String` | Type of player app. Default: `web-app`. |
| data-start-pos | startPos | No | `Number` | Position in seconds from where to start playing. Default: `0`. |
| data-ads-map | adsMap | No | `String` | Custom ad VAST tag URL. |
| data-google-ima-ppid | googleImaPpid | No | `String` | Sets the Publisher provided identifier (PPID). |
| data-customer | customer | No | `String` | Customer id you are playing for analytics and continue playback. |
| data-distributor | distributor | No | `String` | Distributor id for analytics. |
| data-custom-* | custom | No | `String` or `Object` | Customs variables for ads. Note that `data-custom-example=1` would be equivalent to `{custom: {example: 1}}` when using `loadMSPlayer` function |
| data-view | view | No | `String` | Override the view to be used. Setting this option to `none` will render a player with no view elements. |
| data-render-as | renderAs | No | `String` | Inform the player what type of content you want to handle when using `none` view. Possible values are `video` or `audio` |
| data-detect-adblocker | detectAdblocker | No | `Boolean` |  Enable / Disable adblock detector. Default: `false`. |
| data-without-cookies | withoutCookies | No | `Boolean` | Enable / Disable Mediastream cookies. |
| data-ref | ref | No | `String` | Send custom referrer for analytics. Default: `location.hostname`. |
| data-listener-id | listenerId | No | `String`| Send Apple Id For Advertising (IDFA) or Google Advertising Id (GAID) |
## [API attributes](#api-attributes)

Once loaded, you can read and write attributes using the HTML5 compatible API exposed by the player.

#### Example
```html
<!doctype html>
<html>
  <head>
    <script src="https://player.cdn.mdstrm.com/lightning_player/api.js"></script>
  </head>
  <body>
    <div id="player-div"></div>
    <script>
      loadMSPlayer('player-div', {
        type: 'media',
        id: '5f7f563e34cd7e08221af329',
        volume: 0,
        appName: 'appName'
      }).then(player => {
        player.volume = 1 // set volume to 100%
        console.log(player.status) // will log player status
      }).catch(console.error)
    </script>
  </body>
</html>
```

| Property | Description | Type | Readonly |
| --- | --- | --- | --- |
| volume | Gets / Sets volume (from 0 to 1). | `Number` | No |
| src | Gets / Sets current video/audio URL source. | `String` | No |
| fullscreen | Gets / Sets fullscreen state. Only on player views that support fullscreen. | `Boolean` | No |
| currentTime | Gets / Sets playback current time in seconds. | `Number` | No |
| playerType | Gets player type. | `String` | Yes |
| paused | Gets if player paused status. | `Boolean` | Yes |
| fps | Gets detected frames per seconds. Only available when using Hls.js. | `Number` | Yes |
| status | Gets current playback status. | `String` | Yes |
| bandwidth | Gets detected bandwidth. Only available when using Hls.js. | `Number` | Yes |
| level | Gets curent level. Only available when using Hls.js. | `Number` | Yes |
| edge | Gets detected current edge server beeing used. Only available when using Hls.js. | `String` | Yes |
| bitrate | Gets detected bitrate. Only available when using Hls.js.| `Number` | Yes |
| duration | Gets video/audio duration in seconds. | `Number` | Yes |
| autoplay | Gets autoplay settings. | `Boolean` | Yes |
| type | Gets current player type. Can be one of `audio`, `video`. | `String` | Yes |
| ended | Get if video/audio has ended. | `Boolean` | Yes |
| sourceType | Gets detected source type. (Ex: `hls` or `mp4`). | `String` | Yes |
| isLive | Gets if content being played is live. | `Boolean` | Yes |
| isDVR | Gets if content being played is DVR. | `Boolean` | Yes |
| readyState | Gets player readyState. | `Number` | Yes |
| seekable |  Returns a TimeRanges object representing the seekable parts of the audio/video. | `Object` | Yes |

## [Methods](#usage-methods)

| Method | Description |
| --- | --- |
| play | Request for the current media to start playing. Returns a promise that will resolve when playing request is successful or reject otherwise. |
| pause | Request for the currently playing media to be paused. |
| load | Load new content dinamically. |
| addToPlaylist | Add new content to playlist queue. |
| destroy | Destroys current player API instance. |
| on | Ads an event listener that. |
| once | Ads an event listener that will fire only **once**. |
| off | Removes an event listener. |

### load options

| Option | Required | Description |
| --- | --- | --- |
| type | Yes | Media type. Possible values: `media`, `episode`, `live`, `dvr` |
| id | Yes | Media, episode or live id. |

### addToPlaylist options

| Option | Required | Description |
| --- | --- | --- |
| type | Yes | Media type. Possible values: `media`, `episode` |
| id | Yes | Media or episode id. |

## [Events](#usage-events)

Events are triggered to inform when things change. To listen to an event you can use either the `on` or `once` methods, and you use the `off` method to remove an event listener. The event listeners will behave exactly like the native event listener does.

Events are grouped in three sections: Custom Events, HTML 5 Events and Ad events.

```javascript
// will call onReady function every time the event is fired
player.on('ready', onReady)

function onReady () {
  console.log('player ready')
  // Removes the onReady event handler
  player.off('ready', onReady)
}

// Will fire once and never again
player.once('canplay', function onCanPlay () {
  player.play()
})
```

#### [Custom Events](#usage-custom-events)

Custom events are non standard events that are fired by the player API and are not part of the HTML 5 standard or have been overloaded.

| Event name | Description |
| --- | --- |
| loaded | Fires when the API has finished loading everything it needs to initialize. |
| sourcechange | Fires when a new source is passed to the player. |
| error | Fires when an error is detected. |
| ready | Fires when player API is ready to be used. |
| buffering | Fires when player has no buffer ahead to continue playing and is requesting some. |
| programdatetime | Fires when live manifests inform program date time. |
| adblockerDetected | Fires when Adblocker detection is enabled and an adblocker is detected. |
| share | Fires when social share button is clicked. |

#### [HTML 5 Events](#usage-html5-events)

These HTML 5 standard events are proxied through the event emitter.

| Event name | Description |
| --- | --- |
| abort | Fires when the loading of an audio/video is aborted |
| canplay | Fires when the browser can start playing the audio/video |
| canplaythrough | Fires when the browser can play through the audio/video without stopping for buffering |
| durationchange | Fires when the duration of the audio/video is changed |
| emptied | Fires when the current playlist is empty |
| ended | Fires when the current playlist has ended |
| loadeddata | Fires when the browser has loaded the current frame of the audio/video |
| loadedmetadata | Fires when the browser has loaded meta data for the audio/video |
| loadstart | Fires when the browser starts looking for the audio/video |
| pause | Fires when the audio/video has been paused |
| play | Fires when the audio/video has been started or is no longer paused |
| playing | Fires when the audio/video is playing after having been paused or stopped for buffering |
| progress | Fires when the browser is downloading the audio/video |
| ratechange | Fires when the playing speed of the audio/video is changed |
| seeked | Fires when the user is finished moving/skipping to a new position in the audio/video |
| seeking | Fires when the user starts moving/skipping to a new position in the audio/video |
| stalled | Fires when the browser is trying to get media data, but data is not available |
| suspend | Fires when the browser is intentionally not getting media data |
| timeupdate | Fires when the current playback position has changed |
| volumechange | Fires when the volume has been changed |
| waiting | Fires when the video stops because it needs to buffer the next frame |

#### [Ad Events](#usage-ads-events)

These events are fired for Ad playback life cycle.

| Event name | Description |
| --- | --- |
| adsAdBreakReady | Fired when an ad rule or a VMAP ad break would have played if autoPlayAdBreaks is false |
| adsAdMetadata | Fired when an ads list is loaded |
| adsAllAdsCompleted | Fired when the ads manager is done playing all the ads |
| adsClick | Fired when the ad is clicked |
| adsComplete | Fired when the ad completes playing |
| adsContentPauseRequested | Fired when content should be paused. This usually happens right before an ad is about to cover the content |
| adsContentResumeRequested | Fired when content should be resumed. This usually happens when an ad finishes or collapses |
| adsDurationChange | Fired when the ad's duration changes |
| adsFirstQuartile | Fired when the ad playhead crosses first quartile |
| adsImpression | Fired when the impression URL has been pinged |
| adsLinearChanged | Fired when the displayed ad changes from linear to nonlinear, or vice versa |
| adsLoaded | Fired when ad data is available |
| adsLog | Fired when a non-fatal error is encountered. The user need not take any action since the SDK will continue with the same or next ad playback depending on the error situation |
| adsMidpoint | Fired when the ad playhead crosses midpoint |
| adsPaused | Fired when the ad is paused |
| adsResumed | Fired when the ad is resumed |
| adsSkippableStateChanged | Fired when the displayed ads skippable state is changed |
| adsSkipped | Fired when the ad is skipped by the user |
| adsStarted | Fired when the ad starts playing |
| adsThirdQuartile | Fired when the ad playhead crosses third quartile |
| adsTimeUpdate | Fired while ad is playing to indicate current time position has changed. |
| adsUserClose | Fired when the ad is closed by the user |
| adsVolumeChanged |Fired when the ad volume has changed |
| adsVolumeMuted | Fired when the ad volume has been muted |
| adsError | Fired when an error occurred while the ad was loading or playing |
| adsAdBuffering | Fired when the ad has stalled playback to buffer |
| adsAdProgress | Fired when the ad's current time value changes. Calling getAdData() on this event will return an AdProgressData object |
| adsInteraction | Fired when an ad triggers the interaction callback. Ad interactions contain an interaction ID string in the ad data |
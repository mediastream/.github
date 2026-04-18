# Mediastream Roku SDK

Official Mediastream **SceneGraph** SDK for Roku: VOD, live, episodes, and audio with ads (RAF, Google DAI), optional Widevine DRM, analytics, and localization (EN / ES / PT). This guide matches the **current `master`** of the Mediastream Roku SDK package.

## Version

| Field | Value |
|--------|--------|
| **Semantic version** | **9.0.0** |
| **Package build** | `202604180` (from SDK `manifest`) |
| **Component library ID** | `MediastreamRokuPlayerSDK` |
| **Core node** | `MediaStreamPlayer` (inside the loaded package) |

---

## Capabilities (SDK `master`)

- **Content types:** VOD (`video`), live (`live-stream`), episode, audio (`audio`)
- **Streaming formats:** HLS (default), DASH (`videoFormat` / `src` keys), MP4, MP3
- **Ads:** Roku Ads Framework (VAST) and **Google DAI** (live + VOD where configured)
- **Google DAI live + DASH:** When `videoFormat` is DASH and the Mediastream API returns `ad_insertion_google.asset_key_dash`, the SDK uses that **DASH asset key** for IMA; otherwise it uses `asset_key` (HLS-oriented key).
- **DRM:** Widevine (`drmData` + `drmToken` for license requests, e.g. Axinom-style headers)
- **Subtitles:** External WebVTT and in-stream EIA-608/708
- **Analytics:** Playback, buffering, errors, device context
- **Up Next:** Next episode flow and overlay hooks
- **Optional:** Nielsen DAR, InTheGame (ITG) interactive ads (when enabled in content/config)

---

## Requirements

Add the following to your channel **`manifest`** (same as the distributable SDK):

```ini
bs_libs_required=roku_ads_lib,googleima3
```

The SDK package may declare **Verimatrix** DRM requirements; include those lines only if your channel uses that stack:

```ini
requires_verimatrix_drm=1
requires_verimatrix_version=1.0
```

| Requirement | Notes |
|-------------|--------|
| Roku OS | SceneGraph (`rsg_version` 1.2+ as in SDK manifest) |
| Libraries | `roku_ads_lib`, `googleima3` (Google IMA DAI) |

---

## Adding the SDK to your project

Download the **`.pkg`** from CDN and host it locally in your project (for example `pkg:/source/packageFile/MediaStreamPlayer.pkg`).

**Latest (floating):**

```text
https://player.cdn.mdstrm.com/roku_sdk/MediaStreamPlayer.pkg
```

**Pinned to 9.0.0:**

```text
https://player.cdn.mdstrm.com/roku_sdk/9.0.0/MediaStreamPlayer.pkg
```

Typical layout: create `source/packageFile/` at the channel root and place `MediaStreamPlayer.pkg` there.

![Adding the Mediastream package](/images/AddingMediastreampkg.png)

---

## Scene and fields (host app)

In your main scene (e.g. `MainScene.xml`), expose fields the sample app uses for status and position, and a container for the player host node:

```xml
<interface>
  <field id="msPlayerVideoStatus" type="assocarray" alwaysNotify="true"/>
  <field id="msPlayerVideoPosition" type="assocarray" alwaysNotify="true"/>
</interface>
<children>
  <Group id="msRokuPlayerContainer" />
</children>
```

The **reference application** in the SDK repo uses a thin **wrapper** component also named `MediaStreamPlayer` that loads the package via `ComponentLibrary` and then creates `mediastreamRokuPlayerSDK:MediaStreamPlayer`. You can copy that pattern or load the library yourself.

---

## Initialization (ComponentLibrary + SDK player)

1. Set the **URI** of the downloaded `.pkg` (local `pkg:` path or HTTPS URL).
2. Create a `ComponentLibrary` node, set `uri`, and wait until `loadStatus` is `"ready"`.
3. Instantiate the SDK player: `CreateObject("roSGNode", "mediastreamRokuPlayerSDK:MediaStreamPlayer")` (prefix must match your loaded library; the sample uses this casing).
4. Set `content` on that node with the config associative array (see below), then call `startPlayback`.

Minimal BrightScript pattern (aligned with the SDK **Example** `MainScene` / wrapper):

```brightscript
function initPlayer()
    m.SDKPath = "pkg:/source/packageFile/MediaStreamPlayer.pkg"
    if m.mediaStreamPlayer <> invalid
        m.msRokuPlayerContainer.removeChild(m.mediaStreamPlayer)
        m.mediaStreamPlayer = invalid
    end if
    m.mediaStreamPlayer = CreateObject("roSGNode", "MediaStreamPlayer") ' wrapper in sample; or your own host
    m.mediaStreamPlayer.id = "mediaStreamPlayer"
    m.mediaStreamPlayer.callFunc("initializeSDK", m.SDKPath)
    m.msRokuPlayerContainer.appendChild(m.mediaStreamPlayer)
    m.mediaStreamPlayer.observeField("SDKStatus", "onSDKStatusChanged")
end function
```

After `SDKStatus` reports **Loaded**, the sample calls into the wrapper’s `setupPlayer(playerType)` which builds `content` and assigns it to the real SDK node, then `startPlayback`. If you talk to the **SDK node directly**, use:

- `getMediaPlayerConfig()` — returns `msConfig` (defaults + enums).
- Populate `msConfig.mediaStreamPlayerConfig` (or the flat structure your merge uses) with `id`, `type`, `environmentType`, etc.
- Set `player.content = config` and `player.callFunc("startPlayback")`.

---

## Configuration overview

The **`MediastreamPlayerConfig`** shape is documented in detail in the SDK repository [README.md — configuration reference](https://github.com/mediastream/MediastreamPlatformSDKRokuTV/blob/master/README.md#configuration-reference). Summary:

### Required for playback via Mediastream API

- **`id` (string):** Content ID from Mediastream Platform.
- **`type` (string):** One of `video`, `live-stream`, `episode`, `audio`.
- **`environmentType`:** Use your `msConfig.environmentType.PRODUCTION` or `msConfig.environmentType.DEV` (maps to production vs develop API hosts).

### Common optional fields

- **`accessToken`:** For protected / entitlements flows.
- **`videoFormat`:** e.g. `msConfig.audioVideoFormat.DASH` for DASH (`mpd`); default is HLS.
- **`adUrl`:** Client-side VAST; platform ads apply when omitted (per SDK behavior).
- **`appName` / `appVersion`:** Analytics and ad tagging.
- **`startAt`:** Start position (e.g. continue watching).
- **`dvr` / `windowDvr` / `dvrStart` / `dvrEnd`:** Live DVR window parameters when supported.
- **Google DAI:** Values are usually filled from the **Mediastream stream API** (`ad_insertion_google`). For live **DASH + DAI**, ensure `asset_key_dash` is present in the API payload when using DASH; the SDK maps it to `google_dai_assetKeyDash` and selects it when `videoFormat` is DASH.
- **DRM:** `drmData` (Widevine `serverURL` under `widevine`) and `drmToken` as required by your DRM provider (SDK sends the token in the license request path it implements).

### Debug / measurement

- **`setAdsDebugOutput`**, **`setAdMeasurements`**, **`setJITPods`**
- **Nielsen DAR:** `enableNielsenDAR`, `nielsenAppId` (string), `nielsenProgramId`, `contentGenres`

---

## Event handling (host scene)

Observe SDK status on your host node, and player status on the scene fields you defined:

```brightscript
sub setObservers()
    m.top.observeField("msPlayerVideoPosition", "onMsPlayerVideoPositionChanged")
    m.top.observeField("msPlayerVideoStatus", "onMsPlayerVideoStatusChanged")
end sub

sub onMsPlayerVideoStatusChanged(eventData as dynamic)
    print "MainScene : onMsPlayerVideoStatusChanged "; eventData.getData()
end sub

sub onMsPlayerVideoPositionChanged(eventData as dynamic)
    print "MainScene : onMsPlayerVideoPositionChanged "; eventData.getData()
end sub

sub onSDKStatusChanged(event as dynamic)
    payload = event.getData()
    print "MainScene : onSDKStatusChanged : "; payload
    if payload.status = "Loaded" then m.isSDKLoaded = true
end sub
```

---

## Player events (`addEventListener` on SDK `MediaStreamPlayer`)

Subscribe with the SDK node’s `callFunc("addEventListener", eventName, handlerName, contextNode)` (see SDK README for the exact signature). Events include:

| Event | When |
|--------|------|
| `playing` | Playback started or resumed |
| `paused` | Paused |
| `stopped` | Stopped |
| `buffering` | Buffering |
| `timeupdate` | Periodic time / duration updates |
| `error` | Error |
| `ended` | Content finished |
| `bitratechange` | ABR ladder change |
| `seeked` | Seek completed |

---

## Example project

The **[Mediastream Roku SDK sample](https://github.com/mediastream/MediastreamPlatformSDKRokuTV)** (`Example/` in the same repository as the SDK) demonstrates VOD, live, episode, AOD, and player-type–specific config under `Example/components/controls/MediaPlayerConfig/`. Use your own **account** and **content IDs** from Mediastream Platform.

[Sample documentation](/roku/MediastreamRokuSample)

---

## Support

For the latest API surface, configuration keys, and troubleshooting, keep this doc in sync with **`README.md`** and **`SDK/manifest`** on **`master`** in [MediastreamPlatformSDKRokuTV](https://github.com/mediastream/MediastreamPlatformSDKRokuTV).

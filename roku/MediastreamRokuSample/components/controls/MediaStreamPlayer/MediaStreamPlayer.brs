sub Init()
    print "MediaStreamPlayer : Init"
    setLocals()
    setObservers()
end sub

sub setLocals()
    m.scene = m.top.GetScene()
end sub

sub setObservers()
    m.top.observeField("focusedChild", "onFocusedChildChange")
end sub

function initializeSDK(msPlayerSDKPath as string)
    print "MediaStreamPlayer : initializeSDK : " msPlayerSDKPath
    if msPlayerSDKPath <> invalid and msPlayerSDKPath <> ""
        m.mediastreamRokuPlayerSDK = CreateObject("roSGNode", "ComponentLibrary")
        m.mediastreamRokuPlayerSDK.observeField("loadStatus", "mediastreamRokuPlayerSDKLoaded")
        m.mediastreamRokuPlayerSDK.uri = msPlayerSDKPath
    else
        m.top.SDKStatus = { status: "Error", message: "Please provide valid SDK package file path." }
    end if
end function

function mediastreamRokuPlayerSDKLoaded()
    print "MediaStreamPlayer : mediastreamRokuPlayerSDKLoaded : loadStatus : " m.mediastreamRokuPlayerSDK.loadStatus
    status = m.mediastreamRokuPlayerSDK.loadStatus
    if (m.mediastreamRokuPlayerSDK.loadStatus = "ready")
        m.top.SDKStatus = { status: "Loaded", message: "SDK Loaded Successfully" }
    else
        m.top.SDKStatus = { status: status, message: "SDK " + status }
    end if
end function

function startPlayback()
    print "MediaStreamPlayer : mediastreamRokuPlayerSDKLoaded : loadStatus : " m.mediastreamRokuPlayerSDK.loadStatus
    if (m.mediastreamRokuPlayerSDK.loadStatus = "ready" and m.mediaStreamVideoPlayer <> invalid)
        m.mediaStreamVideoPlayer.callFunc("startPlayback")
    else
        m.top.SDKStatus = { status: "Error", message: "SDK not loaded or MediaStream player not initialized." }
    end if
end function

function initializeSDKVideoPlayer()
    if (m.top.SDKStatus <> invalid and m.top.SDKStatus.status = "Loaded")
        m.mediaStreamVideoPlayer = CreateObject("roSGNode", "mediastreamRokuPlayerSDK:MediaStreamPlayer")
        m.mediaStreamVideoPlayer.id = "mediaStreamVideoPlayer"
        m.top.appendChild(m.mediaStreamVideoPlayer)
        m.mediaStreamVideoPlayer.observeField("infoSDKLog", "onSDKInfoLog")
        m.mediaStreamVideoPlayer.observeField("isVideoFinished", "onStoppedVideoPlayer")
        mediaStreamConfigs = m.mediaStreamVideoPlayer.callFunc("getMediaPlayerConfig")
        m.msConfig = mediaStreamConfigs.msConfig
    else
        print "MediaStreamPlayer : initializeSDKVideoPlayer : SDK yet not ready"
    end if
end function

function setupPlayer(playerType as string) as boolean
    result = false
    if playerType <> invalid
        initializeSDKVideoPlayer()
        config = m.msConfig.mediaStreamPlayerConfig
        addEventListeners()
        if playerType = "vodPlayer"
            configData = getVODPlayerConfig(config, m.msConfig)
        else if playerType = "liveVideo"
            configData = getLiveVideoConfig(config, m.msConfig)
        else
            removeEventListeners()
        end if
        if not configData.isEmpty()
            m.mediaStreamVideoPlayer.content = configData
            result = true
        else
            print "MediaStreamPlayer : setupPlayer : Please set first valid config data"
        end if
    end if
    return result
end function

function onSDKInfoLog(event as dynamic)
    eventData = event.getData()
    if eventData.status = "success"
        print "MediaStreamPlayer : onSDKInfoLog : event : " eventData
    else
        print "MediaStreamPlayer : onSDKInfoLog : event : " eventData
        m.top.setFocus(true)
    end if
end function

function addEventListeners()
    print "MediaStreamPlayer : addEventListener : m.msConfig : " m.msConfig
    if m.mediaStreamVideoPlayer <> invalid and m.msConfig <> invalid
        m.mediaStreamVideoPlayer.callFunc("addEventListener", m.msConfig.event.BUFFERING, "eventCallBackHandler", m.top)
        m.mediaStreamVideoPlayer.callFunc("addEventListener", m.msConfig.event.PLAYING, "eventCallBackHandler", m.top)
        m.mediaStreamVideoPlayer.callFunc("addEventListener", m.msConfig.event.PAUSED, "eventCallBackHandler", m.top)
        m.mediaStreamVideoPlayer.callFunc("addEventListener", m.msConfig.event.STOPPED, "eventCallBackHandler", m.top)
        m.mediaStreamVideoPlayer.callFunc("addEventListener", m.msConfig.event.ERROR, "eventCallBackHandler", m.top)
        m.mediaStreamVideoPlayer.callFunc("addEventListener", m.msConfig.event.ENDED, "eventCallBackHandler", m.top)
        m.mediaStreamVideoPlayer.callFunc("addEventListener", m.msConfig.event.TIMEUPDATE, "callbackOnEventPlayerTimeupdate", m.top)
    end if
end function

function removeEventListeners()
    print "MediaStreamPlayer : removeEventListeners : m.msConfig : " m.msConfig
    if m.mediaStreamVideoPlayer <> invalid and m.msConfig <> invalid
        m.mediaStreamVideoPlayer.callFunc("removeEventListener", m.msConfig.event.BUFFERING, "eventCallBackHandler", m.top)
        m.mediaStreamVideoPlayer.callFunc("removeEventListener", m.msConfig.event.PLAYING, "eventCallBackHandler", m.top)
        m.mediaStreamVideoPlayer.callFunc("removeEventListener", m.msConfig.event.PAUSED, "eventCallBackHandler", m.top)
        m.mediaStreamVideoPlayer.callFunc("removeEventListener", m.msConfig.event.STOPPED, "eventCallBackHandler", m.top)
        m.mediaStreamVideoPlayer.callFunc("removeEventListener", m.msConfig.event.ERROR, "eventCallBackHandler", m.top)
        m.mediaStreamVideoPlayer.callFunc("removeEventListener", m.msConfig.event.ENDED, "eventCallBackHandler", m.top)
        m.mediaStreamVideoPlayer.callFunc("removeEventListener", m.msConfig.event.TIMEUPDATE, "callbackOnEventPlayerTimeupdate", m.top)
    end if
end function

function eventCallBackHandler(eventData as dynamic)
    print "MediaStreamPlayer : eventCallBackHandler : eventData : " eventData
    m.scene.msPlayerVideoStatus = eventData
end function

function callbackOnEventPlayerTimeupdate(eventData as dynamic)
    m.scene.msPlayerVideoPosition = eventData
end function

sub onStoppedVideoPlayer()
    print "MediaStreamPlayer : onStoppedVideoPlayer"
    if (m.mediaStreamVideoPlayer <> invalid and m.mediaStreamVideoPlayer.isVideoFinished)
        destory()
        m.scene.isSDKTaskFinished = true
    end if
end sub

sub destory()
    print "MediaStreamPlayer : destory"
    removeEventListeners()
    m.top.removeChild(m.mediaStreamVideoPlayer)
    m.mediaStreamVideoPlayer = invalid
end sub

function onFocusedChildChange()
    print "MediaStreamPlayer : onFocusedChildChange"
    if not m.top.hasFocus()
    end if
end function

function onKeyEvent(key as string, press as boolean) as boolean
    result = false
    if (press)
        print "MediaStreamPlayer : onKeyEvent : key = " + key + " press = " + press.ToStr()
        if (key = "up" or key = "down")
        else if (key = "back") then
            destory()
            result = false
        else if (key = "play" or key = "OK") then
            result = true
        else if (key = "replay") then
            result = true
        else if ((key = "fastforward" or key = "rewind" or key = "left" or key = "right")) then
            result = true
        end if
    end if
    return result
end function

function Init()
    print "MainScene : Init"
    m.isSDKLoaded = false
    setControls()
    setObservers()
    createBusySpinner()
    initialize()
end function

sub setControls()
    m.mediaStreamLableList = m.top.findNode("mediaStreamLableList")
    m.msRokuPlayerContainer = m.top.findNode("msRokuPlayerContainer")
    m.loader = m.top.findNode("loader")
end sub

sub setObservers()
    m.top.observeField("focusedChild", "onFocusedChildChange")
    m.top.observeField("msPlayerVideoPosition", "onMsPlayerVideoPositionChanged")
    m.top.observeField("msPlayerVideoStatus", "onMsPlayerVideoStatusChanged")
    m.top.observeField("isSDKTaskFinished", "OnSDKTaskFinished")
    m.mediaStreamLableList.observeField("itemSelected", "onItemSelected")
end sub

sub createBusySpinner()
    m.loader.poster.uri = "pkg:/images/Loader100px.png"
    m.loader.poster.width = 100
    m.loader.poster.height = 100
    m.loader.visible = false
end sub

sub initialize()
    initPlayer()
    m.mediaStreamLableList.visible = true
    m.mediaStreamLableList.setFocus(true)
end sub

function onFocusedChildChange()
    print "MainScene : onFocusedChildChange"
    if m.top.hasFocus() and m.mediaStreamLableList <> invalid
        m.mediaStreamLableList.setFocus(true)
    end if
end function

function onItemSelected(event as dynamic)
    index = event.getData()
    print "MainScene : onItemSelected : index : " index
    item = m.mediaStreamLableList.content.getChild(index)
    if item <> invalid
        print "MainScene : onItemSelected : PlayerType : " item.id
        if m.mediaStreamPlayer <> invalid and m.isSDKLoaded
            isMsPlayerConfigured = m.mediaStreamPlayer.callFunc("setupPlayer", item.id)
            if (isMsPlayerConfigured)
                m.mediaStreamPlayer.callFunc("startPlayback")
                m.mediaStreamLableList.visible = false
            end if
        else
            print "MainScene : onItemSelected : SDK not loaded yet"
        end if
    else
        print "MainScene : onItemSelected : No item selected"
    end if
end function


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

function onSDKStatusChanged(event as dynamic)
    payload = event.getData()
    print "MainScene : onSDKStatusChanged : payload : " payload
    if payload.status = "Loaded" then m.isSDKLoaded = true
end function

function onMsPlayerVideoStatusChanged(eventData as dynamic)
    event = eventData.getData()
    print "MainScene : onMsPlayerVideoStatusChanged " event
    if (event.status = "finished")
        showPlayerList()
    end if
end function

function OnSDKTaskFinished(eventData as dynamic)
    event = eventData.getData()
    print "MainScene : OnSDKTaskFinished " event
    if (event = true)
        showPlayerList()
    end if
end function

function onMsPlayerVideoPositionChanged(eventData as dynamic)
    print "MainScene : onMsPlayerVideoPositionChanged " eventData.getData()
end function

sub showPlayerList()
    print "MainScene : showPlayerList"
    if m.mediaStreamLableList <> invalid
        m.mediaStreamLableList.visible = true
        m.mediaStreamLableList.setFocus(true)
    end if
end sub

function onKeyEvent(key as string, press as boolean) as boolean
    result = false
    if (press)
        print "MainScene : onKeyEvent : key = " + key + " press = " + press.ToStr()
        if (key = "up" or key = "down")
        else if (key = "back")
            if m.mediaStreamPlayer <> invalid
                m.mediaStreamPlayer.callFunc("destory")
            end if
            showPlayerList()
            result = true
        end if
    end if
    return result
end function

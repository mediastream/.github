package tv.independentwrestling.app.bottomnav

import am.mediastre.mediastreamsampleapp.R


sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: Int,
    val icon_focused: Int
) {

    object Audio: BottomBarScreen(
        route = "audio",
        title = "Audio",
        icon = R.drawable.ic_audio,
        icon_focused = R.drawable.ic_audio_focused
    )

    object Video: BottomBarScreen(
        route = "video",
        title = "Video",
        icon = R.drawable.ic_vod,
        icon_focused = R.drawable.ic_vod_focused
    )
}

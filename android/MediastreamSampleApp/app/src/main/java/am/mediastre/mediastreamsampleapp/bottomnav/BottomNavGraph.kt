package am.mediastre.mediastreamsampleapp.bottomnav

import am.mediastre.mediastreamsampleapp.main.AudioScreen
import am.mediastre.mediastreamsampleapp.main.VideoScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import tv.independentwrestling.app.bottomnav.BottomBarScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Audio.route
    ) {
        composable(route = BottomBarScreen.Audio.route) {
            AudioScreen()
        }
        composable(route = BottomBarScreen.Video.route) {
            VideoScreen()
        }
    }
}
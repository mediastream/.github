package am.mediastre.mediastreamsampleapp.main

import am.mediastre.mediastreamsampleapp.R
import am.mediastre.mediastreamsampleapp.bottomnav.BottomNavGraph
import am.mediastre.mediastreamsampleapp.ui.theme.Material3ComposeTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import tv.independentwrestling.app.bottomnav.BottomBarScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Material3ComposeTheme {
                MainView(this@MainActivity)
            }
        }
    }
}

@Composable
fun MainView(activity: Activity) {
    Column(
        modifier = Modifier.fillMaxSize(),
        content = {
            BottomNav(activity)
        }
    )
}

@Composable
fun BottomNav(requireActivity: Activity) {
    val navController = rememberNavController()

    val backgroundImage = painterResource(id = R.drawable.background_image) // Replace with your image resource
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Content (Scaffold)
        Scaffold(
            bottomBar = { BottomBar(navController = navController, requireActivity = requireActivity) },
            backgroundColor = Color.Transparent // Set the background color to Transparent
        ){
            Modifier.padding(it)
            BottomNavGraph(
                navController = navController
            )
        }

        // Top center image with 50.dp height
        Image(
            painter = painterResource(id = R.drawable.ic_media_sdk_stream),
            contentDescription = null,
            modifier = Modifier
                .wrapContentWidth()
                .height(50.dp)
                .padding(top = 20.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun BottomBar(navController: NavHostController, requireActivity: Activity) {
    val screens = listOf(
        BottomBarScreen.Audio,
        BottomBarScreen.Video
    )

    val navStackBackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navStackBackEntry?.destination

    Row(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
            .background(Color.Transparent)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        screens.forEachIndexed { index, bottomBarScreen ->
            AddItem(
                screen = bottomBarScreen,
                currentDestination = currentDestination,
                navController = navController,
                index = index,
                requireActivity = requireActivity
            )
        }
    }

}

@Composable
fun AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController,
    index: Int = 0,
    requireActivity: Activity
) {
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

    val background =
        if (selected) Color.White else Color.DarkGray

    val contentColor =
        if (selected) Color.DarkGray else Color.White

    val shape = if (index == 0) {
        RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50)
    } else {
        RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
    }

    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(shape)
            .background(background)
            .clickable(onClick = {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            })
    ) {
        Row(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Icon(
                painter = painterResource(id = if (selected) screen.icon_focused else screen.icon),
                contentDescription = "icon",
                tint = contentColor
            )

            AnimatedVisibility(visible = selected) {
                Text(
                    text = screen.title,
                    color = contentColor
                )
            }
        }
    }
}
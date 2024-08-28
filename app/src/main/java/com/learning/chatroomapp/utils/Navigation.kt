package com.learning.chatroomapp.utils

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.learning.chatroomapp.screens.ChatListScreen
import com.learning.chatroomapp.screens.ChatScreen
import com.learning.chatroomapp.screens.SignInScreen
import com.learning.chatroomapp.screens.SignUpScreen
import com.learning.chatroomapp.viewModels.AuthViewModel
import com.learning.chatroomapp.viewModels.RoomViewModel
import kotlinx.coroutines.delay

sealed class Screen(val route:String){
    object LoginScreen:Screen("loginscreen")
    object SignupScreen:Screen("signupscreen")
    object ChatRoomsScreen:Screen("chatroomscreen")
    object ChatScreen:Screen("chatscreen")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    roomViewModel: RoomViewModel
) {
    var backPressedOnce by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler {
        if (backPressedOnce) {
            ActivityCompat.finishAffinity(context as androidx.activity.ComponentActivity)
        } else {
            backPressedOnce = true
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(backPressedOnce) {
        if (backPressedOnce) {
            delay(2000)
            backPressedOnce = false
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route
    ) {
        composableWithSlidingTransitions(Screen.SignupScreen.route) {
            SignUpScreen(
                onNavigateToSignIn = { navController.navigate(Screen.LoginScreen.route) },
                onSignUpSuccess = {
                    navController.navigate(Screen.ChatRoomsScreen.route) {
                        popUpTo(Screen.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                },
                authViewModel = authViewModel
            )
        }
        composableWithSlidingTransitions(Screen.LoginScreen.route){
            SignInScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignupScreen.route) },
                onSignInSuccess = {
                    navController.navigate(Screen.ChatRoomsScreen.route) {
                        popUpTo(Screen.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                },
                authViewModel = authViewModel
            )
        }
        composable("${Screen.ChatScreen.route}/{roomId}") {
            val roomId : String = it.arguments?.getString("roomId") ?: ""
            ChatScreen(roomId = roomId)
        }
        composable(Screen.ChatRoomsScreen.route) {
            ChatListScreen(
                roomViewModel = roomViewModel,
                onJoinRoom = {  navController.navigate("${Screen.ChatScreen.route}/${it.id}") }
            )
        }
    }
}

private fun NavGraphBuilder.composableWithSlidingTransitions(
    route: String,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(700))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(700))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(700))
        },
        content = content
    )
}
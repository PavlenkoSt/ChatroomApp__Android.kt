package com.learning.chatroomapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.learning.chatroomapp.screens.ChatListScreen
import com.learning.chatroomapp.screens.ChatScreen
import com.learning.chatroomapp.screens.SignInScreen
import com.learning.chatroomapp.screens.SignUpScreen
import com.learning.chatroomapp.viewModels.AuthViewModel
import com.learning.chatroomapp.viewModels.RoomViewModel

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
    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(Screen.SignupScreen.route) {
            SignUpScreen(
                onNavigateToSignIn = { navController.navigate(Screen.LoginScreen.route) },
                onSignUpSuccess = { navController.navigate(Screen.ChatRoomsScreen.route) },
                authViewModel = authViewModel
            )
        }
        composable(Screen.LoginScreen.route) {
            SignInScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignupScreen.route) },
                onSignInSuccess = { navController.navigate(Screen.ChatRoomsScreen.route) },
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
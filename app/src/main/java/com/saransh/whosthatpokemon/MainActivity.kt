package com.saransh.whosthatpokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.saransh.whosthatpokemon.ui.theme.WhosThatPokemonTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhosThatPokemonTheme {
                val navController = rememberNavController()
                val client = HttpClient(Android) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true })
                    }
                }
                val context = LocalContext.current
                Scaffold(modifier = Modifier.fillMaxSize() , bottomBar = {
                    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                    if (currentRoute == Home.route || currentRoute == Settings.route) {
                        BottomNavBar(navController)
                    } })
                { innerPadding ->
                    NavHost(navController = navController, startDestination = Home.route)
                    {
                        composable(Home.route)
                        {
                            HomeScreen(innerPadding,navController,context)
                        }
                        composable(Easy.route)
                        {
                            Quiz("Easy",client,context,navController)
                        }
                        composable(Medium.route)
                        {
                            Quiz("Medium",client,context,navController)
                        }
                        composable(Hard.route)
                        {
                            Quiz("Hard",client,context,navController)
                        }
                        composable(VeryHard.route)
                        {
                            Quiz("Very Hard",client,context,navController)
                        }
                        composable(Settings.route)
                        {
                            Settings(context,innerPadding)
                        }
                    }
                }
            }
        }
    }
}



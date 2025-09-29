package com.saransh.whosthatpokemon

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(innerPadding: PaddingValues, navController: NavHostController, context: Context) {
    // Example state for scores (later replace with SharedPreferences/Room DB)
    val scores = context.getSharedPreferences("scores", Context.MODE_PRIVATE)
    var easyHighScore by remember { mutableStateOf(scores.getInt("Easy",0))  }
    var mediumHighScore by remember { mutableStateOf(scores.getInt("Medium",0)) }
    var hardHighScore by remember { mutableStateOf(scores.getInt("Hard",0)) }

    Surface(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pokémon Quiz",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            DifficultyButton("Easy", easyHighScore) {
                navController.navigate(Easy.route)
            }

            Spacer(modifier = Modifier.height(24.dp))

            DifficultyButton("Medium", mediumHighScore) {
                navController.navigate(Medium.route)
            }

            Spacer(modifier = Modifier.height(24.dp))

            DifficultyButton("Hard", hardHighScore) {
                navController.navigate(Hard.route)
            }
        }
    }
}

@Composable
fun DifficultyButton(level: String, score: Int, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            Text(
                text = level,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "High Score: $score",
            fontSize = 16.sp
        )
    }
}
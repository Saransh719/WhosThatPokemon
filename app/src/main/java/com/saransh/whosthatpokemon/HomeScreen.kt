package com.saransh.whosthatpokemon

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(innerPadding: PaddingValues, navController: NavHostController, context: Context) {
    val scores = context.getSharedPreferences("scores", Context.MODE_PRIVATE)
    var showDisclaimer by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monster Quiz",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showDisclaimer = true}) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Disclaimer",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = "Complete a difficulty with a score of 10 to unlock the next one.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = TextAlign.Center
            )

            DifficultyButton("Easy",scores,enabled = true) {
                navController.navigate(Easy.route)
            }

            Spacer(modifier = Modifier.height(24.dp))

            DifficultyButton("Medium",scores,enabled = scores.getInt("Easy", 0) == 10) {
                navController.navigate(Medium.route)
            }

            Spacer(modifier = Modifier.height(24.dp))

            DifficultyButton("Hard", scores, enabled = scores.getInt("Medium", 0) == 10) {
                navController.navigate(Hard.route)
            }
            Spacer(modifier = Modifier.height(24.dp))

            DifficultyButton("Very Hard", scores, enabled = scores.getInt("Hard", 0) == 10) {
                navController.navigate(VeryHard.route)
            }
        }
    }
    if (showDisclaimer) {
        AlertDialog(
            onDismissRequest = { showDisclaimer = false },
            title = {
                Text(
                    text = "Disclaimer",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = """
                            This is a fan-made project.
                            This app is not affiliated with, endorsed by, or in any way officially connected to Nintendo, Game Freak, The Pokémon Company, or any of their subsidiaries or affiliates. Pokémon, the Pokémon logo, and all related names, characters, and images are trademarks of their respective owners.

                            The content in this app is created for educational and entertainment purposes only. All Pokémon data (names, stats, abilities, and images) are provided by the PokéAPI and are used in accordance with their terms of service. All Pokémon images and content are property of their respective copyright holders.

                            We do not claim ownership of any Pokémon-related intellectual property. If you are the copyright holder of any content used in this app and would like it removed, please contact us, and we will promptly comply with your request.
                            Email- saranshsapra08@gmail.com
                        """.trimIndent(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDisclaimer = false }) {
                    Text(
                        text = "Close",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun DifficultyButton(
    level: String,
    scores: SharedPreferences,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            enabled = enabled
        ) {
            Text(
                text = level,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "High Score: ${scores.getInt(level, 0)}",
            fontSize = 16.sp
        )
    }
}

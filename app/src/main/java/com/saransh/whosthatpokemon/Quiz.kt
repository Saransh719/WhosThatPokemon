package com.saransh.whosthatpokemon

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.serialization.Serializable
import kotlin.random.Random

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Quiz(
    difficulty: String,
    client: HttpClient,
    context: Context,
    navController: NavHostController,
) {
    var currentQuestion by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }
    val totalQuestions = 10
    var pokemon by remember { mutableStateOf<PokemonResponse?>(null) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isGameOver by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var options by remember { mutableStateOf<Set<String>>(emptySet()) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    val isDarkTheme = isSystemInDarkTheme()

    val prefs = context.getSharedPreferences("gen",Context.MODE_PRIVATE)
    val selections = (1..9).associateWith { prefs.getBoolean("$it", true) }
    val genRanges = mapOf(
        1 to 1..151,
        2 to 152..251,
        3 to 252..386,
        4 to 387..494,
        5 to 495..649,
        6 to 650..721,
        7 to 722..809,
        8 to 810..905,
        9 to 906..1025
    )
    val valid_gen = selections.filter{it.value}.keys
    LaunchedEffect(currentQuestion) {
        isLoading = true
        selectedAnswer = null
        feedbackMessage = null
        options = emptySet()
        try {
            val selected_gen_range = genRanges[valid_gen.random()]
            // Fetch the correct Pokémon
            val randomId = selected_gen_range?.let { Random.nextInt(it.first,it.last ) }
            val correctPokemon = randomId?.let { getPokemon(it, client) }
            pokemon = correctPokemon

            // Generate options
            val optionSet = mutableSetOf<String>()
            correctPokemon?.name?.let{optionSet.add(it)}
            while (optionSet.size < 4) {
                val wrongId = selected_gen_range?.let { Random.nextInt(it.first,it.last ) }
                if (wrongId != randomId) {
                    val wrongPokemon = wrongId?.let { getPokemon(it, client) }
                    wrongPokemon?.name?.let { optionSet.add(it) }
                }

            }
            options = optionSet.shuffled().toSet() // Shuffle and convert to Set
        } catch (e: Exception) {
            options = emptySet()
        } finally {
            isLoading = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isGameOver) {
            saveHighScore(context,difficulty,score)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Game Over!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Score: $score / $totalQuestions",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = when {
                        score == 10 -> "Excellent! You're a Pokémon Master!"
                        score >= 7 -> "Good effort! Keep training!"
                        else -> "Try again to catch 'em all!"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Button(
                    onClick = {
                        currentQuestion = 1
                        score = 0
                        isGameOver = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(text = "Restart Quiz", fontSize = 18.sp)
                }
                Button(
                    onClick = {navController.navigate(Home.route){
                        popUpTo(Home.route) { inclusive = true }
                    } },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(text = "Go Back to Home Screen", fontSize = 18.sp)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Question $currentQuestion / $totalQuestions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Pokémon image
                if (isLoading) {
                    Text(text = "Loading...", fontSize = 18.sp)
                } else if (pokemon != null) {
                    GlideImage(
                        model = pokemon?.sprites?.front_default,
                        contentDescription = "Pokemon Image",
                        modifier = Modifier.size(200.dp).padding(bottom = 24.dp),
                        requestBuilderTransform = {
                            if (difficulty.lowercase() == "medium") {
                                it.transform(BlurTransformation(5, 1))
                            } else {
                                it
                            }
                        },
                        colorFilter = when (difficulty.lowercase()) {
                            "hard","very hard" -> ColorFilter.tint(
                                if (isDarkTheme) Color.White else Color.Black,
                                androidx.compose.ui.graphics.BlendMode.SrcIn
                            )
                            else -> null
                        }
                    )
                } else {
                    Text(text = "Error loading Pokémon", fontSize = 18.sp)
                }

                // Answer choices
                options.forEach { option ->
                    Button(
                        onClick = {
                            selectedAnswer = option
                            val isCorrect = option == (pokemon?.name ?: "")
                            val message = if (isCorrect) {
                                score++
                                "Correct!"
                            } else {
                                "Incorrect! The answer was ${pokemon?.name}"
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (currentQuestion == totalQuestions) {
                                isGameOver = true
                            } else {
                                currentQuestion++
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        enabled = selectedAnswer == null && !isLoading // Disable during loading or after selection
                    ) {
                        val displayText = if (difficulty.lowercase() == "very hard") {
                            scrambleSentence(option)
                        } else {
                            option
                        }
                        Text(text = displayText.capitalize(), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Serializable
data class PokemonResponse(
    val name: String,
    val sprites: Sprites
)
@Serializable
data class Sprites(
    val front_default: String
)

suspend fun getPokemon(id: Int, client: HttpClient): PokemonResponse {
    return client.get("https://pokeapi.co/api/v2/pokemon/$id").body()
}

@SuppressLint("SuspiciousIndentation")
fun saveHighScore(context: Context, difficulty: String, score: Int) {
    val scores = context.getSharedPreferences("scores", Context.MODE_PRIVATE)
    val editor = scores.edit()
    if (scores.getInt(difficulty,0) < score)
    editor.putInt(difficulty, score)
    editor.apply()
}

fun scrambleWord(word: String): String {
    if (word.length <= 3) return word // too short to scramble
    val middle = word.substring(1, word.length - 1).toCharArray()
    middle.shuffle(Random(System.currentTimeMillis()))
    return "${word.first()}${middle.concatToString()}${word.last()}"
}

fun scrambleSentence(sentence: String): String {
    return sentence.split(" ","-").joinToString(" ") { scrambleWord(it) }
}

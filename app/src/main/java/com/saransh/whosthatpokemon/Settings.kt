package com.saransh.whosthatpokemon

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Settings(context: Context, innerPadding: PaddingValues) {

    val prefs = context.getSharedPreferences("gen",Context.MODE_PRIVATE)
    val initialSelections = (1..9).associateWith { prefs.getBoolean("$it", true) }
    var selectedGens by remember { mutableStateOf(initialSelections) }
    val genNames = mapOf(
        1 to "Kanto",
        2 to "Johto",
        3 to "Hoenn",
        4 to "Sinnoh",
        5 to "Unova",
        6 to "Kalos",
        7 to "Alola",
        8 to "Galar",
        9 to "Paldea"
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Text(
                text = "Select Monster Generation(s)",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(9) { index ->
            val gen = index + 1
            val checked = selectedGens[gen] ?: true
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {isChecked ->
                        val currentCheckedCount = selectedGens.values.count { it }
                        if (!isChecked && currentCheckedCount == 1) {
                            // Don’t allow all to be unchecked
                            Toast.makeText(context, "Please select at least one generation", Toast.LENGTH_SHORT).show()
                            return@Checkbox
                        }
                        selectedGens = selectedGens.toMutableMap().apply { put(gen, isChecked) }
                        prefs.edit().putBoolean("$gen", isChecked).apply()
                    }
                )
                Text(text = "Generation $gen (${genNames[gen]})")
            }
        }
    }
}
package com.example.dailyquotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyquotes.ui.theme.DailyQuotesTheme

class ChoicesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val selectedCategories = sharedPreferences.getStringSet("SELECTED_CATEGORIES", setOf())

        if (selectedCategories != null && selectedCategories.size >= 3) {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        setContent {
            DailyQuotesTheme {
                QuoteChoices()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteChoices() {

    val categories = mapOf(
        "age" to "https://api-ninjas.com/api/quotes",
        "alone" to "https://api-ninjas.com/api/quotes",
        "amazing" to "https://api-ninjas.com/api/quotes",
        "anger" to "https://api-ninjas.com/api/quotes",
        "architecture" to "https://api-ninjas.com/api/quotes",
        "art" to "https://api-ninjas.com/api/quotes",
        "attitude" to "https://api-ninjas.com/api/quotes",
        "beauty" to "https://api-ninjas.com/api/quotes",
        "best" to "https://api-ninjas.com/api/quotes",
        "birthday" to "https://api-ninjas.com/api/quotes",
        "business" to "https://api-ninjas.com/api/quotes",
        "car" to "https://api-ninjas.com/api/quotes",
        "change" to "https://api-ninjas.com/api/quotes",
        "communication" to "https://api-ninjas.com/api/quotes",
        "computers" to "https://api-ninjas.com/api/quotes",
        "cool" to "https://api-ninjas.com/api/quotes",
        "courage" to "https://api-ninjas.com/api/quotes",
        "dad" to "https://api-ninjas.com/api/quotes",
        "dating" to "https://api-ninjas.com/api/quotes",
        "death" to "https://api-ninjas.com/api/quotes",
        "design" to "https://api-ninjas.com/api/quotes",
        "dreams" to "https://api-ninjas.com/api/quotes",
        "education" to "https://api-ninjas.com/api/quotes",
        "environmental" to "https://api-ninjas.com/api/quotes",
        "equality" to "https://api-ninjas.com/api/quotes",
        "experience" to "https://api-ninjas.com/api/quotes",
        "failure" to "https://api-ninjas.com/api/quotes",
        "faith" to "https://api-ninjas.com/api/quotes",
        "family" to "https://api-ninjas.com/api/quotes",
        "famous" to "https://api-ninjas.com/api/quotes",
        "fear" to "https://api-ninjas.com/api/quotes",
        "fitness" to "https://api-ninjas.com/api/quotes",
        "food" to "https://api-ninjas.com/api/quotes",
        "forgiveness" to "https://api-ninjas.com/api/quotes",
        "freedom" to "https://api-ninjas.com/api/quotes",
        "friendship" to "https://api-ninjas.com/api/quotes",
        "funny" to "https://api-ninjas.com/api/quotes",
        "future" to "https://api-ninjas.com/api/quotes",
        "god" to "https://api-ninjas.com/api/quotes",
        "good" to "https://api-ninjas.com/api/quotes",
        "government" to "https://api-ninjas.com/api/quotes",
        "graduation" to "https://api-ninjas.com/api/quotes",
        "great" to "https://api-ninjas.com/api/quotes",
        "happiness" to "https://api-ninjas.com/api/quotes",
        "health" to "https://api-ninjas.com/api/quotes",
        "history" to "https://api-ninjas.com/api/quotes",
        "home" to "https://api-ninjas.com/api/quotes",
        "hope" to "https://api-ninjas.com/api/quotes",
        "humor" to "https://api-ninjas.com/api/quotes",
        "imagination" to "https://api-ninjas.com/api/quotes",
        "inspirational" to "https://api-ninjas.com/api/quotes",
        "intelligence" to "https://api-ninjas.com/api/quotes",
        "jealousy" to "https://api-ninjas.com/api/quotes",
        "knowledge" to "https://api-ninjas.com/api/quotes",
        "leadership" to "https://api-ninjas.com/api/quotes",
        "learning" to "https://api-ninjas.com/api/quotes",
        "legal" to "https://api-ninjas.com/api/quotes",
        "life" to "https://api-ninjas.com/api/quotes",
        "love" to "https://api-ninjas.com/api/quotes",
        "marriage" to "https://api-ninjas.com/api/quotes",
        "medical" to "https://api-ninjas.com/api/quotes",
        "men" to "https://api-ninjas.com/api/quotes",
        "mom" to "https://api-ninjas.com/api/quotes",
        "money" to "https://api-ninjas.com/api/quotes",
        "morning" to "https://api-ninjas.com/api/quotes",
        "movies" to "https://api-ninjas.com/api/quotes",
        "success" to "https://api-ninjas.com/api/quotes",
        "inspire" to "https://quotes.rest/qod?category=inspire",
        "management" to "https://quotes.rest/qod?category=management",
        "sports" to "https://quotes.rest/qod?category=sports",
        "students" to "https://quotes.rest/qod?category=students",
        "anxiety" to "https://zenquotes.io/api/random?category=anxiety",
        "courage" to "https://zenquotes.io/api/random?category=courage",
        "failure" to "https://zenquotes.io/api/random?category=failure",
        "freedom" to "https://zenquotes.io/api/random?category=freedom",
        "kindness" to "https://zenquotes.io/api/random?category=kindness",
        "love" to "https://zenquotes.io/api/random?category=love",
        "time" to "https://zenquotes.io/api/random?category=time",
        "change" to "https://zenquotes.io/api/random?category=change",
        "death" to "https://zenquotes.io/api/random?category=death",
        "fairness" to "https://zenquotes.io/api/random?category=fairness",
        "future" to "https://zenquotes.io/api/random?category=future",
        "leadership" to "https://zenquotes.io/api/random?category=leadership",
        "pain" to "https://zenquotes.io/api/random?category=pain",
        "today" to "https://zenquotes.io/api/random?category=today",
        "choice" to "https://zenquotes.io/api/random?category=choice",
        "dreams" to "https://zenquotes.io/api/random?category=dreams",
        "fear" to "https://zenquotes.io/api/random?category=fear",
        "happiness" to "https://zenquotes.io/api/random?category=happiness",
        "life" to "https://zenquotes.io/api/random?category=life",
        "past" to "https://zenquotes.io/api/random?category=past",
        "truth" to "https://zenquotes.io/api/random?category=truth",
        "confidence" to "https://zenquotes.io/api/random?category=confidence",
        "excellence" to "https://zenquotes.io/api/random?category=excellence",
        "forgiveness" to "https://zenquotes.io/api/random?category=forgiveness",
        "inspiration" to "https://zenquotes.io/api/random?category=inspiration",
        "living" to "https://zenquotes.io/api/random?category=living",
        "success" to "https://zenquotes.io/api/random?category=success",
        "work" to "https://zenquotes.io/api/random?category=work"
    )


    var selectedCategories by remember { mutableStateOf<Set<String>>(emptySet()) }
    var visibleCategoriesCount by remember { mutableStateOf(10) }
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()



    val initialCategories = categories.keys.take(visibleCategoriesCount)

    val verticalScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(verticalScrollState),
        contentAlignment = Alignment.Center
    ) {
        Column {
            // Heading
            Text(
                text = "Select Categories (At least 3 must be selected)",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Display categories as InputChips
            initialCategories.chunked(5).forEach { chunk ->
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    chunk.forEach { category ->
                        InputChip(
                            label = { Text(text = category.capitalize(), fontSize = 12.sp) },
                            selected = selectedCategories.contains(category),
                            onClick = {
                                val updatedSelection = if (selectedCategories.contains(category)) {
                                    selectedCategories - category
                                } else {
                                    selectedCategories + category
                                }
                                selectedCategories = updatedSelection
                                showError = updatedSelection.size < 3
                            },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Error message if less than 3 categories are selected
            if (showError) {
                Text(
                    text = "You must select at least 3 categories. This can be changed later.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Button to load more categories
            if (visibleCategoriesCount < categories.size) {
                InputChip(
                    label = { Text(text = "Show More", fontSize = 12.sp) },
                    selected = false,
                    onClick = {
                        visibleCategoriesCount += 10
                    },
                    modifier = Modifier.padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    if (selectedCategories.size < 3) {
                        showError = true
                    } else {
                        showError = false
                        // Save selected categories to SharedPreferences
                        val categoriesSet = selectedCategories.toSet()
                        editor.putStringSet("SELECTED_CATEGORIES", categoriesSet)
                        editor.apply()

                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}

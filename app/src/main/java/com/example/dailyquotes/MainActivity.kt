package com.example.dailyquotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.dailyquotes.ui.theme.DailyQuotesTheme
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import kotlin.math.roundToInt
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyQuotesTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    var quote by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var isFetching by remember { mutableStateOf(true) } // Initially set to true
    var isRefreshing by remember { mutableStateOf(false) } // Flag to prevent multiple fetches
    val scope = rememberCoroutineScope()

    // Function to fetch and set quote
    fun fetchAndSetQuote() {
        scope.launch {
            isFetching = true // Set fetching to true
            val fetchedQuote = fetchQuote()

            // Check if the fetched quote is different from the current one
            if (fetchedQuote.first != quote || fetchedQuote.second != author) {
                quote = fetchedQuote.first
                author = fetchedQuote.second
            }

            isFetching = false // Set fetching to false once done
            isRefreshing = false // Reset refreshing flag
        }
    }

    // LaunchedEffect to fetch quote initially
    LaunchedEffect(Unit) {
        fetchAndSetQuote()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    // Check for downward swipe (dragAmount.y > 0)
                    if (dragAmount.y > 0 && !isRefreshing) { // Check if not already refreshing
                        isRefreshing = true // Set refreshing flag
                        fetchAndSetQuote() // Fetch and set quote
                    }
                }
            }
    ) {
        MainContext(quote, author, isFetching)
    }
}


suspend fun fetchQuote(): Pair<String, String> = withContext(Dispatchers.IO) {
    val categories = listOf(
        "age", "alone", "amazing", "anger", "architecture", "art", "attitude", "beauty", "best",
        "birthday", "business", "car", "change", "communication", "computers", "cool", "courage",
        "dad", "dating", "death", "design", "dreams", "education", "environmental", "equality",
        "experience", "failure", "faith", "family", "famous", "fear", "fitness", "food", "forgiveness",
        "freedom", "friendship", "funny", "future", "god", "good", "government", "graduation", "great",
        "happiness", "health", "history", "home", "hope", "humor", "imagination", "inspirational",
        "intelligence", "jealousy", "knowledge", "leadership", "learning", "legal", "life", "love",
        "marriage", "medical", "men", "mom", "money", "morning", "movies", "success"
    )
    val randomCategory = categories[Random.nextInt(categories.size)]

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.api-ninjas.com/v1/quotes?category=$randomCategory")
        .addHeader("accept", "application/json")
        .addHeader("X-Api-Key", "1nkiJyFpOk3fEyxbf7LrfA==Q0NKhCa3hiJGcdSQ") // Add your API key here
        .build()

    client.newCall(request).execute().use { response ->
        val responseStream: InputStream = response.body?.byteStream() ?: return@withContext Pair("No quote found", "")
        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(responseStream)
        val quoteText = root.path(0).path("quote").asText()
        val quoteAuthor = root.path(0).path("author").asText()
        Pair(quoteText, quoteAuthor)
    }
}

@Composable
fun MainContext(quote: String, author: String, isFetching: Boolean) {
    val fontSize = when {
        quote.length < 50 -> 24.sp
        quote.length < 100 -> 20.sp
        else -> 16.sp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (isFetching) {
                    // Show the Lottie animation
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
                    LottieAnimation(
                        composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Adjust the height as needed
                    )
                } else {
                    // Show the quote content
                    AnimatedVisibility(
                        visible = !isFetching,
                        enter = fadeIn(animationSpec = tween(1000)) + expandVertically(animationSpec = tween(1000)),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = quote,
                                fontWeight = FontWeight.Bold,
                                fontSize = fontSize
                            )

                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "~$author",
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    textAlign = TextAlign.Right
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            SocialMediaButtons()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SocialMediaButtons() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val iconModifier = Modifier.size(24.dp)

            TextButton(onClick = { /* Handle Facebook click */ }) {
                Image(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Facebook",
                    contentScale = ContentScale.Fit,
                    modifier = iconModifier
                )
            }
            TextButton(onClick = { /* Handle Instagram click */ }) {
                Image(
                    painter = painterResource(id = R.drawable.instagram),
                    contentDescription = "Instagram",
                    contentScale = ContentScale.Fit,
                    modifier = iconModifier
                )
            }
            TextButton(onClick = { /* Handle Twitter click */ }) {
                Image(
                    painter = painterResource(id = R.drawable.twitter),
                    contentDescription = "Twitter",
                    contentScale = ContentScale.Fit,
                    modifier = iconModifier
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            val iconModifier = Modifier.size(24.dp)

            TextButton(onClick = { /* Handle Snapchat click */ }) {
                Image(
                    painter = painterResource(id = R.drawable.snapchat),
                    contentDescription = "Snapchat",
                    contentScale = ContentScale.Fit,
                    modifier = iconModifier
                )
            }
            TextButton(onClick = { /* Handle WhatsApp click */ }) {
                Image(
                    painter = painterResource(id = R.drawable.whatsapp),
                    contentDescription = "WhatsApp",
                    contentScale = ContentScale.Fit,
                    modifier = iconModifier
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContextPreview() {
    DailyQuotesTheme {
        MainContext("Be happy for this moment. This moment is your life.", "Omar Khayyam", isFetching = false)
    }
}

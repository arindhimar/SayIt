package com.example.dailyquotes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.platform.LocalContext
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
import kotlin.random.Random



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val selectedCategories = sharedPreferences.getStringSet("SELECTED_CATEGORIES", setOf())

        if (selectedCategories != null && selectedCategories.size >= 3) {
            setContent {
                DailyQuotesTheme {
                    MainContent(selectedCategories.toList())
                }
            }
        } else {
            val intent = Intent(this, ChoicesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

@Composable
fun MainContent(selectedCategories: List<String>) {
    var quote by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var isFetching by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun fetchAndSetQuote() {
        scope.launch {
            isFetching = true
            val fetchedQuote = fetchQuote(selectedCategories)
            if (fetchedQuote.first != quote || fetchedQuote.second != author) {
                quote = fetchedQuote.first
                author = fetchedQuote.second
            }
            isFetching = false
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        fetchAndSetQuote()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    if (dragAmount.y > 0 && !isRefreshing) {
                        isRefreshing = true
                        fetchAndSetQuote()
                    }
                }
            }
    ) {
        MainContext(quote, author, isFetching)
    }
}

suspend fun fetchQuote(categories: List<String>): Pair<String, String> = withContext(Dispatchers.IO) {
    val apiKey = "1nkiJyFpOk3fEyxbf7LrfA==Q0NKhCa3hiJGcdSQ"

    val randomCategory = categories[Random.nextInt(categories.size)]
    val url = "https://api.api-ninjas.com/v1/quotes?category=$randomCategory"

    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .addHeader("accept", "application/json")
        .addHeader("X-Api-Key", apiKey)
        .build()

    client.newCall(request).execute().use { response ->
        val responseStream = response.body?.byteStream() ?: return@withContext Pair("No quote found", "")
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
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
                    LottieAnimation(
                        composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                } else {
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

                            SocialMediaButtons(quote, author)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SocialMediaButtons(quote: String, author: String) {
    val context = LocalContext.current
    val appDownloadLink = "https://github.com/arindhimar/SayIt/raw/main/SampleApp/app-debug.apk"
    val message = "\"$quote\" ~ $author\n\nShared using the SayIt app. Download it here: $appDownloadLink"

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val iconModifier = Modifier.size(24.dp)

            TextButton(onClick = {
                shareOnSocialMedia(
                    context,
                    "https://www.facebook.com/sharer/sharer.php?u=http://example.com&quote=${Uri.encode(message)}"
                )
            }) {
                Image(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Facebook",
                    contentScale = ContentScale.Fit,
                    modifier = iconModifier
                )
            }
            TextButton(onClick = {
                shareOnSocialMedia(
                    context,
                    "https://www.instagram.com/?url=http://example.com&text=${Uri.encode(message)}"
                )
            }) {
                Image(
                    painter = painterResource(id = R.drawable.instagram),
                    contentDescription = "Instagram",
                    contentScale = ContentScale.Fit,
                    modifier = iconModifier
                )
            }
            TextButton(onClick = {
                shareOnSocialMedia(
                    context,
                    "https://twitter.com/intent/tweet?text=${Uri.encode(message)}"
                )
            }) {
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

            TextButton(onClick = {
                shareOnSocialMedia(
                    context,
                    "https://www.snapchat.com?text=${Uri.encode(message)}"
                )
            }) {
                Image(
                    painter = painterResource(id = R.drawable.snapchat),
                    contentDescription = "Snapchat",
                    contentScale = ContentScale.Fit,
                    modifier = iconModifier
                )
            }
            TextButton(onClick = {
                shareOnSocialMedia(
                    context,
                    "https://api.whatsapp.com/send?text=${Uri.encode(message)}"
                )
            }) {
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

fun shareOnSocialMedia(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    val chooserIntent = Intent.createChooser(intent, "Share via")
    context.startActivity(chooserIntent)
}

@Preview(showBackground = true)
@Composable
fun MainContextPreview() {
    DailyQuotesTheme {
        MainContext("Be happy for this moment. This moment is your life.", "Omar Khayyam", isFetching = false)
    }
}

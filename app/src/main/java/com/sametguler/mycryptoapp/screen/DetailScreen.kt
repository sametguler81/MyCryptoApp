package com.sametguler.mycryptoapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sametguler.mycryptoapp.R
import com.sametguler.mycryptoapp.components.AppBar
import com.sametguler.mycryptoapp.service.CryptoAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.sametguler.mycryptoapp.viewmodel.CryptoViewModel
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Composable
fun DetailScreen(id: String, viewModel: CryptoViewModel, navController: NavController) {
    val coinState = viewModel.coin.collectAsState()
    val aiCommentState = viewModel.aiComment.collectAsState()
    val scrollState = rememberScrollState()
    val scrollState2 = rememberScrollState()

    val coin = coinState.value
    val aiComment = aiCommentState.value



    Scaffold(containerColor = colorResource(R.color.AluminumDarkGray)) { innerPadding ->


        if (coin != null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(color = colorResource(R.color.AppBarRenk)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                coin.image?.large?.let { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "coin image",
                        modifier = Modifier
                            .size(75.dp)
                            .padding(5.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column {
                        Text(
                            text = coin.name,
                            modifier = Modifier.padding(0.dp, 10.dp),
                            style = TextStyle(color = Color.White, fontSize = 20.sp)
                        )
                        Text(
                            text = "${coin.marketData.currentPrice.get("usd").toString()} $",
                            modifier = Modifier.padding(0.dp, 10.dp),
                            style = TextStyle(color = Color.White, fontSize = 20.sp)
                        )
                    }

                    Text(
                        text = "Son 24 saat: %${coin.marketData.marketCapChangePercentage24h}",
                        modifier = Modifier.padding(0.dp, 10.dp),
                        style = TextStyle(color = Color.White, fontSize = 20.sp)
                    )

                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.AppBarRenk)
                    ),
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            1.dp, color = colorResource(R.color.Gold),
                            RoundedCornerShape(24.dp)
                        )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Detaylar", modifier = Modifier.padding(15.dp), color = Color.White)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .weight(50F),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            )
                        ) {
                            Column(modifier = Modifier.padding(15.dp)) {
                                Text(text = "Son 24 Saatte en yüksek : ${coin.marketData.high24h["usd"]}$ ")
                                Text(text = "Son 24 Saatte en düşük : ${coin.marketData.low24h["usd"]}$ ")
                                Text(text = "Toplam hacim : ${coin.marketData.totalVolume["usd"]}$ ")
                                Text(text = "Toplam arz : ${coin.marketData.totalSupply} ")
                                Text(text = "Dolaşımdaki arz : ${coin.marketData.circulatingSupply} ")
                                Text(text = "Son 24 saatteki değişim : %${coin.marketData.priceChangePercentage24h} ")


                            }
                        }
                        Text("AI Yorumu", modifier = Modifier.padding(15.dp), color = Color.White)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .verticalScroll(scrollState)
                                .weight(50F),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            )
                        ) {
                            Column(modifier = Modifier.padding(15.dp)) {
                                if (aiComment != null) {
                                    Text("Yorum: $aiComment")
                                } else {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                modifier = Modifier.size(width = 80.dp, height = 35.dp),
                                onClick = {}, colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Green,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Al", fontSize = 18.sp)
                            }
                            Button(
                                modifier = Modifier.size(width = 80.dp, height = 35.dp),
                                onClick = {}, colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Sat", fontSize = 18.sp)
                            }
                        }
                    }
                }


            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        }
    }
    LaunchedEffect(id) {
        viewModel.getCoin(id)
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarD(id: String, navController: NavController) {
    val varMi = remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        val favRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("favourites")
            .document(id)

        favRef.get().addOnSuccessListener { doc ->
            varMi.value = doc.exists()
        }


    }

    TopAppBar(
        modifier = Modifier.border(1.dp, Color.White, shape = RoundedCornerShape(16.dp)),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.left),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                            contentDescription = "back"
                        )
                    }

                    Text("Detail Page")

                }

            }
        },
        actions = {
            IconButton(onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@IconButton
                val favRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("favourites")
                    .document(id)

                favRef.get().addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        favRef.delete()
                        varMi.value = false
                    } else {
                        favRef.set(mapOf("id" to id))
                        varMi.value = true

                    }
                }


            }) {
                if (varMi.value == false) {
                    Icon(
                        painter = painterResource(R.drawable.favourite),
                        tint = Color.White,
                        contentDescription = "fav",
                        modifier = Modifier
                            .padding(7.dp, 0.dp)
                            .size(20.dp)

                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.heart),
                        contentDescription = "fav",
                        tint = Color.Red,
                        modifier = Modifier
                            .padding(7.dp, 0.dp)
                            .size(20.dp)

                    )
                }

            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.AppBarRenk),
            titleContentColor = Color.White

        )
    )
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"


    // 30 saniye connect/read/write timeout ayarlı OkHttpClient
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: CryptoAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)                        // timeout’lu client’ı ekliyoruz
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CryptoAPI::class.java)
    }
}
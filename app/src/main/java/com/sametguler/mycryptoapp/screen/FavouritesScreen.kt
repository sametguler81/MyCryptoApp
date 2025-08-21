package com.sametguler.mycryptoapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sametguler.mycryptoapp.R
import com.sametguler.mycryptoapp.viewmodel.CryptoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Favourites(viewModel: CryptoViewModel) {
    // Favori coin detaylarını al
    val favoriteCoins by viewModel.favoriteCoins.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(R.color.AppBarRenk),
                titleContentColor = Color.White
            ),
            title = { Text("Favoriler") }
        )
    }) { innerPadding ->
        // Listener'ı bir kere başlat
        LaunchedEffect(Unit) {
            viewModel.startFavoriteListener()
        }

        if (favoriteCoins.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(R.color.AppBarRenk)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = colorResource(R.color.AppBarRenk))

            ) {
                items(favoriteCoins.size) {
                    val coin = favoriteCoins[it]
                    Card(onClick = {}, modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween

                        ) {
                            Column {
                                Text(
                                    text = coin.name,
                                    modifier = Modifier.padding(0.dp, 5.dp),
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                                Text(
                                    modifier = Modifier.padding(0.dp, 5.dp),
                                    text = "${coin.marketData.currentPrice.get("usd")} $",
                                    color = Color.Black,
                                    fontSize = 18.sp
                                )

                            }
                            IconButton(onClick = {
                                val uid =
                                    FirebaseAuth.getInstance().currentUser?.uid ?: return@IconButton
                                val favRef = FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(uid)
                                    .collection("favourites")
                                    .document(coin.id)

                                favRef.delete()

                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.heart),
                                    contentDescription = "fav",
                                    tint = Color.Red,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }

                }
            }
        }
    }


}

package com.sametguler.mycryptoapp.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.sametguler.mycryptoapp.model.CryptoModel
import com.sametguler.mycryptoapp.viewmodel.CryptoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun CryptoListScreen(
    viewModel: CryptoViewModel,
    navController: NavController,
    searchQuery: String = ""
) {

    val user = FirebaseAuth.getInstance().currentUser
    val userEmail = user?.email
    val context = LocalContext.current


    val coins by viewModel.coins.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Filtreleme:
    val filteredCoins = if (searchQuery.isBlank()) {
        coins
    } else {
        coins.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { viewModel.refreshCoins() }
    ) {
        LazyColumn(state = listState) {
            items(filteredCoins) { coin ->
                CryptoRow(coin = coin, navController = navController)
            }
        }
    }

    // Sonsuz scroll (sayfa sonuna yaklaşıldığında yeni veriler çek)
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collectLatest { index ->
                if (index != null && index >= filteredCoins.size - 5) {
                    viewModel.fetchCoins()
                }
            }
    }
}

@Composable
fun CryptoRow(coin: CryptoModel, navController: NavController) {
    val priceChange = coin.price_change_percentage_24h.toString()
    val priceColor = if (priceChange.contains("-")) Color.Red else Color.Green

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = colorResource(com.sametguler.mycryptoapp.R.color.AluminumDarkGray))
            .border(1.dp, Color.White, RoundedCornerShape(16.dp))
            .clickable { navController.navigate("detail_screen/${coin.id}") }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(

        ) {
            Text(text = coin.name, color = Color.White)
            Text(text = "${coin.current_price} $", color = Color.White)
        }
        Text(
            text = "% $priceChange",
            color = priceColor
        )

    }

}

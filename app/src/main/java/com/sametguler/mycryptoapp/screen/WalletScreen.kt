package com.sametguler.mycryptoapp.screen

import android.graphics.Paint.Align
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sametguler.mycryptoapp.R
import com.sametguler.mycryptoapp.viewmodel.CryptoViewModel

@Composable
fun Wallet(
    navController: NavController,
    secilenIndex: MutableState<Int>,
    viewModel: CryptoViewModel
) {
    val balance = remember { mutableStateOf(0.0) }

    Scaffold(topBar = { AppBarW(secilenIndex = secilenIndex) }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = colorResource(R.color.AppBarRenk))
        ) {
            MainSection(navController, balance, viewModel = viewModel)
        }
    }
}

@Composable
fun MainSection(
    navController: NavController,
    balance: MutableState<Double>,
    viewModel: CryptoViewModel
) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {
        PriceSection(balance = balance)
        Spacer(modifier = Modifier.size(width = 0.dp, height = 20.dp))
        AddBalanceSection(balance, modifier = Modifier.weight(1F), viewModel = viewModel)
    }
}

@Composable
fun AddBalanceSection(
    balance: MutableState<Double>,
    modifier: Modifier,
    viewModel: CryptoViewModel
) {
    val creditCardNumber = remember { mutableStateOf("1234  1234  1234  1234") }
    val creditCardName = remember { mutableStateOf("SAMET GULER") }
    val creditCardSKT = remember { mutableStateOf("12/28") }
    val tfCvv = remember { mutableStateOf("266") }
    var tfText = remember { mutableStateOf("") }
    var context = LocalContext.current

    val userUid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(true) {
        userUid?.let {
            viewModel.fetchCartItems(it)
            val list = viewModel.cartList.value

            list.firstOrNull()?.let { item ->
                creditCardNumber.value = item.cartNumber
                creditCardName.value = item.cartName
                creditCardSKT.value = item.cartSKT
                tfCvv.value = item.cartCvv
            }

            val db = FirebaseFirestore.getInstance()
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            db.collection("user")
                .document(userId!!)
                .collection("wallet")
                .document("balance")
                .get()
                .addOnSuccessListener { gelen ->
                    if (gelen != null && gelen.exists()) {
                        val balanceGelen = gelen.getDouble("balance")
                        balance.value = balanceGelen!!
                    }

                }

        }
    }


    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Bakiye Ekleme", fontFamily = FontFamily(Font(R.font.libertinusserif_semibold)),
                fontSize = 30.sp, color = Color.Black
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Seçili Kart", color = Color.Black, fontSize = 22.sp)
                Spacer(modifier = Modifier.size(width = 0.dp, height = 15.dp))
                LazyRow() {
                    item {
                        Card(
                            modifier = Modifier
                                .size(width = 300.dp, 160.dp)
                                .padding(end = 15.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),

                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "SmtPay",
                                        fontFamily = FontFamily(Font(R.font.mozillaheadline_medium)),
                                        fontSize = 15.sp,
                                        color = Color.White,
                                    )
                                    Image(
                                        modifier = Modifier.size(50.dp),
                                        painter = painterResource(R.drawable.money),
                                        contentDescription = "mastercard"
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 5.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.chip),
                                        contentDescription = "chip",
                                        modifier = Modifier
                                            .size(20.dp),
                                    )
                                    Image(
                                        painter = painterResource(R.drawable.contactless),
                                        contentDescription = "contactless",
                                        modifier = Modifier
                                            .size(15.dp)

                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 5.dp),

                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        "${creditCardNumber.value}",
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 5.dp),

                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${creditCardName.value}", color = Color(0xFFfff4b0))
                                    Text("${creditCardSKT.value}", color = Color.White)

                                }

                            }

                        }
                    }
                    item {
                        Card(
                            modifier = Modifier.size(width = 300.dp, 160.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),

                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "SmtPay",
                                        fontFamily = FontFamily(Font(R.font.mozillaheadline_medium)),
                                        fontSize = 15.sp,
                                        color = Color.White,
                                    )
                                    Image(
                                        modifier = Modifier.size(50.dp),
                                        painter = painterResource(R.drawable.money),
                                        contentDescription = "mastercard"
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 5.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.chip),
                                        contentDescription = "chip",
                                        modifier = Modifier
                                            .size(20.dp),
                                    )
                                    Image(
                                        painter = painterResource(R.drawable.contactless),
                                        contentDescription = "contactless",
                                        modifier = Modifier
                                            .size(15.dp)

                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 5.dp),

                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        "${creditCardNumber.value}",
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 5.dp),

                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${creditCardName.value}", color = Color(0xFFfff4b0))
                                    Text("${creditCardSKT.value}", color = Color.White)

                                }

                            }

                        }
                    }

                }
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp, 7.dp),// dış padding
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(2.dp, Color.Gray)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(7.dp),  // iç padding
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("Bakiye Gir", fontSize = 24.sp)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextField(value = tfText.value, onValueChange = {
                                    tfText.value = it
                                })

                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(onClick = {

                                    val balance = hashMapOf(
                                        "balance" to balance.value - tfText.value.toDouble()
                                    )

                                    FirebaseFirestore.getInstance()
                                        .collection("user")
                                        .document(userUid!!)
                                        .collection("wallet")
                                        .document("balance")
                                        .set(balance)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Bakiye Eklendi",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "Bakiye Yetersiz",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                }) {
                                    Text("Bakiye Çek")
                                }
                                Button(onClick = {

                                    val balance = hashMapOf(
                                        "balance" to balance.value + tfText.value.toDouble()
                                    )

                                    FirebaseFirestore.getInstance()
                                        .collection("user")
                                        .document(userUid!!)
                                        .collection("wallet")
                                        .document("balance")
                                        .set(balance)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Bakiye Çekildi",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "Bakiye Yetersiz",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }) {
                                    Text("Bakiye Ekle")
                                }
                            }
                        }
                    }
                }

            }

        }
    }
}


@Composable
fun PriceSection(balance: MutableState<Double>) {

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Güncel Bakiye",
                fontFamily = FontFamily(Font(R.font.libertinusserif_semibold)),
                color = Color.DarkGray,
                fontSize = 30.sp
            )
            Text(text = "$${balance.value}", color = Color.DarkGray, fontSize = 28.sp)

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarW(secilenIndex: MutableState<Int>) {
    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = colorResource(R.color.AppBarRenk),
        titleContentColor = Color.White
    ), title = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {
                secilenIndex.value = 1
            }) {
                Icon(
                    painter = painterResource(R.drawable.left),
                    tint = Color.White,
                    modifier = Modifier.size(30.dp),
                    contentDescription = "left"
                )
            }
            Text("Cüzdan", fontSize = 24.sp, color = Color.White)
        }
    })
}
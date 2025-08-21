package com.sametguler.mycryptoapp.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sametguler.mycryptoapp.R
import com.sametguler.mycryptoapp.viewmodel.CryptoViewModel

@Composable
fun AddCartMain(navController: NavController, viewModel: CryptoViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar(navController) }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainScreen(navController, viewModel = viewModel)
        }
    }
}

@Composable
fun MainScreen(navController: NavController, viewModel: CryptoViewModel) {
    val creditCardNumber = remember { mutableStateOf("1234  1234  1234  1234") }
    val creditCardName = remember { mutableStateOf("SAMET GULER") }
    val creditCardSKT = remember { mutableStateOf("12/28") }
    val tfCvv = remember { mutableStateOf("266") }
    val context = LocalContext.current
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
        }
    }

    if (viewModel.cartList.value.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CardDesign(creditCardNumber, creditCardName, creditCardSKT)
                AddCard(creditCardNumber, creditCardName, creditCardSKT, tfCvv)
                Button(
                    onClick = {
                        try {
                            val cartItem = hashMapOf(
                                "cartNumber" to creditCardNumber.value,
                                "cartName" to creditCardName.value,
                                "cartSKT" to creditCardSKT.value,
                                "cartCvv" to tfCvv.value
                            )

                            FirebaseFirestore.getInstance()
                                .collection("user")
                                .document(userUid!!)
                                .collection("carts")
                                .document()
                                .set(cartItem)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Kaydedildi", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        context,
                                        "Hata: ${e.printStackTrace()}",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        navController.navigate(route = "account_screen") {
                            popUpTo(route = "account_screen") {
                                inclusive = true
                            }
                        }
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        "Kaydet",
                        color = Color.Black,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(20.dp, 5.dp),
                    )
                }
            }
        }
    }


}

@Composable
fun AddCard(
    creditCardNumber: MutableState<String>,
    creditCardName: MutableState<String>,
    creditCardSKT: MutableState<String>,
    creditCardCvv: MutableState<String>
) {
    Box(modifier = Modifier.size(width = 350.dp, height = 300.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                label = { Text("Card number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(0.dp, Color.Transparent, RoundedCornerShape(8.dp))
                    .padding(5.dp),

                value = creditCardNumber.value,
                onValueChange = {
                    creditCardNumber.value = it
                },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = Color.Black,
                    focusedIndicatorColor = Color(0xFFf0c75e),
                    focusedTextColor = Color.Black
                )
            )
            TextField(
                label = { Text("Enter the name as it appears on your card.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(0.dp, Color.Transparent, RoundedCornerShape(8.dp))
                    .padding(5.dp),

                value = creditCardName.value,
                onValueChange = {
                    creditCardName.value = it
                },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = Color.Black,
                    focusedIndicatorColor = Color(0xFFf0c75e),
                    focusedTextColor = Color.Black
                )
            )
            Row {
                TextField(
                    label = { Text("Expiration Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(0.dp, Color.Transparent, RoundedCornerShape(8.dp))
                        .weight(40F)
                        .padding(5.dp),

                    value = creditCardSKT.value,
                    onValueChange = {
                        creditCardSKT.value = it
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedTextColor = Color.Black,
                        focusedIndicatorColor = Color(0xFFf0c75e),
                        focusedTextColor = Color.Black
                    )
                )
                TextField(
                    label = { Text("Cvv") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(0.dp, Color.Transparent, RoundedCornerShape(8.dp))
                        .weight(60F)
                        .padding(5.dp),
                    value = creditCardCvv.value,
                    onValueChange = {
                        creditCardCvv.value = it
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedTextColor = Color.Black,
                        focusedIndicatorColor = Color(0xFFf0c75e),
                        focusedTextColor = Color.Black
                    )
                )
            }
        }
    }
}

@Composable
fun CardDesign(
    creditCardNumber: MutableState<String>, creditCardName: MutableState<String>,
    creditCardSKT: MutableState<String>
) {
    val mozillaR = FontFamily(Font(R.font.mozillaheadline_regular))
    val mozillaM = FontFamily(Font(R.font.mozillaheadline_medium))

    Card(
        modifier = Modifier
            .size(350.dp, 235.dp)

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0f2027),
                            Color(0xFF203a43),
                            Color(0xFF2c5364)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp, 0.dp)

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),

                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "SmtPay",
                        fontFamily = mozillaM,
                        fontSize = 26.sp,
                        color = Color.White,
                    )
                    Image(
                        modifier = Modifier.size(100.dp),
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
                            .size(40.dp),
                    )
                    Image(
                        painter = painterResource(R.drawable.contactless),
                        contentDescription = "contactless",
                        modifier = Modifier
                            .size(30.dp)

                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 5.dp),

                    horizontalArrangement = Arrangement.Start
                ) {
                    Text("${creditCardNumber.value}", fontSize = 30.sp, color = Color.White)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavController) {
    val libertinusBold = FontFamily(Font(R.font.libertinusserif_semibold))
    val libertinus = FontFamily(Font(R.font.libertinusserif_regular))


    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.AppBarRenk)
        ),
        title = {
            Column(modifier = Modifier.padding(4.dp)) {
                Text("Add Card", fontFamily = libertinusBold, color = Color.White, fontSize = 28.sp)
                Text(
                    "Add your debit/credit card",
                    color = Color.LightGray,
                    fontFamily = libertinus,
                    fontSize = 22.sp
                )
            }
        },
        actions = {
            IconButton(onClick = {
                navController.navigate(route = "account_screen") {
                    popUpTo(route = "account_screen") {
                        inclusive = true
                    }
                }

            }, modifier = Modifier.padding(4.dp)) {
                Icon(
                    painter = painterResource(R.drawable.no),
                    tint = Color.White,
                    contentDescription = "exit"
                )
            }
        }
    )
}

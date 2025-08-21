package com.sametguler.mycryptoapp.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.sametguler.mycryptoapp.R
import com.sametguler.mycryptoapp.viewmodel.CryptoViewModel
import java.io.File

@Composable
fun Account(navController: NavController,viewModel: CryptoViewModel) {
    val mainNavController = rememberNavController()

    NavHost(mainNavController, startDestination = "account_screen") {
        composable(route = "account_screen") {
            AccountScreen(navController = navController, mainNavController = mainNavController)
        }

        composable(route = "edit_screen") {
            EditScreen()
        }

        composable(route = "add_cart") {
            AddCartMain(mainNavController, viewModel = viewModel)
        }

    }
}

@Composable
fun AccountScreen(navController: NavController, mainNavController: NavController) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val key = "profileImageUri_${user?.uid}"

    val savedPath = prefs.getString(key, null)
    val imageUri = remember { mutableStateOf<Uri?>(savedPath?.let { Uri.parse(it) }) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                val file = File(context.filesDir, "${user?.uid}_profile.jpg")
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                val newUri = Uri.fromFile(file)
                imageUri.value = newUri
                prefs.edit().putString(key, newUri.toString()).apply()
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        val ad = prefs.getString("tfAd", "")
        val soyad = prefs.getString("tfSoyad", "")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colorResource(R.color.AppBarRenk), Color.Black)
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(0.35f)) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = imageUri.value,
                        contentDescription = "Profil Fotoğrafı",
                        placeholder = painterResource(R.drawable.profile),
                        error = painterResource(R.drawable.profile),
                        fallback = painterResource(R.drawable.profile),
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable { launcher.launch("image/*") }
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("${ad} ${soyad}", fontSize = 24.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = user?.email ?: "Bilinmeyen Kullanıcı",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.65f)
            ) {
                Card(
                    shape = RoundedCornerShape(topEnd = 32.dp, topStart = 32.dp),
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.AppBarRenk),
                        contentColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(color = colorResource(R.color.CustomDarkBlueGray))
                                .clickable {
                                    mainNavController.navigate("edit_screen") {
                                        popUpTo(route = "account_screen") {
                                            inclusive = false
                                        }
                                    }
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(

                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    tint = Color.White,
                                    painter = painterResource(R.drawable.settings),
                                    contentDescription = "settings",
                                    modifier = Modifier.size(50.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    "Settings",
                                    color = Color.White
                                )
                            }
                            Row(

                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.chevron),
                                    contentDescription = "right",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(color = colorResource(R.color.CustomDarkBlueGray))
                                .clickable {
                                    mainNavController.navigate("add_cart") {
                                        popUpTo(route = "account_screen") {
                                            inclusive = false
                                        }
                                    }
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(

                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    tint = Color.White,
                                    painter = painterResource(R.drawable.debit_card),
                                    contentDescription = "addCard",
                                    modifier = Modifier.size(50.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    "Manage Card",
                                    color = Color.White
                                )
                            }
                            Row(

                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.chevron),
                                    contentDescription = "right",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("login") {
                                    popUpTo("account") { inclusive = true }
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Çıkış Yap")
                        }
                    }
                }

            }
        }
    }
}


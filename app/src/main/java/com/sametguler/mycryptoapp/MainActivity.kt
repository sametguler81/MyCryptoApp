package com.sametguler.mycryptoapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Space
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.sametguler.mycryptoapp.components.AppBar
import com.sametguler.mycryptoapp.model.CryptoModel
import com.sametguler.mycryptoapp.screen.Account
import com.sametguler.mycryptoapp.screen.AppBarD
import com.sametguler.mycryptoapp.screen.Community
import com.sametguler.mycryptoapp.screen.CryptoListScreen
import com.sametguler.mycryptoapp.screen.DetailScreen
import com.sametguler.mycryptoapp.screen.EditScreen
import com.sametguler.mycryptoapp.screen.Favourites
import com.sametguler.mycryptoapp.screen.Wallet
import com.sametguler.mycryptoapp.service.CryptoAPI
import com.sametguler.mycryptoapp.ui.theme.MyCryptoAppTheme
import com.sametguler.mycryptoapp.viewmodel.CryptoViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private val viewModel: CryptoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContent {
            MyCryptoApp(viewModel)
        }
    }
}

// 🔹 Tüm navigasyonu buraya taşıdık ve BottomNavBar’ı login ekranında gizledik
@Composable
fun MyCryptoApp(viewModel: CryptoViewModel) {
    val navController = rememberNavController()
    val currentBackStack = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack.value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "login") {
                BottomNavBar(viewModel, navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
                "list_screen"
            } else {
                "login"
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginPage(
                    onLoginSuccess = {
                        navController.navigate("list_screen") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("list_screen") {
                PageChanges(viewModel = viewModel, navController)
            }


        }
    }
}


@Composable
fun BottomNavBar(viewModel: CryptoViewModel, navController: NavController) {
    val items = listOf("Favoriler", "Ana Sayfa", "Topluluk", "Cüzdan", "Hesap")
    val secilenIndex = remember { mutableStateOf(1) }
    Scaffold(bottomBar = {
        NavBarSection(items, secilenIndex)
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (secilenIndex.value == 0) {
                Favourites(viewModel)
            } else if (secilenIndex.value == 1) {
                PageChanges(viewModel = viewModel, navController)
            } else if (secilenIndex.value == 2) {
                Community(viewModel)
            } else if (secilenIndex.value == 3) {
                Wallet(navController, secilenIndex, viewModel)
            } else if (secilenIndex.value == 4) {
                Account(navController, viewModel = viewModel)
            }
        }

    }
}


@Composable
fun NavBarSection(items: List<String>, secilenIndex: MutableState<Int>) {
    // NavigationBar: Alt menünün arka planını oluşturur
    NavigationBar(
        containerColor = colorResource(R.color.AppBarRenk),
        modifier = Modifier
            .background(color = colorResource(R.color.AppBarRenk))
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .border(
                1.dp,
                Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ),
        tonalElevation = 8.dp         // Gölge (yükseklik efekti)
    ) {
        items.forEachIndexed { index, item ->

            // Seçili olan item mı diye kontrol ediyoruz
            val selected = secilenIndex.value == index

            NavigationBarItem(
                selected = selected,            // seçili olan item'ı belirtir
                onClick = { secilenIndex.value = index }, // item seçildiğinde index güncellenir

                // Label: item'ın altındaki yazı
                label = {
                    Text(
                        text = item,
                        color = if (selected) Color.White else Color.Gray // seçiliyse beyaz, değilse gri
                    )
                },

                // Icon: İkonun kendisini çiziyoruz
                icon = {
                    Box(
                        modifier = Modifier
                            // Seçili item daha büyük görünür
                            .size(if (selected) 48.dp else 40.dp)

                            // Seçili item'ın arkasına yuvarlak bir arka plan koyuyoruz
                            .background(
                                if (selected) Color.Black else Color.Transparent,
                                shape = RoundedCornerShape(50)
                            )

                            // İkon biraz içerde dursun diye padding
                            .padding(if (selected) 10.dp else 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Item’a göre ikonları çiziyoruz
                        when (item) {
                            "Favoriler" -> Icon(
                                painter = painterResource(R.drawable.favourite),
                                contentDescription = "fav",
                                tint = if (selected) Color.White else Color.Gray // renk seçimi
                            )

                            "Hesap" -> Icon(
                                painter = painterResource(R.drawable.profile),
                                contentDescription = "Acc",
                                tint = if (selected) Color.White else Color.Gray
                            )

                            "Ana Sayfa" -> Icon(
                                painter = painterResource(R.drawable.cryptocurrency),
                                contentDescription = "Main",
                                tint = if (selected) Color.White else Color.Gray
                            )

                            "Topluluk" -> Icon(
                                painter = painterResource(R.drawable.communities),
                                contentDescription = "Main",
                                tint = if (selected) Color.White else Color.Gray
                            )

                            "Cüzdan" -> Icon(
                                painter = painterResource(R.drawable.wallet),
                                contentDescription = "Main",
                                tint = if (selected) Color.White else Color.Gray
                            )
                        }
                    }
                },

                // Renklerin default highlight'ını kaldırıyoruz (mavi seçili rengi istemiyoruz)
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                ),

                // Seçili item biraz yukarı kalksın (floating efekti)
                modifier = if (selected) {
                    Modifier.offset(y = (-4).dp)
                } else Modifier
            )
        }
    }
}


@Composable
fun PageChanges(viewModel: CryptoViewModel, mainnavController: NavController) {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination?.route
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    MyCryptoAppTheme {
        Scaffold(
            containerColor = colorResource(R.color.AppBarRenk),
            topBar = {
                when (currentDestination) {
                    "list_screen" -> AppBar(
                        searchText = searchQuery,
                        onSearchTextChanged = { searchQuery = it },
                        isSearching = isSearching,
                        onSearchStateChanged = {
                            isSearching = it
                            if (!it) searchQuery = ""
                        },
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            mainnavController.navigate("login") {
                                popUpTo("list_screen") {
                                    inclusive = true
                                }
                            }
                        }
                    )

                    "detail_screen/{id}" -> {
                        val id = currentBackStackEntry.value?.arguments?.getString("id") ?: ""
                        AppBarD(id, navController)
                    }
                }

            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController,
                    startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
                        "list_screen"
                    } else {
                        "login"
                    }
                ) {
                    composable("login") {
                        LoginPage(
                            onLoginSuccess = {
                                navController.navigate("list_screen") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("list_screen") {
                        CryptoListScreen(viewModel, navController, searchQuery)
                    }
                    composable("detail_screen/{id}") {
                        val id = it.arguments?.getString("id") ?: return@composable
                        DetailScreen(id, viewModel, navController)
                    }
                }

            }
        }
    }
}

@Composable
fun LoginPage(onLoginSuccess: () -> Unit) {
    val auth: FirebaseAuth = Firebase.auth
    val context = LocalContext.current
    var tfEmail = remember { mutableStateOf("") }
    var tfPassword = remember { mutableStateOf("") }
    val cbValue = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.AppBarRenk)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Quantora",
            fontStyle = FontStyle.Italic,
            fontSize = 40.sp,
            color = colorResource(R.color.Gold)
        )
        Spacer(modifier = Modifier.size(width = 0.dp, 80.dp))

        Column {
            OutlinedTextField(
                label = { Text("Email", color = Color.White) },
                value = tfEmail.value,
                onValueChange = { tfEmail.value = it },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    focusedBorderColor = colorResource(R.color.Gold)
                )
            )

            Spacer(modifier = Modifier.size(width = 0.dp, 40.dp))

            OutlinedTextField(
                label = { Text("Şifre", color = Color.White) },
                value = tfPassword.value,
                visualTransformation = if (cbValue.value) VisualTransformation.None else PasswordVisualTransformation(),
                onValueChange = { tfPassword.value = it },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    focusedBorderColor = colorResource(R.color.Gold)
                )
            )
            Spacer(modifier = Modifier.size(width = 0.dp, 20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = cbValue.value, onCheckedChange = {
                    cbValue.value = it
                })
                if (cbValue.value) Text(
                    "Parolayı gösterme!",
                    color = Color.White
                ) else Text("Parolayı göster!", color = Color.White)
            }

        }
        Spacer(modifier = Modifier.size(width = 0.dp, 40.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(onClick = {
                if (tfEmail.value.isEmpty() || tfPassword.value.isEmpty()) {
                    Toast.makeText(context, "Lütfen boş bırakmayın", Toast.LENGTH_SHORT).show()
                } else {
                    auth.signInWithEmailAndPassword(tfEmail.value, tfPassword.value)
                        .addOnSuccessListener {
                            onLoginSuccess()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                }
            }) {
                Text("Giriş Yap")
            }
            OutlinedButton(onClick = {
                if (tfEmail.value.isEmpty() || tfPassword.value.isEmpty()) {
                    Toast.makeText(context, "Lütfen boş bırakmayın", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(tfEmail.value, tfPassword.value)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Kayıt başarılı", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                }
                tfEmail.value = ""
                tfPassword.value = ""
            }) {
                Text("Kayıt Ol")
            }
        }
    }
}


object RetrofitInstance {
    private const val BASE_URL = "https://api.coingecko.com/api/v3/"
    private const val API_KEY = "CG-KbJFM8mQHpXTYJEPcuRpyRHW"

    private val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val originalRequest: Request = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader("x-api-key", API_KEY)
                .build()
            chain.proceed(newRequest)
        })
        .build()

    val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(CryptoAPI::class.java)
}

object RetrofitInstance2 {
    private const val BASE_URL = "https://api.coingecko.com/api/v3/"
    private const val API_KEY = "CG-KbJFM8mQHpXTYJEPcuRpyRHW"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("accept", "application/json")
                .addHeader("x-cg-demo-api-key", API_KEY)
                .build()
            chain.proceed(request)
        }
        .build()

    val api: CryptoAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CryptoAPI::class.java)
    }
}
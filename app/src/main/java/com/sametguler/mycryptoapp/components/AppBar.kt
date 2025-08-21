package com.sametguler.mycryptoapp.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sametguler.mycryptoapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    isSearching: Boolean,
    onSearchStateChanged: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    TopAppBar(

        title = {
            if (!isSearching) {
                Text("Quantora")
            } else {
                TextField(
                    placeholder = { Text("Ara") },
                    value = searchText,
                    onValueChange = onSearchTextChanged,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedTextColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFFB8860B), // koyu gold rengi
                        focusedTextColor = Color.White
                    )
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.AppBarRenk), // Örnek koyu renk
            titleContentColor = Color.White
        ),
        actions = {
            if (!isSearching) {
                IconButton(onClick = { onSearchStateChanged(true) }) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.search),
                        tint = Color.White,
                        contentDescription = "search"
                    )
                }
            } else {
                IconButton(onClick = { onSearchStateChanged(false) }) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.close),
                        tint = Color.White,
                        contentDescription = "close"
                    )
                }
            }
            IconButton(onClick = {
                onLogout()
                Toast.makeText(context, "Çıkış Yapıldı!", Toast.LENGTH_LONG).show()
            }) {
                Icon(
                    painter = painterResource(R.drawable.logout),
                    tint = Color.White,
                    contentDescription = "Logout",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}

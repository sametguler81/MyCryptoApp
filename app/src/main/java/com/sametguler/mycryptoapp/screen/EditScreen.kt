package com.sametguler.mycryptoapp.screen

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sametguler.mycryptoapp.R

@Composable
fun EditScreen(
) {


    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val tfAd = remember { mutableStateOf("") }
    val tfSoyad = remember { mutableStateOf("") }
    val tfTel = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        tfAd.value = prefs.getString("tfAd", "") ?: ""
        tfSoyad.value = prefs.getString("tfSoyad", "") ?: ""
        tfTel.value = prefs.getString("tfTel", "") ?: ""
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp, 30.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Kişisel Bilgiler", color = Color.Black, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(50.dp))

            OutlinedTextField(
                value = tfAd.value,
                onValueChange = { tfAd.value = it },
                label = { Text("Ad") },
                placeholder = { Text("Adınız") },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF2E2E2E),
                    focusedLabelColor = colorResource(R.color.Gold),
                    focusedIndicatorColor = colorResource(R.color.Gold)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tfSoyad.value,
                onValueChange = { tfSoyad.value = it },
                label = { Text("Soyad") },
                placeholder = { Text("Soyadınız") },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF2E2E2E),
                    focusedLabelColor = colorResource(R.color.Gold),
                    focusedIndicatorColor = colorResource(R.color.Gold)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tfTel.value,
                onValueChange = { tfTel.value = it },
                label = { Text("Telefon") },
                placeholder = { Text("Telefon Numaranız") },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF2E2E2E),
                    focusedLabelColor = colorResource(R.color.Gold),
                    focusedIndicatorColor = colorResource(R.color.Gold)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    prefs.edit().putString("tfAd", tfAd.value).apply()
                    prefs.edit().putString("tfSoyad", tfSoyad.value).apply()
                    prefs.edit().putString("tfTel", tfTel.value).apply()
                    Toast.makeText(context, "Kaydedildi", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Kaydet")
            }

        }
    }
}
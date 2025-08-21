package com.sametguler.mycryptoapp.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.sametguler.mycryptoapp.R
import com.sametguler.mycryptoapp.viewmodel.CryptoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Community(viewModel: CryptoViewModel) {
    val commentText = remember { mutableStateOf("") }
    val commentList = viewModel.comments.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.observeComments()
    }

    Scaffold(topBar = { AppBarC() }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.AppBarRenk))
                .padding(16.dp)

        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(innerPadding)
            ) {
                items(commentList.value.size) {
                    val currentUserMail = FirebaseAuth.getInstance().currentUser?.email
                    val isMine = currentUserMail == commentList.value[it].userEmail
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            modifier = Modifier.padding(8.dp), colors = CardDefaults.cardColors(
                                containerColor = if (isMine) Color.Green else Color.White,
                                contentColor = Color.Black
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp),
                            ) {
                                Text(
                                    text = commentList.value[it].userEmail,
                                    fontSize = 12.sp,
                                    color = Color.Blue
                                )
                                Text(text = commentList.value[it].text, fontSize = 16.sp)

                            }
                        }
                    }

                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = commentText.value,
                    onValueChange = { commentText.value = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Yorum yaz...") }
                )
                IconButton(onClick = {
                    viewModel.sendComment(commentText.value)
                    commentText.value = ""
                    Toast.makeText(context, "Yorum paylaşıldı!", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        tint = Color.White,
                        contentDescription = "Gönder"
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarC() {
    TopAppBar(
        title = { Text("Topluluk") }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.AppBarRenk),
            titleContentColor = Color.White
        )
    )
}


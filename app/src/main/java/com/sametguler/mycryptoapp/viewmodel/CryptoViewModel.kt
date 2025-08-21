package com.sametguler.mycryptoapp.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sametguler.mycryptoapp.RetrofitInstance
import com.sametguler.mycryptoapp.RetrofitInstance2
import com.sametguler.mycryptoapp.model.CartItem
import com.sametguler.mycryptoapp.model.CommentModel
import com.sametguler.mycryptoapp.model.CryptoDetailModel
import com.sametguler.mycryptoapp.model.CryptoModel
import com.sametguler.mycryptoapp.screen.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CryptoViewModel(application: Application) : AndroidViewModel(application) {

    private val _coins = MutableStateFlow<List<CryptoModel>>(emptyList())
    val coins: StateFlow<List<CryptoModel>> = _coins

    private val _coin = MutableStateFlow<CryptoDetailModel?>(null)
    val coin: StateFlow<CryptoDetailModel?> = _coin

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private var currentPage = 1
    private var isLoading = false

    init {
        fetchCoins()
    }

    fun getCoin(id: String) {
        viewModelScope.launch {
            try {
                val coin = RetrofitInstance2.api.getCoin(id)
                _coin.value = coin
                fetchAiComment(coin)  // Coin detay çekildikten sonra AI yorumu al
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchCoins() {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            try {
                val coinList = RetrofitInstance.api.getCoins(page = currentPage)
                val currentList = _coins.value.toMutableList()

                coinList.forEach { coin ->
                    if (currentList.none { it.id == coin.id }) {
                        currentList.add(coin)
                    }
                }

                _coins.value = currentList
                currentPage++
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    private val _favoriteIds = MutableStateFlow<List<String>>(emptyList())
    val favoriteIds: StateFlow<List<String>> = _favoriteIds

    private val _favoriteCoins = MutableStateFlow<List<CryptoDetailModel>>(emptyList())
    val favoriteCoins: StateFlow<List<CryptoDetailModel>> = _favoriteCoins

    fun startFavoriteListener() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("favourites")
            .addSnapshotListener { snap, _ ->
                val ids = snap?.documents?.mapNotNull { it.id }?.toList() ?: emptyList()
                _favoriteIds.value = ids

                viewModelScope.launch {
                    val favoriteDetails = ids.map { id ->
                        async {
                            try {
                                RetrofitInstance2.api.getCoin(id)
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }.mapNotNull { it.await() }
                    _favoriteCoins.value = favoriteDetails
                }
            }
    }

    // --- AI Yorum için eklenen kodlar ---

    private val _aiComment = MutableStateFlow<String?>(null)
    val aiComment: StateFlow<String?> = _aiComment

    private val _isLoadingAiComment = MutableStateFlow(false)
    val isLoadingAiComment: StateFlow<Boolean> = _isLoadingAiComment

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError

    fun fetchAiComment(
        coin: CryptoDetailModel
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getAiComment(
                    coin = coin.id,
                    price = coin.marketData.currentPrice["usd"] ?: 0.0,
                    change24 = coin.marketData.priceChangePercentage24h,
                    change7d = coin.marketData.price_change_percentage_7d!!,  // 7 günlük veri yok
                    rsi = 0.0,       // rsi yok, 0.0 koyduk
                    macd = "neutral", // macd yok, "neutral" koyduk
                    marketCap = coin.marketData.marketCap["usd"]?.toDouble() ?: 0.0,
                    volume = coin.marketData.totalVolume["usd"] ?: 0.0
                )
                _aiComment.value = response.aiComment
            } catch (e: Exception) {
                println(e.message)
                _aiComment.value = "AI yorum alınamadı"
            }
        }
    }

    fun refreshCoins() {
        if (_isRefreshing.value) return
        _isRefreshing.value = true
        currentPage = 1
        viewModelScope.launch {
            try {
                val freshCoins = RetrofitInstance.api.getCoins(page = currentPage)
                _coins.value = freshCoins
                Toast.makeText(
                    getApplication(),
                    "Veriler Yenilendi: ${freshCoins.size}",
                    Toast.LENGTH_LONG
                ).show()
                currentPage++
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
                isLoading = false
            }
        }
    }

    private val _cartList = mutableStateOf<List<CartItem>>(emptyList())
    val cartList: State<List<CartItem>> = _cartList

    fun fetchCartItems(userUid: String) {
        FirebaseFirestore.getInstance()
            .collection("user")
            .document(userUid)
            .collection("carts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && !snapshot.isEmpty) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(CartItem::class.java)
                    }
                    _cartList.value = items
                }
            }
    }

    fun sendComment(commentText: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val comment = CommentModel(
                text = commentText,
                userEmail = user.email ?: "Anonim"
            )
            FirebaseFirestore.getInstance()
                .collection("comments")
                .add(comment)
        }
    }

    val _comments = MutableStateFlow<List<CommentModel>>(emptyList())
    val comments: StateFlow<List<CommentModel>> = _comments

    fun observeComments() {
        FirebaseFirestore.getInstance()
            .collection("comments")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val list =
                        snapshot.documents.mapNotNull { it.toObject(CommentModel::class.java) }
                    _comments.value = list
                }
            }
    }


}

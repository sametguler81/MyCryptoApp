package com.sametguler.mycryptoapp.service

import com.sametguler.mycryptoapp.model.CryptoDetailModel
import com.sametguler.mycryptoapp.model.CryptoModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// AI yorum API cevabı modeli
data class AiCommentResponse(
    val coin: String,
    val aiComment: String
)

interface CryptoAPI {

    // Coin listesi endpoint'i
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 50,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): List<CryptoModel>

    // Coin detayları endpoint'i
    @GET("coins/{id}")
    suspend fun getCoin(@Path("id") id: String): CryptoDetailModel

    // AI yorum endpoint'i
    @GET("ai-comment")
    suspend fun getAiComment(
        @Query("coin") coin: String,
        @Query("price") price: Double,
        @Query("change24") change24: Double,
        @Query("change7d") change7d: Double,
        @Query("rsi") rsi: Double,
        @Query("macd") macd: String,
        @Query("market_cap") marketCap: Double,
        @Query("volume") volume: Double
    ): AiCommentResponse
}

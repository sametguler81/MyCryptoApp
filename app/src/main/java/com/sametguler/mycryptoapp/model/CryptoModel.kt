package com.sametguler.mycryptoapp.model

import com.google.gson.annotations.SerializedName

data class CryptoModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("current_price")
    val current_price: Double,
    @SerializedName("market_cap")
    val market_cap: Long,
    @SerializedName("price_change_percentage_24h")
    val price_change_percentage_24h: Double
)


data class CryptoDetailModel(
    val id: String,
    val symbol: String,
    val name: String,
    val image: Image,
    @SerializedName("market_data")
    val marketData: MarketData
)

data class Image(
    val thumb: String,
    val small: String,
    val large: String
)

data class MarketData(
    @SerializedName("current_price")
    val currentPrice: Map<String, Double>,

    @SerializedName("market_cap")
    val marketCap: Map<String, Long>,

    @SerializedName("market_cap_change_percentage_24h")
    val marketCapChangePercentage24h: Double,

    @SerializedName("total_volume")
    val totalVolume: Map<String, Double>,

    @SerializedName("high_24h")
    val high24h: Map<String, Double>,

    @SerializedName("low_24h")
    val low24h: Map<String, Double>,

    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Double,

    @SerializedName("circulating_supply")
    val circulatingSupply: Double,

    @SerializedName("total_supply")
    val totalSupply: Double?,

    @SerializedName("max_supply")
    val maxSupply: Double?,

    @SerializedName("price_change_percentage_7d")
    val price_change_percentage_7d: Double?

)

data class CommentModel(
    val text: String = "",
    val userEmail: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class CartItem(
    val cartNumber: String = "",
    val cartName: String = "",
    val cartSKT: String = "",
    val cartCvv: String = "",
)


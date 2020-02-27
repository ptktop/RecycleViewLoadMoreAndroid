package com.ptktop.recycleviewloadmoreandroid.data.network.model

import com.google.gson.annotations.SerializedName

class CoinResponse {
    @SerializedName("status")
    lateinit var status: String
    @SerializedName("data")
    lateinit var coinData: CoinDataResponse
}
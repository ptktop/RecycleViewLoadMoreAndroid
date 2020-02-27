package com.ptktop.recycleviewloadmoreandroid.data.network.model

import com.google.gson.annotations.SerializedName

class CoinDataResponse {

    @SerializedName("coins")
    var listCoin = ArrayList<CoinDataListResponse?>()

}
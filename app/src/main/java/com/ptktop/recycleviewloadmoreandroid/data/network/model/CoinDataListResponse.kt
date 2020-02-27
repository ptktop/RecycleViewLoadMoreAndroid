package com.ptktop.recycleviewloadmoreandroid.data.network.model

import com.google.gson.annotations.SerializedName

class CoinDataListResponse {
    @SerializedName("name")
    var name: String? = null
    @SerializedName("description")
    var description: String? = null
    @SerializedName("iconUrl")
    var iconUrl: String? = null
}
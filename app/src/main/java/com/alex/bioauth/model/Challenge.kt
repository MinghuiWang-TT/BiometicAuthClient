package com.alex.bioauth.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Challenge(
    @SerializedName("id") val id: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("challenge") val challenge: String? = null,
    @SerializedName("nonce") val nonce: Long? = null
) : Serializable
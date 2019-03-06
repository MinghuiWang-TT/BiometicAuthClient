package com.alex.bioauth.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("userName") var userName: String? = null,
    @SerializedName("secret") var secret: String? = null,
    @SerializedName("publicKey") var publicKey: String?? = null
) : Serializable

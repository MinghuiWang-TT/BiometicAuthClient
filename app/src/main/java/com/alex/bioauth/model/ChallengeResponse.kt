package com.alex.bioauth.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChallengeResponse(
    @SerializedName("challengeId") var challengeId: String? = null,
    @SerializedName("payload") var payload: String? = null
) : Serializable
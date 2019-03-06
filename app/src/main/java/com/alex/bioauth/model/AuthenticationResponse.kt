package com.alex.bioauth.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AuthenticationResponse(@SerializedName("status") val status: String) : Serializable
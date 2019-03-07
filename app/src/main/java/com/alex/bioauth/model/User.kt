package com.alex.bioauth.model

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
data class User(
    var userName: String? = null,
    var secret: String? = null,
    var publicKey: String? = null
) : Serializable

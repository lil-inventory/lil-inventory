package org.ivcode.inventory.auth.services.model

import com.fasterxml.jackson.annotation.JsonProperty

data class AccessToken (
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("token_type")
    val tokenType: String = "Bearer",
    @JsonProperty("expires_in")
    val expiresIn: Long?,
    @JsonProperty("refresh_token")
    val refreshToken: String?,
)
package com.cubiccode.moreki.models

data class RegisterRequest(val name: String, val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)
data class UpdateProfileRequest(val email: String, val name: String)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val name: String? = null,
    val email: String? = null
)
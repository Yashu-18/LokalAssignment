package com.example.lokalassignment.viewmodel

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    
    data class OtpSent(
        val email: String,
        val expiryTime: Long
    ) : AuthState()
    
    data class OtpError(
        val message: String,
        val email: String = "",
        val expiryTime: Long = 0L,
        val attemptsRemaining: Int = 0
    ) : AuthState()
    
    data class Authenticated(
        val email: String,
        val sessionStartTime: Long
    ) : AuthState()
}

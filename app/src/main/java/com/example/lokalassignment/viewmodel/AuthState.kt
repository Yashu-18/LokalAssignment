package com.example.lokalassignment.viewmodel

/**
 * Sealed class representing the authentication state
 * Provides type-safe state management for the UI
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    
    data class OtpSent(
        val email: String,
        val expiryTime: Long
    ) : AuthState()
    
    data class OtpError(
        val message: String,
        val attemptsRemaining: Int = 0
    ) : AuthState()
    
    data class Authenticated(
        val email: String,
        val sessionStartTime: Long
    ) : AuthState()
}

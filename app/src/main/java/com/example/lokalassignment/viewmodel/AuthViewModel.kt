package com.example.lokalassignment.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.lokalassignment.analytics.AnalyticsLogger
import com.example.lokalassignment.data.OtpManager
import com.example.lokalassignment.data.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel managing authentication logic
 * Handles OTP generation, validation, and session management
 */
class AuthViewModel(
    private val otpManager: OtpManager,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Generate and send OTP for the given email
     */
    fun sendOtp(email: String) {
        if (!isValidEmail(email)) {
            _authState.value = AuthState.OtpError("Invalid email address")
            return
        }
        
        _authState.value = AuthState.Loading
        
        try {
            val otp = otpManager.generateOtp(email)
            val expiryTime = System.currentTimeMillis() + 60_000
            
            // Log analytics event
            analyticsLogger.logOtpGenerated(email)
            
            // In a real app, you would send OTP via email/SMS here
            // For this assignment, we just log it
            println("🔐 OTP for $email: $otp")
            
            _authState.value = AuthState.OtpSent(email, expiryTime)
        } catch (e: Exception) {
            _authState.value = AuthState.OtpError("Failed to generate OTP: ${e.message}")
        }
    }
    
    /**
     * Validate OTP entered by user
     */
    fun validateOtp(email: String, inputOtp: String) {
        if (inputOtp.length != 6) {
            _authState.value = AuthState.OtpError("OTP must be 6 digits")
            return
        }
        
        _authState.value = AuthState.Loading
        
        when (val result = otpManager.validateOtp(email, inputOtp)) {
            is ValidationResult.Success -> {
                analyticsLogger.logOtpValidationSuccess(email)
                _authState.value = AuthState.Authenticated(
                    email = email,
                    sessionStartTime = System.currentTimeMillis()
                )
            }
            is ValidationResult.Invalid -> {
                analyticsLogger.logOtpValidationFailure(email, "Invalid OTP")
                _authState.value = AuthState.OtpError(
                    message = "Invalid OTP. ${result.attemptsRemaining} attempts remaining.",
                    attemptsRemaining = result.attemptsRemaining
                )
            }
            is ValidationResult.Expired -> {
                analyticsLogger.logOtpValidationFailure(email, "OTP Expired")
                _authState.value = AuthState.OtpError("OTP has expired. Please request a new one.")
            }
            is ValidationResult.AttemptsExhausted -> {
                analyticsLogger.logOtpValidationFailure(email, "Attempts Exhausted")
                _authState.value = AuthState.OtpError("Maximum attempts exceeded. Please request a new OTP.")
            }
            is ValidationResult.NoOtpFound -> {
                analyticsLogger.logOtpValidationFailure(email, "No OTP Found")
                _authState.value = AuthState.OtpError("No OTP found. Please request a new one.")
            }
        }
    }
    
    /**
     * Get remaining time for OTP in seconds
     */
    fun getRemainingTime(email: String): Long? {
        return otpManager.getRemainingTime(email)?.let { it / 1000 }
    }
    
    /**
     * Logout user
     */
    fun logout() {
        val currentState = _authState.value
        if (currentState is AuthState.Authenticated) {
            val sessionDuration = System.currentTimeMillis() - currentState.sessionStartTime
            analyticsLogger.logLogout(currentState.email, sessionDuration)
        }
        _authState.value = AuthState.Idle
    }
    
    /**
     * Reset to idle state
     */
    fun resetToIdle() {
        _authState.value = AuthState.Idle
    }
}

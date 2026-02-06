package com.example.lokalassignment.data

import kotlin.random.Random

/**
 * Manages OTP generation, validation, and storage
 * Uses in-memory Map to store OTP data per email
 */
class OtpManager {
    
    // In-memory storage: email -> OTP data
    private val otpStorage = mutableMapOf<String, OtpData>()
    
    /**
     * Generate a new 6-digit OTP for the given email
     * Invalidates any existing OTP for this email
     * 
     * @param email User's email address
     * @return Generated OTP string
     */
    fun generateOtp(email: String): String {
        val otp = Random.nextInt(100000, 999999).toString()
        val expiryTime = System.currentTimeMillis() + 60_000 // 60 seconds
        
        otpStorage[email] = OtpData(
            otp = otp,
            expiryTime = expiryTime,
            attemptsRemaining = 3
        )
        
        return otp
    }
    
    /**
     * Validate OTP for the given email
     * 
     * @param email User's email address
     * @param inputOtp OTP entered by user
     * @return ValidationResult indicating success or failure reason
     */
    fun validateOtp(email: String, inputOtp: String): ValidationResult {
        val otpData = otpStorage[email] ?: return ValidationResult.NoOtpFound
        
        // Check if expired
        if (otpData.isExpired()) {
            otpStorage.remove(email) // Clean up expired OTP
            return ValidationResult.Expired
        }
        
        // Check if attempts exhausted
        if (!otpData.hasAttemptsRemaining()) {
            otpStorage.remove(email) // Clean up
            return ValidationResult.AttemptsExhausted
        }
        
        // Validate OTP
        return if (otpData.otp == inputOtp) {
            otpStorage.remove(email) // Clean up on success
            ValidationResult.Success
        } else {
            // Decrement attempts
            otpStorage[email] = otpData.copy(attemptsRemaining = otpData.attemptsRemaining - 1)
            ValidationResult.Invalid(otpData.attemptsRemaining - 1)
        }
    }
    
    /**
     * Get remaining time for OTP in milliseconds
     * Returns null if no OTP exists for email
     */
    fun getRemainingTime(email: String): Long? {
        val otpData = otpStorage[email] ?: return null
        val remaining = otpData.expiryTime - System.currentTimeMillis()
        return if (remaining > 0) remaining else 0
    }
    
    /**
     * Get remaining attempts for email
     */
    fun getRemainingAttempts(email: String): Int {
        return otpStorage[email]?.attemptsRemaining ?: 0
    }
}

/**
 * Sealed class representing OTP validation results
 */
sealed class ValidationResult {
    object Success : ValidationResult()
    object NoOtpFound : ValidationResult()
    object Expired : ValidationResult()
    object AttemptsExhausted : ValidationResult()
    data class Invalid(val attemptsRemaining: Int) : ValidationResult()
}

package com.example.lokalassignment.data

import kotlin.random.Random

class OtpManager {
    
    private val otpStorage = mutableMapOf<String, OtpData>()
    
    fun generateOtp(email: String): String {
        val otp = Random.nextInt(100000, 999999).toString()
        val expiryTime = System.currentTimeMillis() + 60_000
        
        otpStorage[email] = OtpData(
            otp = otp,
            expiryTime = expiryTime,
            attemptsRemaining = 3
        )
        
        return otp
    }
    
    fun validateOtp(email: String, inputOtp: String): ValidationResult {
        val otpData = otpStorage[email] ?: return ValidationResult.NoOtpFound
        
        if (otpData.isExpired()) {
            otpStorage.remove(email)
            return ValidationResult.Expired
        }
        
        if (!otpData.hasAttemptsRemaining()) {
            otpStorage.remove(email)
            return ValidationResult.AttemptsExhausted
        }
        
        return if (otpData.otp == inputOtp) {
            otpStorage.remove(email)
            ValidationResult.Success
        } else {
            otpStorage[email] = otpData.copy(attemptsRemaining = otpData.attemptsRemaining - 1)
            ValidationResult.Invalid(otpData.attemptsRemaining - 1)
        }
    }
    
    fun getRemainingTime(email: String): Long? {
        val otpData = otpStorage[email] ?: return null
        val remaining = otpData.expiryTime - System.currentTimeMillis()
        return if (remaining > 0) remaining else 0
    }
    
    fun getRemainingAttempts(email: String): Int {
        return otpStorage[email]?.attemptsRemaining ?: 0
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    object NoOtpFound : ValidationResult()
    object Expired : ValidationResult()
    object AttemptsExhausted : ValidationResult()
    data class Invalid(val attemptsRemaining: Int) : ValidationResult()
}

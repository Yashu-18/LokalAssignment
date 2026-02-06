package com.example.lokalassignment.data

data class OtpData(
    val otp: String,
    val expiryTime: Long,
    val attemptsRemaining: Int = 3
) {
    fun isExpired(): Boolean = System.currentTimeMillis() > expiryTime
    
    fun hasAttemptsRemaining(): Boolean = attemptsRemaining > 0
}

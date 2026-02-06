package com.example.lokalassignment.data

/**
 * Data class representing OTP information for a specific email
 * 
 * @param otp The 6-digit OTP string
 * @param expiryTime Timestamp when OTP expires (System.currentTimeMillis() + 60000)
 * @param attemptsRemaining Number of validation attempts remaining (max 3)
 */
data class OtpData(
    val otp: String,
    val expiryTime: Long,
    val attemptsRemaining: Int = 3
) {
    /**
     * Check if OTP has expired
     */
    fun isExpired(): Boolean = System.currentTimeMillis() > expiryTime
    
    /**
     * Check if attempts are exhausted
     */
    fun hasAttemptsRemaining(): Boolean = attemptsRemaining > 0
}

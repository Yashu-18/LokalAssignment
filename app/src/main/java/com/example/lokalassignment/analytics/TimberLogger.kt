package com.example.lokalassignment.analytics

import timber.log.Timber

/**
 * Timber implementation of AnalyticsLogger
 * Logs all events to Logcat for debugging
 */
class TimberLogger : AnalyticsLogger {
    
    override fun logOtpGenerated(email: String) {
        Timber.d("📧 OTP Generated for: $email")
    }
    
    override fun logOtpValidationSuccess(email: String) {
        Timber.d("✅ OTP Validation Success for: $email")
    }
    
    override fun logOtpValidationFailure(email: String, reason: String) {
        Timber.e("❌ OTP Validation Failed for: $email | Reason: $reason")
    }
    
    override fun logLogout(email: String, sessionDuration: Long) {
        val minutes = sessionDuration / 60000
        val seconds = (sessionDuration % 60000) / 1000
        Timber.d("👋 Logout: $email | Session Duration: ${minutes}m ${seconds}s")
    }
}

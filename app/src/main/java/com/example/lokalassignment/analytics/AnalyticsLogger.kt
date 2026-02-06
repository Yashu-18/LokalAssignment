package com.example.lokalassignment.analytics

/**
 * Interface for logging analytics events
 * This abstraction allows us to switch logging implementations easily
 */
interface AnalyticsLogger {
    fun logOtpGenerated(email: String)
    fun logOtpValidationSuccess(email: String)
    fun logOtpValidationFailure(email: String, reason: String)
    fun logLogout(email: String, sessionDuration: Long)
}

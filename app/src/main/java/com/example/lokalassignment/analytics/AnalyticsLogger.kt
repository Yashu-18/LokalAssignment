package com.example.lokalassignment.analytics

interface AnalyticsLogger {
    fun logOtpGenerated(email: String, otp: String)
    fun logOtpValidationSuccess(email: String)
    fun logOtpValidationFailure(email: String, reason: String)
    fun logLogout(email: String, sessionDuration: Long)
}

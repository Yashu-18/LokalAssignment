package com.example.lokalassignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lokalassignment.analytics.AnalyticsLogger
import com.example.lokalassignment.data.OtpManager
import com.example.lokalassignment.viewmodel.AuthViewModel

/**
 * Factory for creating AuthViewModel with dependencies
 */
class AuthViewModelFactory(
    private val otpManager: OtpManager,
    private val analyticsLogger: AnalyticsLogger
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(otpManager, analyticsLogger) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

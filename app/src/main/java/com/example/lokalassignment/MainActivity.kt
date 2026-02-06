package com.example.lokalassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lokalassignment.analytics.TimberLogger
import com.example.lokalassignment.data.OtpManager
import com.example.lokalassignment.ui.LoginScreen
import com.example.lokalassignment.ui.OtpScreen
import com.example.lokalassignment.ui.SessionScreen
import com.example.lokalassignment.ui.theme.LokalAssignmentTheme
import com.example.lokalassignment.viewmodel.AuthState
import com.example.lokalassignment.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LokalAssignmentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthApp()
                }
            }
        }
    }
}

@Composable
fun AuthApp() {
    val otpManager = OtpManager()
    val analyticsLogger = TimberLogger()
    
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(otpManager, analyticsLogger)
    )
    
    val authState by viewModel.authState.collectAsState()
    
    when (val state = authState) {
        is AuthState.Idle -> {
            LoginScreen(
                onSendOtp = { email -> viewModel.sendOtp(email) },
                isLoading = false,
                errorMessage = null
            )
        }
        
        is AuthState.Loading -> {
            LoginScreen(
                onSendOtp = {},
                isLoading = true,
                errorMessage = null
            )
        }
        
        is AuthState.OtpSent -> {
            OtpScreen(
                email = state.email,
                expiryTime = state.expiryTime,
                onValidateOtp = { email, otp -> viewModel.validateOtp(email, otp) },
                onResendOtp = { email -> viewModel.sendOtp(email) },
                isLoading = false,
                errorMessage = null,
                attemptsRemaining = 3
            )
        }
        
        is AuthState.OtpError -> {
            LoginScreen(
                onSendOtp = { email -> viewModel.sendOtp(email) },
                isLoading = false,
                errorMessage = state.message
            )
        }
        
        is AuthState.Authenticated -> {
            SessionScreen(
                email = state.email,
                sessionStartTime = state.sessionStartTime,
                onLogout = { viewModel.logout() }
            )
        }
    }
}
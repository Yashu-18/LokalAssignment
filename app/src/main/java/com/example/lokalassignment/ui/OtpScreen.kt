package com.example.lokalassignment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    email: String,
    expiryTime: Long,
    onValidateOtp: (String, String) -> Unit,
    onResendOtp: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    attemptsRemaining: Int
) {
    var otp by remember { mutableStateOf(TextFieldValue("")) }
    var remainingSeconds by remember { mutableStateOf(60) }
    
    // Countdown timer
    LaunchedEffect(expiryTime) {
        while (remainingSeconds > 0) {
            delay(1000)
            val remaining = (expiryTime - System.currentTimeMillis()) / 1000
            remainingSeconds = if (remaining > 0) remaining.toInt() else 0
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Enter OTP",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "OTP sent to $email",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.text.length <= 6) otp = it },
            label = { Text("6-Digit OTP") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            enabled = !isLoading
        )
        
        // Timer and attempts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Time remaining: ${remainingSeconds}s",
                style = MaterialTheme.typography.bodySmall,
                color = if (remainingSeconds < 10) MaterialTheme.colorScheme.error 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (attemptsRemaining > 0) {
                Text(
                    text = "Attempts: $attemptsRemaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        Button(
            onClick = { onValidateOtp(email, otp.text) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            enabled = !isLoading && otp.text.length == 6 && remainingSeconds > 0
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Verify OTP")
            }
        }
        
        TextButton(
            onClick = { onResendOtp(email) },
            enabled = !isLoading
        ) {
            Text("Resend OTP")
        }
    }
}

package com.example.lokalassignment

import android.app.Application
import timber.log.Timber

/**
 * Application class for initializing Timber
 */
class LokalApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.d("LokalApplication initialized")
    }
}

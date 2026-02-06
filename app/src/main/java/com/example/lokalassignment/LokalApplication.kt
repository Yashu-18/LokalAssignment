package com.example.lokalassignment

import android.app.Application
import timber.log.Timber

class LokalApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.d("LokalApplication initialized")
    }
}

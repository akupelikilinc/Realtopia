package com.realtopia.game

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RealtopiaApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize any global configurations here
        // For example: Firebase, Analytics, etc.
    }
}

package com.example.addressex


import android.app.Application
import timber.log.Timber

class AppApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(CustomDebugTree())
    }

    class CustomDebugTree: Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String {
            return "${"debugLog"}:${element.fileName}:${element.lineNumber}"
        }
    }
}
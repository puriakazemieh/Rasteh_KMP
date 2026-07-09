package com.kazemieh.rasteh


import android.app.Application
import org.koin.android.ext.koin.androidContext

class RastehApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@RastehApplication)
        }
    }
}
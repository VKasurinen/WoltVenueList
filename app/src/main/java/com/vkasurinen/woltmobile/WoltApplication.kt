package com.vkasurinen.woltmobile

import android.app.Application
import com.vkasurinen.woltmobile.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WoltApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WoltApplication)
            modules(appModule)
        }
    }
}
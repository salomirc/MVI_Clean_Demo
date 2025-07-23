package com.example.mvi_clean_demo.di

import android.app.Application
import android.content.Context
import com.example.mvi_clean_demo.Repository
import com.example.mvi_clean_demo.viewmodels.DistancesViewModel
import com.example.mvi_clean_demo.viewmodels.TemperatureViewModel

// Custom Application class that needs to be specified
// in the AndroidManifest.xml file
class MyApplication : Application() {

    // Instance of AppContainer that will be used by all the Activities of the app
    private lateinit var _appContainer: AppContainer
    val appContainer: AppContainer
        get() = _appContainer

    override fun onCreate() {
        super.onCreate()
        _appContainer = AppContainer(applicationContext)
    }
}

class AppContainer(applicationContext: Context) {

    companion object {
        const val PREF_NAME = "MVIDemoSharedPreferences"
    }

    private val sharedPreferences = applicationContext.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    fun provideTemperatureViewModelFactory(initialTempValue: String): ViewModelFactory<TemperatureViewModel> {
        return ViewModelFactory {
            TemperatureViewModel(initialTempValue, Repository(sharedPreferences))
        }
    }

    fun provideDistancesViewModelFactory(): ViewModelFactory<DistancesViewModel> {
        return ViewModelFactory {
            DistancesViewModel(Repository(sharedPreferences))
        }
    }
}
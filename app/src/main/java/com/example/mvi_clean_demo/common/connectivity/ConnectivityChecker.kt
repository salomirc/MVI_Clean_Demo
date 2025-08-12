package com.example.mvi_clean_demo.common.connectivity

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface IConnectivityChecker {
    val isConnected: Boolean
}

@Singleton
class ConnectivityChecker @Inject constructor (
    @ApplicationContext private val applicationContext: Context
): IConnectivityChecker {
    private val connectivityManager by lazy { applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager }

    override val isConnected: Boolean
        get() {
            val currentNetwork = connectivityManager.activeNetwork
            val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
            return caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }
}

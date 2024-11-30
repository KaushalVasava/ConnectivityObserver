package com.kaushal.apps.internetobserver.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkObserverWithLifecycleObserver(
    private val context: Context,
    private val lifecycle: Lifecycle
) : DefaultLifecycleObserver {

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var connectivityManager: ConnectivityManager? = null
    private val _networkAvailableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val isConnectedFlow: StateFlow<Boolean> get() = _networkAvailableStateFlow

    init {
        lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        registerNetworkCallback()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        unregisterNetworkCallback()
    }

    private fun registerNetworkCallback() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            networkCallback = createNetworkCallback()

            val networkRequest = NetworkRequest.Builder()
                .addCapability(NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()

            connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
        }
    }

    private fun unregisterNetworkCallback() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                connectivityManager?.getNetworkCapabilities(network)?.let {
                    if (it.hasCapability(NET_CAPABILITY_INTERNET)) {
                        _networkAvailableStateFlow.tryEmit(true)
                    }
                }
            }

            override fun onLost(network: Network) {
                _networkAvailableStateFlow.tryEmit(false)
            }

            override fun onUnavailable() {
                _networkAvailableStateFlow.tryEmit(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, capabilities)
                if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    _networkAvailableStateFlow.tryEmit(true)
                } else {
                    _networkAvailableStateFlow.tryEmit(false)
                }
            }
        }
}

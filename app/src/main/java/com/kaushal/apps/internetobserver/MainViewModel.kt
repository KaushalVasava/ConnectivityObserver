package com.kaushal.apps.internetobserver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushal.apps.internetobserver.util.NetworkObserver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainViewModel(networkObserver: NetworkObserver): ViewModel() {

    val isConnected = networkObserver.isConnectedFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = false
    )
}
package com.kaushal.apps.internetobserver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kaushal.apps.internetobserver.ui.theme.InternetObserverTheme
import com.kaushal.apps.internetobserver.util.NetworkObserverWithLifecycleObserver

class MainActivity : ComponentActivity() {

    private lateinit var networkObserver: NetworkObserverWithLifecycleObserver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        networkObserver = NetworkObserverWithLifecycleObserver(this,lifecycle)
        setContent {
            InternetObserverTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                     /***
                      * Method 1: Using Flow
                      - In this method you need to observe flow using viewmodel
                       and manually observe lifecycle changes
                     */

//                    val viewModel = viewModel<MainViewModel> {
//                        MainViewModel(NetworkObserver(this@MainActivity))
//                    }
//                    val isConnected by viewModel.isConnected.collectAsState()

                    /***
                    * Method 2: Using Flow and LifeCycleObserver
                     - In this method you can directly observe stateflow in your view.
                     - It's lifecycle aware method. You don't need viewModel.
                    * */
                    val isConnected by networkObserver.isConnectedFlow.collectAsState()
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        isConnected = isConnected
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier, isConnected: Boolean = false) {
    val (message, color) = if (isConnected) {
        "Network is available" to Color.Green
    } else {
        "Network is unavailable" to Color.Red
    }
    Column(
        modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message, color = color, fontSize = 24.sp, fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InternetObserverTheme {
        Greeting()
    }
}
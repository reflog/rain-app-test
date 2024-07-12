package com.example.rainapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rainapp.ui.theme.RainAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            RainAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherScreen()
                }
            }
        }
    }

    @Composable
    fun WeatherScreen() {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        var location by remember { mutableStateOf<Location?>(null) }
        var weatherData by remember { mutableStateOf<WeatherData?>(null) }

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                    location = loc
                    coroutineScope.launch {
                        weatherData = loc?.let { WeatherApi.getWeatherData(it) }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Rainfall Graph", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(16.dp))
            weatherData?.let {
                RainfallGraph(data = it)
            }
        }
    }

    @Composable
    fun RainfallGraph(data: WeatherData) {
        // Implement the rainfall graph using Jetpack Compose
    }
}

package com.example.rainapp

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import com.example.rainapp.ui.theme.RainAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            coroutineScope.launch {
                val weatherData = location?.let { WeatherApi.getWeatherData(it) }
                val views = RemoteViews(context.packageName, R.layout.widget_layout)
                weatherData?.let {
                    // Update the widget layout with the weather data
                    // Implement the logic to update the widget views
                }
                appWidgetManager.updateAppWidget(appWidgetIds, views)
            }
        }
    }
}

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = WeatherWidgetGlance()
}

class WeatherWidgetGlance : GlanceAppWidget() {
    @Composable
    override fun Content() {
        RainAppTheme {
            WeatherWidgetContent()
        }
    }

    @Composable
    fun WeatherWidgetContent() {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        var weatherData by remember { mutableStateOf<WeatherData?>(null) }

        LaunchedEffect(Unit) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                coroutineScope.launch {
                    weatherData = location?.let { WeatherApi.getWeatherData(it) }
                }
            }
        }

        weatherData?.let {
            RainfallGraph(data = it)
        }
    }

    @Composable
    fun RainfallGraph(data: WeatherData) {
        // Implement the rainfall graph using Jetpack Compose
    }
}

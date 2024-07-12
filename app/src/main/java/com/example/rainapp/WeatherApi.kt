package com.example.rainapp

import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object WeatherApi {
    private const val BASE_URL = "https://api.open-meteo.com/v1/forecast"
    private const val PARAMETERS = "hourly=temperature_2m,precipitation_probability,precipitation&timezone=Europe%2FLondon&past_days=1&forecast_days=3"

    suspend fun getWeatherData(location: Location): WeatherData? {
        val urlString = "$BASE_URL?latitude=${location.latitude}&longitude=${location.longitude}&$PARAMETERS"
        return withContext(Dispatchers.IO) {
            try {
                val response = URL(urlString).readText()
                parseWeatherData(response)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun parseWeatherData(response: String): WeatherData? {
        val jsonObject = JSONObject(response)
        val hourly = jsonObject.getJSONObject("hourly")
        val temperature = hourly.getJSONArray("temperature_2m")
        val precipitationProbability = hourly.getJSONArray("precipitation_probability")
        val precipitation = hourly.getJSONArray("precipitation")

        val temperatureList = mutableListOf<Double>()
        val precipitationProbabilityList = mutableListOf<Double>()
        val precipitationList = mutableListOf<Double>()

        for (i in 0 until temperature.length()) {
            temperatureList.add(temperature.getDouble(i))
            precipitationProbabilityList.add(precipitationProbability.getDouble(i))
            precipitationList.add(precipitation.getDouble(i))
        }

        return WeatherData(
            temperature = temperatureList,
            precipitationProbability = precipitationProbabilityList,
            precipitation = precipitationList
        )
    }
}

data class WeatherData(
    val temperature: List<Double>,
    val precipitationProbability: List<Double>,
    val precipitation: List<Double>
)

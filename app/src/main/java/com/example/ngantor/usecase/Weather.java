package com.example.ngantor.usecase;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.ngantor.BuildConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Weather {
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final Context context;

    private static final String API_KEY = BuildConfig.WEATHER_API_KEY;
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    public Weather(Context context) {
        this.context = context;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void fetchWeather(LocationWeatherCallback callback) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onError("Location permission not granted");
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            fetchWeatherData(latitude, longitude, callback);
                        } else {
                            callback.onError("Failed to get location");
                        }
                    }
                });
    }

    private void fetchWeatherData(double latitude, double longitude, LocationWeatherCallback callback) {
        new Thread(() -> {
            try {
                String urlString = API_URL + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                String weather = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");
                double temperature = jsonResponse.getJSONObject("main").getDouble("temp");

                // Convert temperature from Kelvin to Celsius
                temperature = temperature - 273.15;

                callback.onWeatherResult(weather, temperature);
            } catch (Exception e) {
                callback.onError("Failed to fetch weather data: " + e.getMessage());
            }
        }).start();
    }

    public interface LocationWeatherCallback {
        void onWeatherResult(String weatherDescription, double temperature);
        void onError(String error);
    }
}

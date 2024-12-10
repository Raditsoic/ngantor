package com.example.ngantor;

import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.ngantor.fragment.HomeFragment;
import com.example.ngantor.fragment.LogsFragment;
import com.example.ngantor.usecase.Weather;

public class Ngantor extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    TextView timeUntilBedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeUntilBedText = findViewById(R.id.time_until_bed);

        requestLocationPermission();

        // Referensi tombol
        Button btnHome = findViewById(R.id.btn_home);
        Button btnLogs = findViewById(R.id.btn_logs);

        // Set fragment default (Home)
        replaceFragment(new HomeFragment());

        // Tombol Home
        btnHome.setOnClickListener(v -> replaceFragment(new HomeFragment()));

        // Tombol Logs
        btnLogs.setOnClickListener(v -> replaceFragment(new LogsFragment()));
    }

    // ganti fragment
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchWeather();
        }
    }

    private void fetchWeather() {
        Weather weather = new Weather(this);
        weather.fetchWeather(new Weather.LocationWeatherCallback() {
            @Override
            public void onWeatherResult(String weatherDescription, double temperature) {
                runOnUiThread(() -> {
                    String message;
                    switch (weatherDescription.toLowerCase()) {
                        case "sunny":
                            message = "It's sunny today, have a good day!!";
                            break;
                        case "rain":
                        case "rainy":
                            message = "It's raining, sleep might be best for now";
                            break;
                        case "clear":
                            message = "Good day today, don't forget your sunscreen";
                            break;
                        default:
                            message = "Have a good day!!";
                            break;
                    }
                    timeUntilBedText.setText(message);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> timeUntilBedText.setText("Failed to fetch weather data: " + error));
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchWeather();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}

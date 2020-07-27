package com.example.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;






public class MainActivity extends Activity {
    EditText edtCityName;
    Button btnProcessForecast;
    Button btnGPS;
    String json;
    double lat;
    double lon;
    private LocationManager locationManager;
    private LocationListener locationListener;
    static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtCityName = findViewById(R.id.edtCityName);
        btnGPS = findViewById(R.id.btnGPS);

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("checkbox", false) == true) {

            Intent intent = new Intent(MainActivity.this, WeatherMain.class);
            intent.putExtra("json", sharedPreferences.getString("json", ""));
            startActivity(intent);
        }

        btnProcessForecast = findViewById(R.id.btnDubao);

        btnProcessForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForecastWeather();
            }
        });

        edtCityName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                ForecastWeather();
                return false;
            }
        });
        //get location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&APPID=94ee50782c5418049e39f32e1294d58d";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Intent intent = new Intent(MainActivity.this, WeatherMain.class);
                                intent.putExtra("weatherData", response.toString());
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this,"Không định vị được vị trí hiện tại, vui lòng chọn tên thành phố", Toast.LENGTH_LONG).show();
                            }
                        });
                requestQueue.add(jsonObjectRequest);
                locationManager.removeUpdates(locationListener);
                locationManager = null;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                locationManager.removeUpdates(locationListener);
                locationManager = null;
            }
        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            } else {
                configureButton();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
        }
    }

    private void configureButton() {
        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, locationListener);

            }
        });

    }


    private void ForecastWeather() {
        String cityName = edtCityName.getText().toString().trim();
        if(cityName.equals("")){
            Toast.makeText(
                    MainActivity.this,
                    "Chưa nhập tên thành phố",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(
                    MainActivity.this,
                    "Đang trong quá trình xử lý thông tin thành phố " + cityName,
                    Toast.LENGTH_SHORT).show();
            ProcessCity(cityName);
        }
    }

    private void ProcessCity(final String cityName) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&APPID=94ee50782c5418049e39f32e1294d58d";
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Intent intent = new Intent(MainActivity.this, WeatherMain.class);
                                intent.putExtra("weatherData", response.toString());
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this,"Không tìm thấy thành phố " + cityName, Toast.LENGTH_LONG).show();
                            }
                        });

        // Chính thức gọi tới server
        requestQueue.add(jsonObjectRequest);
    }
}

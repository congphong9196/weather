package com.example.weather;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class WeatherMain extends Activity {
    private TextView txtWeather;
    private TextView txtCityName, txtTemp, txtDes, txtTime, txtTips;
    private ImageView imgWeather;
    private ImageButton btnSettings;

    private String CityName;
    static String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);
        txtWeather = findViewById(R.id.txtWeather);
        txtCityName = findViewById(R.id.txtCityName);
        txtTemp = findViewById(R.id.txtTemp);
        txtDes = findViewById(R.id.txtDes);
        imgWeather = findViewById(R.id.imgWeather);
        txtTime = findViewById(R.id.txtTime);
        txtTips = findViewById(R.id.txtTips);
        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowMenu();
            }
        });

        Intent intent = getIntent();

        try {
            ProcessWeather(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ShowMenu(){

        PopupMenu popupMenu = new PopupMenu(this, btnSettings);
        popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu5:
                        Intent intent = new Intent(WeatherMain.this, FiveDaysWeather.class);
                        intent.putExtra("cityname", CityName);
                        intent.putExtra("weatherData", getIntent().getStringExtra("weatherData"));
                        startActivity(intent);
                        break;
                    case R.id.menu1:
                        Intent intent1 = new Intent(WeatherMain.this, TodayWeatherActivity.class);
                        intent1.putExtra("cityname", CityName);
                        intent1.putExtra("weatherData", getIntent().getStringExtra("weatherData"));
                        startActivity(intent1);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void ProcessWeather(Intent intent) throws JSONException {
        JSONObject response = new JSONObject(intent.getStringExtra("weatherData"));
        JSONArray weather = response.getJSONArray("weather");

        final String weatherSummary = weather.getJSONObject(0).getString("main");
        double lat = response.getJSONObject("coord").getDouble("lat");
        double lon = response.getJSONObject("coord").getDouble("lon");
        String weatherDes = response.getJSONArray("weather").getJSONObject(0).getString("description");
        double windSpeed = response.getJSONObject("wind").getDouble("speed");
        double temp = response.getJSONObject("main").getDouble("temp");
        final String cityName = response.getString("name");
        String country = response.getJSONObject("sys").getString("country");
        final long timeEpoch = response.getLong("dt");
        final DateTime date = new DateTime(timeEpoch * 1000L);
        String icon = response.getJSONArray("weather").getJSONObject(0).getString("icon");

        // Translate to celcius degree
        temp = temp - 273;

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        String outText = "";
        outText += "Thời tiết: " + weatherSummary + "\n";
        outText += "Thời tiết chi tiết: " + weatherDes + "\n";
        outText += "Kinh độ: " + Double.toString(lat) + "\n";
        outText += "Vĩ độ: " + Double.toString(lon) + "\n";
        outText += "Tốc độ gió: " + Double.toString(windSpeed) + "\n";
        outText += "Nhiệt độ (°C): " + Double.toString(temp) + "\n";
        Log.d("NCP", outText);

        TextView txtCityName = findViewById(R.id.txtCityName);
        txtCityName.setText(String.format("%s (%s)", cityName, country));
        CityName = cityName;

        TextView txtWeather = findViewById(R.id.txtWeather);
        txtWeather.setText(Translate(weatherSummary));

        TextView txtTemp = findViewById(R.id.txtTemp);
        txtTemp.setText(df.format(temp) + "°C");

        final TextView txtTime = findViewById(R.id.txtTime);

        TextView txtTips = findViewById(R.id.txtTips);
        txtTips.setText(GetTips(weatherSummary));

        String link = String.format("http://openweathermap.org/img/w/%s.png", icon);
        Glide.with(WeatherMain.this)
                .load(link)
                .centerCrop()
                .placeholder(R.drawable.load)
                .into(imgWeather);

        /// Set background image

        ////////////// Get timezone at lat and lon
        String strLat = Double.toString(lat);
        String strLon = Double.toString(lon);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/timezone/json?location=" + strLat + "," + strLon + "&timestamp=1521365400&key=AIzaSyADDvj4VfAlduDdmGlhbtRYNQd-UH_Z9HQ";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            id = response.getString("timeZoneId");
                            String strTimeZone = response.getString("timeZoneId");
                            DateTimeZone timeZone = DateTimeZone.forID(strTimeZone);
                            DateTime dateAtLatLon = new DateTime(timeEpoch * 1000L, timeZone);

                            txtTime.setText(String.format("Cập nhật: %s", dateAtLatLon.toString()));

                            int currentHours = dateAtLatLon.getHourOfDay();
                            setBG(currentHours, weatherSummary.toLowerCase());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WeatherMain.this, "Không lấy được time zone của thành phố" + cityName, Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private String Translate(String input) {
        String result = input;
        switch (input) {
            case "Clouds":
                result = "Trời nhiều mây";
                break;
            case "Clear":
                result = "Trời quang";
                break;
            case "Mist":
                result = "Trời nhiều sương mù";
                break;
            case "Haze":
                result = "Mưa phùn";
                break;
            case "Rain":
                result = "Mưa";
                break;
            case "Fog":
                result = "Sương mù dày đặc";
                break;
            case "Snow":
                result = "Tuyết rơi";
                break;
            case "Smoke":
                result = "Khói bụi";
                break;
        }
        return result;
    }

    private String GetTips(String weather) {
        String tipsResult = "";
        if (weather.equals("Clear")) {
            tipsResult =
                    "-  Trời thoáng mát,nhiều mây.\n " +
                            "-  Thời tiết đẹp,thích hợp cho các hoạt động ngoài trời.";
        }
        if (weather.equals("Mist")) {
            tipsResult =
                    "-  Trời có sương mù,tầm nhìn xa giảm \n " +
                            "-  Khi ra ngoài nên mang theo mũ,mặt nạ và áo khoác.";
        }
        if (weather.equals("Haze")) {
            tipsResult =
                    "-  Trời sương mù và có thể có mưa phùn \n" +
                            "-  Khi ra ngoài nên mang theo ô,mũ,áo khoác.";
        }
        if (weather.equals("Rain")) {
            tipsResult =
                    "-  Mưa rải rác và có thể có lớn cục bộ.\n" +
                            "-  Khi đi ra ngoài, nên mang theo ô và áo mưa để tránh bị ướt.";
        }
        if (weather.equals("Clouds")) {
            tipsResult =
                    "-  Trời nhiều mây\n" +
                            "-  Thời tiết mát mẻ\n" +
                            "-  Thích hợp cho các buổi picnic,du lịch,chụp ảnh.";
        }
        if (weather.equals("Snow")) {
            tipsResult =
                    "-  Tuyết rơi nhiều.\n" +
                            "-  Nhớ giữ ấm cơ thể.\n" +
                            "-  Mang giày có độ ma sát cao để tránh trơn trượt\n" +
                            "-  Có thể mang ô khi đi ra ngoài.";

        }
        if (weather.equals("Fog")) {

            tipsResult =
                    "-  Có sương mù,tầm nhìn giảm.\n" +
                            "-  Tầm nhìn hạn chế, đi chậm hơn nếu bạn di chuyển bằng ô tô.";
        }
        if (weather.equals("Smoke")) {

            tipsResult =
                    "-  Nhiều khói bụi.\n" +
                            "-  Nên mang theo khẩu trang,mặt nạ.\n" +
                            "-  Tầm nhìn hạn chế.";
        }
        return tipsResult;
    }


    void getBG(String namePic) {
        int id = getResources().getIdentifier(namePic.toLowerCase(), "drawable", getPackageName());
        ImageView backgroundImage = findViewById(R.id.imgWeatherBackground);
        Glide
                .with(WeatherMain.this)
                .load(id)
                .into(backgroundImage);
    }

    void setBG(int hour, String mainWeather) {

        if (hour >= 6 && hour < 18) {
            getBG(mainWeather + "day");
            txtCityName.setTextColor(Color.BLUE);
            txtWeather.setTextColor(Color.RED);
            txtTemp.setTextColor(Color.BLACK);
            txtTips.setTextColor(Color.BLUE);
            txtTime.setTextColor(Color.BLACK);
            txtDes.setTextColor(Color.RED);
        }
        if (hour < 6 || hour >= 18) {
            getBG(mainWeather + "night");
            txtCityName.setTextColor(Color.GREEN);
            txtWeather.setTextColor(Color.YELLOW);
            txtTemp.setTextColor(Color.YELLOW);
            txtTips.setTextColor(Color.GREEN);
            txtTime.setTextColor(Color.YELLOW);
            txtDes.setTextColor(Color.YELLOW);
        }
    }

}

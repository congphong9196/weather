package com.example.weather;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TodayWeatherActivity extends Activity {
    ConstraintLayout layout;
    TextView txtThoiGian, txtMatTroi, txtMatTrang, txtKhoangNhietDo, txtNhietDoTB, txtUV, txtDoAm, txtThoiTiet, txtGio, txtTamNhinTB, txtLuongMua;
    ImageView imgWeather;
    String celsius = "\u00b0" + "C";
    JSONArray conditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_weather);
        LoadConditions();
        //      layout=findViewById(R.id.layout);

        txtThoiGian = findViewById(R.id.txtThoiGian);
        txtMatTroi = findViewById(R.id.txtMatTroi);
        txtMatTrang = findViewById(R.id.txtMatTrang);
        txtKhoangNhietDo = findViewById(R.id.txtKhoangNhietDo);
        txtNhietDoTB = findViewById(R.id.txtNhietDoTB);
        txtUV = findViewById(R.id.txtUV);
        txtDoAm = findViewById(R.id.txtDoAm);
        txtThoiTiet = findViewById(R.id.txtThoiTiet);
        txtGio = findViewById(R.id.txGio);
        txtTamNhinTB = findViewById(R.id.txtTamNhinTB);
        txtLuongMua = findViewById(R.id.txtLuongMua);
        imgWeather = findViewById(R.id.imgWeather);

        Intent intent = getIntent();
        JSONObject response1 = null;

        try {
            response1 = new JSONObject(intent.getStringExtra("weatherData"));
            final String mainWeather = response1.getJSONArray("weather").getJSONObject(0).getString("main");
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject coord = response1.getJSONObject("coord");
            double lat = coord.getDouble("lat");
            double lon = coord.getDouble("lon");
            final long timeEpoch = response1.getLong("dt");

            String url = "https://maps.googleapis.com/maps/api/timezone/json?location=" + lat + "," + lon + "&timestamp=1521365400&key=AIzaSyADDvj4VfAlduDdmGlhbtRYNQd-UH_Z9HQ";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String strTimeZone = response.getString("timeZoneId");
                                DateTimeZone timeZone = DateTimeZone.forID(strTimeZone);
                                DateTime dateAtLatLon = new DateTime(timeEpoch * 1000L, timeZone);

                                int currentHours = dateAtLatLon.getHourOfDay();
                                setBG(currentHours, mainWeather.toLowerCase());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
            requestQueue.add(jsonObjectRequest);

            String CityName = intent.getStringExtra("cityname");
            Toast.makeText(this, CityName, Toast.LENGTH_SHORT).show();
            String[] tach = CityName.split(" ");
            int n = tach.length;
            String a = "";
            for (int i = 0; i < n; i++)
                a += tach[i];
            url = "http://api.weatherstack.com/forecast?access_key=ecff120bec6be612f942d26a5c931c29&query=" + lat + "," + lon + "&forecast_days=0";
//            url = "http://api.apixu.com/v1/forecast.json?key=6fbbf712221e45498d6172428182703&q=" + lat + "," + lon;
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject forecast_all_days = response.getJSONObject("forecast");
                                JSONObject forecast = forecast_all_days.getJSONObject(forecast_all_days.keys().next());
                                JSONObject current = response.getJSONObject("current");
                                String date = response.getJSONObject("location").getString("localtime");
                                String avgTemp = forecast.getString("avgtemp");
                                String maxminTemp =
                                        forecast.getString("mintemp") + celsius + " - " +
                                        forecast.getString("maxtemp") + celsius;
                                String[] s = date.split(" ")[0].split("-");
                                date = s[2] + "/" + s[1] + "/" + s[0];
                                JSONObject astro = forecast.getJSONObject("astro");
                                String stringMatTroi = "Mặt trời lên : " + astro.getString("sunrise") + " " + "-" + " " + astro.getString("sunset");
                                String stringMatTrang = "Mặt trăng lên : " + astro.getString("moonrise") + " " + "-" + " " + astro.getString("moonset");
                                String avgHum = current.getInt("humidity") + "";
                                String Uv = forecast.getString("uv_index");
                                String stringCon = current.getJSONArray("weather_descriptions").getString(0);
                                String icon = current.getJSONArray("weather_icons").getString(0);
                                String win = current.getInt("wind_speed") + " km/h";
                                String degree = current.getInt("wind_degree") + " celsius";
                                String precip = current.getInt("precip") + " mm";

                                txtKhoangNhietDo.setText(maxminTemp);
                                txtThoiGian.setText(date);
                                txtMatTroi.setText(stringMatTroi);
                                txtMatTrang.setText(stringMatTrang);
                                txtNhietDoTB.setText(avgTemp + celsius);
                                txtDoAm.setText("Độ ẩm: " + avgHum + "%");
                                txtUV.setText("Độ UV: " + Uv);

                                String weather = "";
                                for (int i = 0; i < conditions.length(); i++) {
                                    JSONObject condition1 = conditions.getJSONObject(i);
                                    String dayOrg = condition1.getString("day");
                                    String nightOrg = condition1.getString("night");

                                    if (dayOrg.toLowerCase().equals(stringCon.toLowerCase()) ||
                                            nightOrg.toLowerCase().equals(stringCon.toLowerCase())) {
                                        JSONArray languages = condition1.getJSONArray("languages");
                                        JSONObject vietnamObj = null;
                                        for (int j = 0; j < languages.length(); j++) {
                                            if (languages.getJSONObject(j).getString("lang_name").equals("Vietnamese")) {
                                                vietnamObj = languages.getJSONObject(j);
                                                break;
                                            }
                                        }
                                        if (vietnamObj != null) {
                                            if (dayOrg.toLowerCase().equals(stringCon.toLowerCase())) {
                                                weather = vietnamObj.getString("day_text");
                                            } else {
                                                weather = vietnamObj.getString("night_text");
                                            }
                                        }
                                    }
                                    if (!weather.equals("")) {
                                        break;
                                    }
                                }

                                if (weather.equals("")) {
                                    weather = stringCon;
                                }

                                if (weather.equals("Patchy rain possible")) {
                                    weather = "Mưa phùn nhẹ";
                                }
                                if (weather.equals("Thundery outbreaks possible")) {
                                    weather = "Sấm sét có thể bùng phát";
                                }

                                txtThoiTiet.setText(weather);

                                txtGio.setText("Tốc độ gió trung bình: " + win);
                                txtTamNhinTB.setText("Hướng gió: " + degree);
                                txtLuongMua.setText("Lượng mưa trung bình: " + precip);
                                Glide.with(TodayWeatherActivity.this).load(icon).centerCrop().placeholder(R.drawable.load).into(imgWeather);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(TodayWeatherActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void LoadConditions() {
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.conditions);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            conditions = new JSONArray(new String(b));
        } catch (Exception e) {
            Log.d("NCP", "Can't read from file conditions.json");
        }
    }

    void getBG(String namePic) {
        int id = getResources().getIdentifier(namePic.toLowerCase(), "drawable", getPackageName());
        ImageView backgroundImage = findViewById(R.id.imgBackground);
        Glide
                .with(TodayWeatherActivity.this)
                .load(id)
                .into(backgroundImage);
    }

    void setBG(int hour, String mainWeather) {

        if (hour >= 6 && hour < 18) {
            getBG(mainWeather + "day");
            txtKhoangNhietDo.setTextColor(Color.BLUE);
            txtThoiGian.setTextColor(Color.BLACK);
            txtMatTroi.setTextColor(Color.RED);
            txtMatTrang.setTextColor(Color.RED);
            txtNhietDoTB.setTextColor(Color.BLACK);
            txtDoAm.setTextColor(Color.RED);
            txtUV.setTextColor(Color.RED);
            txtThoiTiet.setTextColor(Color.BLUE);
            txtGio.setTextColor(Color.RED);
            txtTamNhinTB.setTextColor(Color.RED);
            txtLuongMua.setTextColor(Color.RED);
        }
        if (hour < 6 || hour >= 18) {
            getBG(mainWeather + "night");
            txtKhoangNhietDo.setTextColor(Color.YELLOW);
            txtThoiGian.setTextColor(Color.RED);
            txtMatTroi.setTextColor(Color.RED);
            txtMatTrang.setTextColor(Color.RED);
            txtNhietDoTB.setTextColor(Color.BLACK);
            txtDoAm.setTextColor(Color.RED);
            txtUV.setTextColor(Color.RED);
            txtThoiTiet.setTextColor(Color.GREEN);
            txtGio.setTextColor(Color.YELLOW);
            txtTamNhinTB.setTextColor(Color.YELLOW);
            txtLuongMua.setTextColor(Color.YELLOW);
        }
    }
}

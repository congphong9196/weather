package com.example.weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FiveDaysWeather extends Activity {
    ConstraintLayout myLayout;
    String api, apilist;
    ListView listView;
    //  SharedPreferences sharedPreferences;
    ArrayList<Object> objects;
    Adapter adapter;
    String celsius = "\u00b0" + "C";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fivedays_weather);

        Intent intent=getIntent();
        try {
            JSONObject response = new JSONObject(intent.getStringExtra("weatherData"));
            Double lat = response.getJSONObject("coord").getDouble("lat");
            Double lon = response.getJSONObject("coord").getDouble("lon");
            GetWeatherOfLatLon(lat, lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void GetWeatherOfLatLon(Double lat, Double lon) {
        Toast.makeText(
                this,
                "Lat " + lat.toString() + " | Lon " + lon.toString(),
                Toast.LENGTH_SHORT).show();

        listView = findViewById(R.id.listViewMain);
        String url =String.format("http://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&APPID=94ee50782c5418049e39f32e1294d58d", lat.toString(), lon.toString());
        ;

        RequestQueue queue = Volley.newRequestQueue(FiveDaysWeather.this);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        api = response.toString();
                        try {

                            JSONArray list = response.getJSONArray("list");
                            apilist = list.toString();
                            objects = new ArrayList<>();
                            int dayCheck=1000;
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject jsonObject = list.getJSONObject(i);
                                String datetime = jsonObject.getString("dt_txt");

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date dateC = df.parse(datetime);
                                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH);
                                df2.setTimeZone(TimeZone.getTimeZone(WeatherMain.id));
                                String formattedDate2 = df2.format(dateC);


                                String[] phantich = formattedDate2.split(" ");
                                String time = phantich[1];
                                String date = phantich[0];
                                phantich=date.split("/");
                                String day=phantich[0];


                                JSONObject main = jsonObject.getJSONObject("main");
                                JSONArray weather = jsonObject.getJSONArray("weather");
                                JSONObject weatherObject = weather.getJSONObject(0);
                                if(Integer.parseInt(day)!=dayCheck){
                                    objects.add(new Header(date));
                                    dayCheck=Integer.parseInt(day);
                                }
                                objects.add(new Weather(
                                        (main.getInt("temp") - 273) + celsius,
                                        time,
                                        date,
                                         "hum:"+ main.getInt("humidity") + "%",
                                        weatherObject.getString("main"),
                                        weatherObject.getString("description"),
                                        weatherObject.getString("icon")));
                            }

                            adapter = new Adapter(objects, FiveDaysWeather.this);
                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            Toast.makeText(FiveDaysWeather.this, "No Internet", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FiveDaysWeather.this, "No Internet", Toast.LENGTH_LONG).show();
                    }
                });
        queue.add(jsonObjectRequest);
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
}

package com.example.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {
    private ArrayList<Object> list;
    private LayoutInflater inflater;
    private static final int WEATHER_ITEM = 2;
    private static final int HEADER_ITEM = 1;
    private Context context;

    public Adapter(ArrayList<Object> weathers, Context context) {
        this.list = weathers;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof Header)
            return HEADER_ITEM;
        else return WEATHER_ITEM;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case WEATHER_ITEM:
                convertView = inflater.inflate(R.layout.weather_5_days, null);
                TextView txtNhietDo = convertView.findViewById(R.id.txtNhietDo);
                TextView txtThoiGian = convertView.findViewById(R.id.txtGio);
                TextView txtDoAm = convertView.findViewById(R.id.txtDoAm);
                TextView txtNgay = convertView.findViewById(R.id.txtNgay);
                TextView txtThoiTiet = convertView.findViewById(R.id.txtThoiTiet);
                TextView txtTongQuan = convertView.findViewById(R.id.txtTongQuan);
                ImageView imgThoiTiet = convertView.findViewById(R.id.imgThoiTiet);

                Weather weather = (Weather) list.get(position);
                txtNhietDo.setText(weather.getTemp());
                txtThoiGian.setText(weather.getTime());
                txtNgay.setText(weather.getDate());
                txtDoAm.setText(weather.getHumidity());
                txtThoiTiet.setText(weather.getWeather());
                txtTongQuan.setText(weather.getDescription());
                String link = String.format("http://openweathermap.org/img/w/%s.png", weather.getIcon());
                Glide.with(context).load(link).centerCrop().placeholder(R.drawable.load).into(imgThoiTiet);
                break;
            case HEADER_ITEM:
                convertView = inflater.inflate(R.layout.activity_header, null);
                TextView Header_date = convertView.findViewById(R.id.txtHeaderDate);
                Header header = (Header) list.get(position);
                Header_date.setText(header.getDate());
                break;
        }

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_list);
        convertView.startAnimation(animation);
        return convertView;
    }
}
package com.example.songzhihao.myweathertext.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.songzhihao.myweathertext.R;

import java.util.List;

public class CitylistAdapter extends ArrayAdapter<Citylist> {
    private int resourceId;

    public CitylistAdapter(Context context, int textViewResourceId, List<Citylist> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Citylist citylist = getItem(position);

        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        } else {
            view = convertView;
        }
        ImageView weatherImage = view.findViewById(R.id.weather_image);
        TextView areaName = view.findViewById(R.id.areaName_tv);
        TextView nowTemperature = view.findViewById(R.id.nowTem_tv);
        TextView maxLowTemperature = view.findViewById(R.id.maxLowTem_tv);
        weatherImage.setImageResource(citylist.getImageId());
        areaName.setText(citylist.getAreaName());
        nowTemperature.setText(citylist.getNowTemperature());
        maxLowTemperature.setText(citylist.getMaxLowTemperature());
        return view;
    }
}

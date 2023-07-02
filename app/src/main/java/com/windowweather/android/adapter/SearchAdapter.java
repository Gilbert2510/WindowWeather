package com.windowweather.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.windowweather.android.R;
import com.windowweather.android.db.City;
import com.windowweather.android.db.CitySearch;

import java.util.List;

public class SearchAdapter extends ArrayAdapter<CitySearch> {
    private int resourceId;

    public SearchAdapter(@NonNull Context context, int resource, @NonNull List<CitySearch> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CitySearch search =getItem(position);
        String searchName=search.getCityName()+"-"+search.getCityAdm1()+"-"+search.getCityAdm2()+"-"+search.getCityCountry();
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView cityName=view.findViewById(R.id.activity_search_item_city);
        cityName.setText(searchName);
        return view;
    }
}

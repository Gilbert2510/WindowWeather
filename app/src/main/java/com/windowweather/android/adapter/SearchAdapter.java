package com.windowweather.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.windowweather.android.R;
import com.windowweather.android.db.CitySearch;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<CitySearch> citySearchList;
    private List<CitySearch> backSearchList;
    MyFilter mFilter;

    public SearchAdapter(Context context, List<CitySearch> citySearchList) {
        this.context = context;
        this.citySearchList = citySearchList;
        this.backSearchList = citySearchList;
    }

    public SearchAdapter() {

    }

    @Override
    public int getCount() {
        return backSearchList.size();
    }

    @Override
    public Object getItem(int position) {
        return backSearchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView areaName;
        TextView cityId;
        TextView cityName;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.activity_search_item, null);
            holder = new ViewHolder();
            holder.areaName = convertView.findViewById(R.id.activity_search_item_city);
            holder.cityId=convertView.findViewById(R.id.activity_search_item_id);
            holder.cityName=convertView.findViewById(R.id.activity_search_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.areaName.setText(backSearchList.get(position).getAreaName());
        holder.areaName.setTextSize(15f);
        holder.cityId.setText(backSearchList.get(position).getCityId());
        holder.cityName.setText(backSearchList.get(position).getCityName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new MyFilter();
        }
        return mFilter;
    }

    /**
     * 定义过滤器的类来定义过滤规则
     */
    class MyFilter extends Filter {
        //在performFiltering(CharSequence charSequence)这个方法中定义过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CitySearch> list = new ArrayList<>();
            if (!TextUtils.isEmpty(constraint)) {
                //当过滤的关键字不为空的时候，把符合条件的数据对象添加到集合中
                for (CitySearch search : citySearchList) {
                    if (search.getAreaName().contains(constraint)) {
                        //要匹配的item中的view
                        list.add(search);
                    }
                }
            }
            FilterResults results = new FilterResults();

            results.values = list;
            results.count = list.size();
            return results;
        }

        //在publishResults方法中让适配器更新界面
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            backSearchList = (List<CitySearch>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged(); //通知数据发生了改变
            } else {
                notifyDataSetInvalidated(); //通知数据失效
            }
        }
    }

}

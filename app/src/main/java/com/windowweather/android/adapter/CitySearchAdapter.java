package com.windowweather.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

public class CitySearchAdapter extends BaseAdapter implements Filterable {
    private int resourceId;
    private Context context;
    private List<CitySearch> citySearchList;
    private List<CitySearch> backSearchList;
    MyFilter mFilter;

    public CitySearchAdapter(Context context, List<CitySearch> citySearchList) {
        this.context = context;
        this.citySearchList = citySearchList;
    }

    public CitySearchAdapter() {

    }

    @Override
    public int getCount() {
        return citySearchList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder {
        TextView areaName;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null) {
            convertView= LayoutInflater.from(context).inflate(R.layout.activity_search_item,null);
            holder=new ViewHolder();
            holder.areaName=convertView.findViewById(R.id.activity_search_item_city);
            convertView.setTag(holder);
        } else {
            holder=(ViewHolder) convertView.getTag();
        }
        holder.areaName.setText(citySearchList.get(position).getAreaName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(mFilter==null) {
            mFilter=new MyFilter();
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
            FilterResults results=new FilterResults();
            List<CitySearch> list;
            if(TextUtils.isEmpty(constraint)) {
                //当过滤的关键字为空的时候，则显示所有的数据
                list=backSearchList;
            } else {
                //否则把符合条件的数据对象添加到集合中
                list=new ArrayList<>();
                for(CitySearch search:backSearchList) {
                    if(search.getAreaName().contains(constraint)) {
                        //要匹配的item中的view
                        list.add(search);
                    }
                }
            }
            results.values=list;
            results.count=list.size();
            return results;
        }
        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            citySearchList=(List<CitySearch>) results.values;
            if(results.count>0) {
                notifyDataSetChanged(); //通知数据发生了改变
            } else {
                notifyDataSetInvalidated(); //通知数据失效
            }
        }
    }
}

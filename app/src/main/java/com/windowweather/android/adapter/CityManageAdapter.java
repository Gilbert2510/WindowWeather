package com.windowweather.android.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.windowweather.android.R;
import com.windowweather.android.db.City;
import com.windowweather.android.util.CurrentDateUtils;

import java.util.List;

public class CityManageAdapter extends RecyclerView.Adapter<CityManageAdapter.ViewHolder> {
    private final List<City> mCityList;
    private static onItemClickListener mClickListener;

    /**
     * 为Activity提供设置OnItemClickListener的接口
     *
     * @param itemClickListener
     */
    public void setOnItemClickListener(onItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    /**
     * 定义接口,在activity里面使用setOnItemClickListener方法并创建此接口的对象、实现其方法
     */
    public interface onItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView manageItemArea;
        private final TextView manageItemTemp;
        private final CardView manageItemCardView;

        public ViewHolder(View view) {
            super(view);
            manageItemArea = view.findViewById(R.id.manage_area_item_area);
            manageItemTemp = view.findViewById(R.id.manage_area_item_temp);
            manageItemCardView = view.findViewById(R.id.manage_area_item_cardview);
            manageItemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(view, getBindingAdapterPosition());
                    }
                }
            });
            manageItemCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onItemLongClick(view, getBindingAdapterPosition());
                    }
                    return true;
                }
            });
        }

        public TextView getManageItemArea() {
            return manageItemArea;
        }

        public TextView getManageItemTemp() {
            return manageItemTemp;
        }

        public CardView getManageItemCardView() {
            return manageItemCardView;
        }

    }

    public CityManageAdapter(List<City> mCityList) {
        this.mCityList = mCityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_manage_area_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        City city = mCityList.get(position);
        holder.manageItemArea.setText(city.getCityName());
        holder.manageItemTemp.setText(city.getNowTemp());
//        int currentHour=Integer.parseInt(city.getObsTime().substring(11,12));
//        if (currentHour >= 5 && currentHour < 18) {
//            //此时是白天，设置白天天气壁纸
//            holder.manageItemCardView.setBackgroundColor(R.color.TestColor);
//        } else {
//            //此时是夜晚，设置夜晚天气壁纸
//            holder.manageItemCardView.setBackgroundColor(R.color.JackieBlue);
//        }
    }

    @Override
    public int getItemCount() {
        return mCityList.size();
    }
}

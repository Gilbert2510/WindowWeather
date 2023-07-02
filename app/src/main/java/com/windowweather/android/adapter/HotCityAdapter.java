package com.windowweather.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.windowweather.android.R;
import com.windowweather.android.db.CityHot;

import java.util.List;

public class HotCityAdapter extends RecyclerView.Adapter<HotCityAdapter.ViewHolder> {
    private final List<CityHot> mHotCityList;
    private static OnItemClickListener mClickListener;

    /**
     * 为Activity提供设置OnItemClickListener的接口
     * @param itemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    /**
     * 定义接口,在activity里面使用setOnItemClickListener方法并创建此接口的对象、实现其方法
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView hotCityName;
        private final CardView cardView;

        public ViewHolder(View view) {
            super(view);
            hotCityName = view.findViewById(R.id.activity_search_item_hotcityname);
            cardView = view.findViewById(R.id.activity_search_item_hotcity_cardview);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * 使用getTag方法获取点击的item的position
                     */
                    if(mClickListener != null) {
                        mClickListener.onItemClick(view,(int)v.getTag());
                    }
                }
            });
        }

        public TextView getHotCityName() {
            return hotCityName;
        }

        public CardView getCardView() {
            return cardView;
        }
    }

    public HotCityAdapter(List<CityHot> mHotCityList) {
        this.mHotCityList = mHotCityList;
    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_item_hotcity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CityHot cityHot = mHotCityList.get(position);
        holder.hotCityName.setText(cityHot.getCityName());
        /**
         * 将position保存在itemView的Tag中，以便点击时进行获取
         */
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mHotCityList.size();
    }

}

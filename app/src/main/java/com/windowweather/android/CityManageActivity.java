package com.windowweather.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.windowweather.android.adapter.CityManageAdapter;
import com.windowweather.android.db.City;

import org.litepal.LitePal;

import java.util.List;

public class CityManageActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar manageToolbar;
    private Button manageBack;
    private Button manageInsert;
    private Button manageEdit;
    private RecyclerView manageRecyclerView;
    private TextView manageItemArea;
    private TextView manageItemTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_area);
        //初始化各组件
        manageToolbar = findViewById(R.id.manage_area_toolbar);
        manageBack = findViewById(R.id.manage_area_back);
        manageInsert = findViewById(R.id.manage_area_insert);
        manageEdit = findViewById(R.id.manage_area_edit);
        manageRecyclerView = findViewById(R.id.manage_area_recyclerview);
        manageItemArea=findViewById(R.id.manage_area_item_area);
        manageItemTemp=findViewById(R.id.manage_area_item_temp);

        //控件设置监听器
        manageBack.setOnClickListener(this);
        manageInsert.setOnClickListener(this);
        manageEdit.setOnClickListener(this);
        /**
         * 读取数据库寻找RecyclerView的加载数据
         * 对RecyclerView进行设置
         */
        List<City> cityList = LitePal.findAll(City.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        manageRecyclerView.setLayoutManager(layoutManager);
        CityManageAdapter manageAdapter = new CityManageAdapter(cityList);
        manageRecyclerView.setAdapter(manageAdapter);

        Intent intent = getIntent();
        String cityId = intent.getStringExtra("searchCityId");



        /**
         * 为城市管理项增设点按和长按的监听器
         * 当点按时进入该城市的天气界面
         * 当长按时出现删除菜单
         */
        manageAdapter.setOnItemClickListener(new CityManageAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView cityName = view.findViewById(R.id.manage_area_item_area);
                String name = cityName.getText().toString();
                List<City> cityList = LitePal.select("cityId").where("cityName = ?", name).find(City.class);
                String id = cityList.get(0).getCityId();
                Intent intent = new Intent(view.getContext(), WeatherActivity.class);
                intent.putExtra("cityId", id);
                startActivity(intent);
                finish();
            }
            @Override
            public void onItemLongClick(View view, int position) {
                TextView cityName = view.findViewById(R.id.manage_area_item_area);
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.manage_city, popupMenu.getMenu());
                //弹出式菜单的点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        cityList.remove(position);
                        manageAdapter.notifyItemRemoved(position);
                        LitePal.deleteAll(City.class, "cityName = ?", cityName.getText().toString());
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }

    /**
     * 设置长按菜单
     *
     * @param menu     The context menu that is being built
     * @param v        The view for which the context menu is being built
     * @param menuInfo Extra information about the item for which the
     *                 context menu should be shown. This information will vary
     *                 depending on the class of v.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.manage_city, menu);
    }

    /**
     * 上方三个按钮（返回，增加，编辑）的监听器
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        //返回天气界面
        if (id == R.id.manage_area_back) {
            finish();
        } else if (id == R.id.manage_area_insert) {
            Intent intent = new Intent(CityManageActivity.this, CitySearchActivity.class);
            Log.d("manage", "onClick: ");
            startActivity(intent);
        } else if (id == R.id.manage_area_edit) {
            Toast.makeText(CityManageActivity.this, "正在开发", Toast.LENGTH_SHORT).show();
        }
    }

}

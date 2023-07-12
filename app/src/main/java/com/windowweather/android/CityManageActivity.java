package com.windowweather.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.qweather.sdk.bean.Basic;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;
import com.windowweather.android.adapter.CityManageAdapter;
import com.windowweather.android.db.City;
import com.windowweather.android.util.DateUtils;

import org.litepal.LitePal;

import java.util.List;

public class CityManageActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout manageLinearLayout;
    private Toolbar manageToolbar;
    private TextView manageTextView;
    private Button manageBack;
    private Button manageInsert;
    private Button manageEdit;
    private CheckBox manageCheckBox;
    private RecyclerView manageRecyclerView;
    private CityManageAdapter cityManageAdapter;
    List<City> cityList;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            int resultCode = result.getResultCode();
            Log.d("manage", String.valueOf(resultCode));
            if (resultCode == RESULT_OK) {
                //此时是表明搜索的城市已经存在于数据库中，只需要要提示已存在即可
                Toast.makeText(CityManageActivity.this, "该城市已在列表中", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_FIRST_USER) {
                //更新管理界面
                Intent data = result.getData();
                assert data != null;
                String cityId = data.getStringExtra("searchId");
                String cityName = data.getStringExtra("searchName");
                /**
                 * 查询
                 */
                QWeather.getWeatherNow(CityManageActivity.this, cityId, new QWeather.OnResultWeatherNowListener() {
                    public static final String TAG = "weather_now";

                    @Override
                    public void onError(Throwable throwable) {
                        Log.i(TAG, "on error: ", throwable);
                        //System.out.println("Weather Now Error:"+new Gson());
                        Looper.prepare();
                        Toast.makeText(CityManageActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onSuccess(WeatherNowBean weatherNowBean) {
                        Log.i(TAG, "getWeatherNow onSuccess: " + new Gson().toJson(weatherNowBean));
                        //System.out.println("获取当前天气成功"+new Gson().toJson(weatherNowBean));
                        if (Code.OK == weatherNowBean.getCode()) {
                            WeatherNowBean.NowBaseBean bean = weatherNowBean.getNow();
                            Basic basic = weatherNowBean.getBasic();
                            City city;
                            //否则添加新的城市项
                            city = new City();
                            city.setCityName(cityName);
                            city.setCityId(cityId);
                            city.setFxLink(basic.getFxLink());
                            city.setObsTime(bean.getObsTime());
                            city.setFeelsLike(bean.getFeelsLike());
                            city.setNowTemp(bean.getTemp());
                            city.setNowText(bean.getText());
                            city.setNowIcon(bean.getIcon());
                            city.setNowCloud(bean.getCloud());
                            city.setNowHumidity(bean.getHumidity());
                            city.setNowPressure(bean.getPressure());
                            city.setNowVis(bean.getVis());
                            city.setNowWindDir(bean.getWindDir());
                            city.setNowWindScale(bean.getWindScale());
                            city.save();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    City city = LitePal.findLast(City.class);
                                    cityList.add(city);
                                    cityManageAdapter.notifyItemInserted(cityList.size());
                                }
                            });
                        }
                    }
                });
//                Utility.queryWeatherNow(CityManageActivity.this, cityId, cityName, false);
            }
        }
    });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_area);
        //初始化各组件
        manageLinearLayout = findViewById(R.id.activity_manage_area_linearlayout);
        manageToolbar = findViewById(R.id.manage_area_toolbar);
        manageTextView = findViewById(R.id.manage_area_toolbar_textview);
        manageBack = findViewById(R.id.manage_area_back);
        manageInsert = findViewById(R.id.manage_area_insert);
        manageEdit = findViewById(R.id.manage_area_edit);
        manageCheckBox=findViewById(R.id.manage_area_item_checkbox);
        manageRecyclerView = findViewById(R.id.manage_area_recyclerview);

        //控件设置监听器
        manageBack.setOnClickListener(this);
        manageInsert.setOnClickListener(this);
        manageEdit.setOnClickListener(this);
        /**
         * 读取数据库寻找RecyclerView的加载数据
         * 对RecyclerView进行设置
         */
        cityList = LitePal.findAll(City.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        manageRecyclerView.setLayoutManager(layoutManager);
        cityManageAdapter = new CityManageAdapter(cityList,false);
        manageRecyclerView.setAdapter(cityManageAdapter);

        /**
         * 为城市管理项增设点按和长按的监听器
         * 当点按时进入该城市的天气界面
         * 当长按时出现删除菜单
         */
        cityManageAdapter.setOnItemClickListener(new CityManageAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                City city=cityList.get(position);
                String id = city.getCityId();
                String name = city.getCityName();
                Intent intent = new Intent(view.getContext(), WeatherActivity.class);
                intent.putExtra("cityId", id);
                intent.putExtra("cityName", name);
                setResult(RESULT_OK, intent);
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
                        int id = item.getItemId();
                        if (id == R.id.manage_city_menu_delete) {
                            //移除Adapter的List数据和数据库数据
                            cityList.remove(position);
                            cityManageAdapter.notifyItemRemoved(position);
                            LitePal.deleteAll(City.class, "cityName = ?", cityName.getText().toString());
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onResume() {
        super.onResume();
        int currentHour = DateUtils.getCurrentHour();
        if (currentHour < 7 || currentHour >= 21) {
            //当处于夜晚时切换夜间模式
            manageLinearLayout.setBackgroundResource(R.color.black);
            manageTextView.setTextColor(Color.parseColor("#FFFFFF"));
            manageBack.setBackgroundResource(R.drawable.back_vector);
            manageInsert.setBackgroundResource(R.drawable.city_add_vector);
            manageEdit.setBackgroundResource(R.drawable.city_edit_vector);
        }
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
            launcher.launch(intent);
        } else if (id == R.id.manage_area_edit) {
            Intent intent=new Intent(CityManageActivity.this,CityManageChangeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }


}

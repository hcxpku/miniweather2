package com.example.haochuanxin.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import app.MyApplication;
import bean.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class selectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private ListView mlist;
    private SimpleAdapter myadapter;
    private List<City> cityList;
    private List<Map<String,Object>> data;
    private AutoCompleteTextView auto;
    private ArrayAdapter madapter;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        auto=(AutoCompleteTextView)findViewById(R.id.search_city);
       initViews();
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.title_back:
                Intent i=new Intent();
                i.putExtra("cityCode","101160101");
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;
        }
    }
    private void initViews(){
        mlist=(ListView)findViewById(R.id.title_list);
        MyApplication myApplication=(MyApplication)getApplication();
        cityList=myApplication.getCityList();
        data=new ArrayList<Map<String, Object>>();
        for (City city:cityList){
            Map<String,Object> item=new HashMap<>();
            item.put("city_name",city.getCity());
            item.put("city_code",city.getNumber());
            data.add(item);
        }

        myadapter=new SimpleAdapter(this,data,R.layout.item,new String[] {"city_name","city_code"},new int[] {R.id.city_name,R.id.city_code});
        mlist.setAdapter(myadapter);
        auto.setAdapter(myadapter);
        mlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int position,long id) {
                ListView listView=(ListView)adapterView;
                Map<String,Object> map=(Map<String, Object>)listView.getItemAtPosition(position);
                String str1=(String)map.get("city_code");
                Intent i = new Intent();
                i.putExtra("cityCode",str1);
                setResult(RESULT_OK, i);
                finish();
            }

        });







    }
}

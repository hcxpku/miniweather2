package com.example.haochuanxin.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import bean.TodayWeather;



public class MainActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener{

    private static final int UPDATE_TODAY_WEATHER=1;
    private static final int UPDATE_FUTURE_WEATHER=2;

    private ImageView mUpdateBtn;
    private ImageView mCitySelect;

    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,
            temperatureTv,climateTv,windTv,city_name_Tv,wenduTv;
    private TextView vk1,vk2,vk3,vk4,tm1,tm2,tm3,tm4,cl1,cl2,cl3,cl4,wind1,wind2,wind3,wind4;
    private ImageView weatherImg,pmImg;
    private ProgressBar pro;
    private List<View> views;
    private viewPagerAdapter vpAdapter;
    private ViewPager vp;
    private ImageView[] dots;
    private int[] ids={R.id.iv1,R.id.iv2};
    private List<TodayWeather> futureWeather=null;
    //主线程增加Handler
    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg){
            switch(msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                case UPDATE_FUTURE_WEATHER:
                    updateFutureWeather();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);//加载布局文件
        mUpdateBtn=(ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        pro=(ProgressBar)findViewById(R.id.title_update_progress);
        //测试网络连接
        if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
            Log.d("myWeather","网络OK");
            Toast.makeText(MainActivity.this, "网络OK", Toast.LENGTH_SHORT).show();
        }
        else{
            Log.d("myWeather","网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_SHORT).show();
        }
        mCitySelect=(ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        initView();
        initPage();
        vp.setOnPageChangeListener(this);
        initDots();

    }
    //初始化页面信息
    void initView(){
        city_name_Tv=(TextView)findViewById(R.id.title_city_name);
        cityTv=(TextView)findViewById(R.id.city);
        timeTv=(TextView)findViewById(R.id.time);
        humidityTv=(TextView)findViewById(R.id.humidity);
        weekTv=(TextView)findViewById(R.id.week_today);
        pmDataTv=(TextView)findViewById(R.id.pm_data);
        pmQualityTv=(TextView)findViewById(R.id.pm2_5_quality);
        pmImg=(ImageView)findViewById(R.id.pm2_5_img);
        temperatureTv=(TextView)findViewById(R.id.temperature);
        climateTv=(TextView)findViewById(R.id.climate);
        windTv=(TextView)findViewById(R.id.wind);
        weatherImg=(ImageView)findViewById(R.id.weather_img);
        wenduTv=(TextView)findViewById(R.id.wendu);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        wenduTv.setText("N/A");

    }
    //初始化viewpager
    public void initPage(){
        LayoutInflater inflater=LayoutInflater.from(this);
        views=new ArrayList<View>();
        views.add(inflater.inflate(R.layout.pager1,null));
        views.add(inflater.inflate(R.layout.pager2,null));
        vpAdapter=new viewPagerAdapter(views,this);
        vp=(ViewPager)findViewById(R.id.viewPager1);
        vp.setAdapter(vpAdapter);

        vk1=(TextView)views.get(0).findViewById(R.id.week_1);
        vk2=(TextView)views.get(0).findViewById(R.id.week_2);
        vk3=(TextView)views.get(0).findViewById(R.id.week_3);
        vk4=(TextView)views.get(1).findViewById(R.id.week_4);
        tm1=(TextView)views.get(0).findViewById(R.id.temperature);
        tm2=(TextView)views.get(0).findViewById(R.id.temperature2);
        tm3=(TextView)views.get(0).findViewById(R.id.temperature3);
        tm4=(TextView)views.get(1).findViewById(R.id.temperature);
        cl1=(TextView)views.get(0).findViewById(R.id.climate);
        cl2=(TextView)views.get(0).findViewById(R.id.climate2);
        cl3=(TextView)views.get(0).findViewById(R.id.climate3);
        cl4=(TextView)views.get(1).findViewById(R.id.climate);
        wind1=(TextView)views.get(0).findViewById(R.id.wind);
        wind2=(TextView)views.get(0).findViewById(R.id.wind2);
        wind3=(TextView)views.get(0).findViewById(R.id.wind3);
        wind4=(TextView)views.get(1).findViewById(R.id.wind);

        vk1.setText("n/a");
        vk2.setText("n/a");
        vk3.setText("n/a");
        vk4.setText("n/a");
        tm1.setText("n/a");
        tm2.setText("n/a");
        tm3.setText("n/a");
        tm4.setText("n/a");
        cl1.setText("n/a");
        cl2.setText("n/a");
        cl3.setText("n/a");
        cl4.setText("n/a");
        wind1.setText("n/a");
        wind2.setText("n/a");
        wind3.setText("n/a");
        wind4.setText("n/a");




    }
    public void initDots(){
        dots=new ImageView[views.size()];
        for(int i=0;i<views.size();i++){
            dots[i]=(ImageView)findViewById(ids[i]);
        }

    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.title_city_manager){
            Intent i=new Intent(this,selectCity.class);
            startActivityForResult(i,1);

        }
        if(view.getId()==R.id.title_update_btn){
            pro.setVisibility(ProgressBar.VISIBLE);
            mUpdateBtn.setVisibility(ImageView.INVISIBLE);
            SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
            String cityCode=sharedPreferences.getString("main_city_code","101010100");
            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE) {
                Log.d("myWeather", cityCode);
                queryWeatherCode(cityCode);
            }
            else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_SHORT).show();
            }
        }
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data ){
        if(requestCode==1&&resultCode==RESULT_OK){
            String newCityCode=data.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为"+newCityCode);
            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络OK");
                queryWeatherCode(newCityCode);
            }else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
            }
        }
    }
    //连接网络，获取数据
    private void queryWeatherCode(String cityCode)  {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                TodayWeather todayWeather=null;

                try{
                    URL url=new URL(address);
                    con=(HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in=con.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String str;
                    while((str=reader.readLine())!=null){
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr=response.toString();
                    Log.d("myWeather", responseStr);
                    todayWeather=parseXML(responseStr);
                    InputStream ip=new ByteArrayInputStream(responseStr.getBytes());
                    futureWeather=parseXML2(ip);

                    if(todayWeather!=null){
                        Log.d("myWeather",todayWeather.toString());
                        Message msg=new Message();
                        msg.what=UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        mHandler.sendMessage(msg);
                    }

                    if(futureWeather!=null){
                        Log.d("futureweather",futureWeather.toString());
                        Message msg=new Message();
                        msg.what=UPDATE_FUTURE_WEATHER;
                        msg.obj=futureWeather;
                        mHandler.sendMessage(msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }
    //解析今日天气数据
    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());

                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());

                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }


                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }
    //更新页面信息
    public void updateTodayWeather(TodayWeather todayWeather){
        Log.d("todayWeather","更新成功");
        pro.setVisibility(ProgressBar.INVISIBLE);
        mUpdateBtn.setVisibility(ImageView.VISIBLE);
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度： "+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getLow()+"~"+todayWeather.getHigh());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力： "+todayWeather.getFengli());
        wenduTv.setText("温度： "+todayWeather.getWendu());

        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }
    //解析六日天气数据
    public List<TodayWeather> parseXML2(InputStream ip)  {
        List<TodayWeather> futureWeather=new ArrayList<TodayWeather>();
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document=db.parse(ip);
            Element root=document.getDocumentElement();
            NodeList weatherNodes=root.getElementsByTagName("weather");
            for(int i=1;i<weatherNodes.getLength();i++){
                TodayWeather temp=new TodayWeather();
                Element weatherNode=(Element)weatherNodes.item(i);
                NodeList weatherChilds=weatherNode.getChildNodes();
                for(int j=0;j<weatherChilds.getLength();j++){
                    Node node=weatherChilds.item(j);
                    if(node.getNodeType()==Node.ELEMENT_NODE){
                        Element element=(Element)node;
                        if("date".equals(element.getNodeName())){
                            temp.setDate(element.getFirstChild().getNodeValue());
                        }
                        else if("high".equals(element.getNodeName())){
                            temp.setHigh(element.getFirstChild().getNodeValue());
                        }else if("low".equals(element.getNodeName())){
                            temp.setLow(element.getFirstChild().getNodeValue());
                        }

                    }
                    if(node.getNodeName().equals("day")){
                        NodeList day=node.getChildNodes();
                        for(int m=0;m<day.getLength();m++){
                            Node nodes=day.item(m);
                            Element element=(Element)nodes;
                            if(element.getNodeName().equals("type")){
                                temp.setType(element.getFirstChild().getNodeValue());
                            }
                            else if(element.getNodeName().equals("fengxiang")){
                                temp.setFengxiang(element.getFirstChild().getNodeValue());
                            }
                            else if(element.getNodeName().equals("fengli")){
                                temp.setFengli(element.getFirstChild().getNodeValue());
                            }
                        }
                    }


                }
                futureWeather.add(temp);
            }
            ip.close();


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

       return futureWeather;
    }

    public void updateFutureWeather(){
        Log.d("future","未来几天天气更新成功～");
        vk1.setText(futureWeather.get(0).getDate());
        vk2.setText(futureWeather.get(1).getDate());
        vk3.setText(futureWeather.get(2).getDate());
        vk4.setText(futureWeather.get(3).getDate());
        tm1.setText(futureWeather.get(0).getLow()+"~"+futureWeather.get(0).getHigh());
        tm2.setText(futureWeather.get(1).getLow()+"~"+futureWeather.get(1).getHigh());
        tm3.setText(futureWeather.get(2).getLow()+"~"+futureWeather.get(2).getHigh());
        tm4.setText(futureWeather.get(3).getLow()+"~"+futureWeather.get(3).getHigh());
        wind1.setText(futureWeather.get(0).getFengli());
        wind2.setText(futureWeather.get(1).getFengli());
        wind3.setText(futureWeather.get(2).getFengli());
        wind4.setText(futureWeather.get(3).getFengli());
        cl1.setText(futureWeather.get(0).getType());
        cl2.setText(futureWeather.get(1).getType());
        cl3.setText(futureWeather.get(2).getType());
        cl4.setText(futureWeather.get(3).getType());





    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    //如果页面被选中，设置焦点
    @Override
    public void onPageSelected(int p) {
        for(int a=0;a<ids.length;a++){
            if(a==p){
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            }
            else
            {
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

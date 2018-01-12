package com.zucc.g3.hzy.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity  implements Button.OnClickListener , CompoundButton.OnCheckedChangeListener,NavigationView.OnNavigationItemSelectedListener{

    private String HOST="127.0.0.1:1883";
    private final static String USERNAME="";
    private final static String PWD="";
    private String topic_pub="";
    private String topic_sub="";

    private boolean swc=false;//保护开关是否开启
    private boolean wait=true;//判断按键等待延时


    private final static int CONNECTED=1;
    private final static int LOST=2;
    private final static int FAIL=3;
    private final static int RECEIVE=4;
    private final static int MSGSEND=5;


    private MqttAsyncClient mqttClient;
    private Switch switch_connect;

    private Toolbar toolbar;
    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    public  Fragment fragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = this.getIntent().getExtras();
        topic_pub=bundle.getString("Pubtopic");
        topic_sub=bundle.getString("Subtopic");
        HOST=bundle.getString("IP");

        switch_connect = (Switch) findViewById(R.id.sw_connect);
        switch_connect.setOnCheckedChangeListener(this);


        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        //加载状态fragment
        fragment =new StateFragment();
        replaceFragment(fragment);


        //抬头的点点点
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //设置导航图标要在setSupportActionBar方法之后
        setSupportActionBar(toolbar);

        //配置toolbar左边三条杠布局
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //底部导航监听
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        Toast.makeText(MainActivity.this, "嘿嘿嘿", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==CONNECTED){
                Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
            }else if(msg.what==LOST){
                Toast.makeText(MainActivity.this,"连接丢失，进行重连",Toast.LENGTH_SHORT).show();
            }else if(msg.what==FAIL){

                Toast.makeText(MainActivity.this,"连接失败",Toast.LENGTH_SHORT).show();
            }else if(msg.what==RECEIVE){

            }
            else if(msg.what==MSGSEND){

            }
            super.handleMessage(msg);
        }
    };

    public void setHandler(Handler handler){
        this.handler = handler;
    }


//滑块滑动
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId()== R.id.sw_connect){
            if(isChecked){
                connectBroker();
                swc=true;
                delay_connector();
            }
            else{
                Toast.makeText(MainActivity.this,"断开连接",Toast.LENGTH_SHORT).show();
                try {
                    mqttClient.disconnect();
                    swc=false;
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //异步线程连接订阅mqtt消息
    private void delay_connector() {
        Timer timer=new Timer();
        TimerTask task=new TimerTask(){
            public void run(){
                try {
                    mqttClient.subscribe(topic_sub, 2);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(task, 500);
    }

    //判断是否接收成功
    private IMqttActionListener mqttActionListener=new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            //连接成功处理
            Message msg=new Message();
            msg.what=CONNECTED;
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            exception.printStackTrace();
            //连接失败处理
            Message msg=new Message();
            msg.what=FAIL;
            handler.sendMessage(msg);
        }
    };

    ////创建mqtt监听
    private MqttCallback callback=new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            //连接断开
            Message msg=new Message();
            msg.what=LOST;
            handler.sendMessage(msg);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            //消息到达
//            subMsg.append(new String(message.getPayload())+"\n"); //不能直接修改,需要在UI线程中操作
            Message msg=new Message();
            msg.what=RECEIVE;
            msg.obj=new String(message.getPayload())+"\n";
            handler.sendMessage(msg);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            //消息发送完成
        }
    };

    //mqtt连接
    private void connectBroker(){
        try {
            mqttClient=new MqttAsyncClient("tcp://"+HOST,"ClientID"+Math.random(),new MemoryPersistence());
            mqttClient.connect(getOptions(),null,mqttActionListener);
            mqttClient.setCallback(callback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //配置mqtt长连接
    private MqttConnectOptions getOptions(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);//重连不保持状态
        //如果需要设置mqtt数据用户名和密码
        if(USERNAME!=null&&USERNAME.length()>0&&PWD!=null&&PWD.length()>0){
            options.setUserName(USERNAME);//设置服务器账号密码
            options.setPassword(PWD.toCharArray());
        }
        options.setConnectionTimeout(10);//设置连接超时时间
        options.setKeepAliveInterval(30);//设置保持活动时间，超过时间没有消息收发将会触发ping消息确认
        return options;
    }

    private String buildJSON(int SHH,int ST,int SH,int opLT,int LtDl){
        JSONObject obj = new JSONObject();
        try {
            obj.put("SHH", SHH);
            obj.put("ST", ST);
            obj.put("SH", SH);
            obj.put("opLt", opLT);
            obj.put("LtDl", LtDl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    //mqtt延时，防止用户暴力按压
    private void delay(int time) {
        Timer timer=new Timer();
        TimerTask task=new TimerTask(){
            public void run(){
                wait=true;
            }
        };
        timer.schedule(task, time);//设置两次发送时间间隔
    }

    @Override
    //抬头的点点点
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //底部导航监听
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.history:
                    toolbar.setTitle("历史");
                    Toast.makeText(MainActivity.this,"历史",Toast.LENGTH_SHORT).show();
                    fragment =new HistoryFragment();
                    replaceFragment(fragment);

                    return true;
                case R.id.state:
                    toolbar.setTitle("状态");
                    Toast.makeText(MainActivity.this,"状态",Toast.LENGTH_SHORT).show();
                    fragment =new StateFragment();
                    replaceFragment(fragment);
                    return true;
            }
            return false;
        }

    };

    //侧滑菜单点击事件
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_set) {
            Toast.makeText(MainActivity.this, "备份中", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_backups) {
            Toast.makeText(MainActivity.this, "备份中....", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_quit) {

            DataSupport.deleteAll(QRcode.class);
            Intent intent = new Intent(MainActivity.this, ScanQRActivity.class);
            startActivity(intent);
            this.finish();

        } else if (id == R.id.nav_about) {
            Toast.makeText(MainActivity.this, "关于我们", Toast.LENGTH_SHORT).show();
        } else {
            //Do nothing
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

//        if(v==pubButton)
//        {
//            if (wait&&swc)
//            {
//                try {
//                    mqttClient.publish(topic_pub, buildJSON(0, 30, 80, 8, 13).getBytes(), 1, false);
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                } finally {
//                    wait = false;
//                    delay(800);
//                }
//            }
//            else  if(!swc)
//            {
//                Toast.makeText(MainActivity.this,"请先连接再发送",Toast.LENGTH_SHORT).show();
//            }
//            else if(!wait)
//            {
//                Toast.makeText(MainActivity.this,"亲，命令已经到达，不要频繁发送",Toast.LENGTH_SHORT).show();
//            }
//        }
//        else if(v==clearButton){
//            subMsg.setText("");
//        }
    }

    public void onClick_Event(View view) {
        switch (view.getId()) {
            case R.id.H:
                if (wait&&swc)
                {


                    try {
                        mqttClient.publish(topic_pub, buildJSON(0, 30, 80, 8, 13).getBytes(), 1, false);

                    } catch (MqttException e) {
                        e.printStackTrace();
                    } finally {
                        wait = false;
                        delay(800);
                    }
                }
                else  if(!swc)
                {
                    Toast.makeText(MainActivity.this,"请先连接再发送",Toast.LENGTH_SHORT).show();
                }
                else if(!wait)
                {
                    Toast.makeText(MainActivity.this,"亲，命令已经到达，不要频繁发送",Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(view.getContext(), "H", Toast.LENGTH_SHORT).show();
                break;
            case R.id.S:
                if (wait&&swc)
                {
                    try {
                        mqttClient.publish(topic_pub, buildJSON(0, 30, 80, 8, 13).getBytes(), 1, false);

                    } catch (MqttException e) {
                        e.printStackTrace();
                    } finally {
                        wait = false;
                        delay(800);
                    }
                }
                else  if(!swc)
                {
                    Toast.makeText(MainActivity.this,"请先连接再发送",Toast.LENGTH_SHORT).show();
                }
                else if(!wait)
                {
                    Toast.makeText(MainActivity.this,"亲，命令已经到达，不要频繁发送",Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(view.getContext(), "S", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    //去除Fragment
    private void removeFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction =fragmentManager.beginTransaction();
        transaction.hide(fragment);
        transaction.remove(fragment);
        transaction.commit();
    }

    //加载Fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction =fragmentManager.beginTransaction();
        transaction.replace(R.id.place_holder,fragment);
        transaction.commit();
    }


}


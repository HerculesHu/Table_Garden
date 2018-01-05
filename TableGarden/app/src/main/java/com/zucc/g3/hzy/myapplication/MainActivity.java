package com.zucc.g3.hzy.myapplication;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity  implements Button.OnClickListener , CompoundButton.OnCheckedChangeListener{

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

    private TextView subMsg;
    private Button pubButton,clearButton;
    private MqttAsyncClient mqttClient;
    private Switch switch_connect;

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
        subMsg=(TextView)findViewById(R.id.submessage);
        pubButton=(Button)findViewById(R.id.pubButton);

        clearButton=(Button)findViewById(R.id.clearButton);
        pubButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

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
                subMsg.append((String)msg.obj);
            }
            super.handleMessage(msg);
        }
    };


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


    //mqtt事务处理中心
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
    public void onClick(View v) {

        if(v==pubButton)
        {
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

        }

        else if(v==clearButton){
            subMsg.setText("");
        }
    }
}


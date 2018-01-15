package com.zucc.g3.hzy.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zucc.g3.hzy.myapplication.Unit.Signal;
import com.zucc.g3.hzy.myapplication.Unit.SignalBack;
import com.zucc.g3.hzy.myapplication.view.TimeView;

import org.json.JSONException;

/**
 * Created by Administrator on 2018/1/10.
 */

public class HistoryFragment extends Fragment {

    private final static int CONNECTED=1;
    private final static int LOST=2;
    private final static int FAIL=3;
    private final static int RECEIVE=4;
    private final static int MSGSEND=5;

    private TextView M;
    private MainActivity main_activity;
    private TimeView temView,humView,soilHumView;//自定义组件


    private Signal signal;
    private int[] lock = new int[5];

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==CONNECTED){
                Toast.makeText(getActivity(),"连接成功",Toast.LENGTH_LONG).show();
            }else if(msg.what==LOST){
                Toast.makeText(getActivity(),"连接丢失，进行重连",Toast.LENGTH_SHORT).show();
            }else if(msg.what==FAIL){
                Toast.makeText(getActivity(),"连接失败",Toast.LENGTH_SHORT).show();
            }else if(msg.what==RECEIVE){
                M.setText((String)msg.obj);
                try {
                    signal.ParseJson((String)msg.obj);//对消息的处理
                    msgToView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(msg.what==MSGSEND){
                //当设置已经被发送
                temView.senting();
                humView.senting();
                soilHumView.senting();
                for (int i=0;i<5;i++){
                    lock[i]=(Integer.MAX_VALUE);
                }
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        signal=new Signal();
        for (int i=0;i<5;i++){
            lock[i]=(Integer.MAX_VALUE);
        }
        M=(TextView)view.findViewById(R.id.HT);

        temView = (TimeView)view. findViewById(R.id.temperature_picker);
        humView=(TimeView)view. findViewById(R.id.humidity_picker);
        soilHumView=(TimeView)view. findViewById(R.id.soilhumidity_picker);


        temView.setTXT("实时温度");
        temView.setTxtColor("#ff00ff");
        temView.setLowerBound(-10);
        temView.setUpperBound(40);
        temView.setTextSize(18);
        temView.setInnerColor("#454500");
        temView.setValueSetColor("#112300");

        humView.setTXT("实时湿度");
        humView.setTxtColor("#ff00ff");
        humView.setLowerBound(0);
        humView.setUpperBound(101);
        humView.setTextSize(16);
        humView.setInnerColor("#454500");
        humView.setValueSetColor("#112300");

        soilHumView.setTXT("土壤湿度");
        soilHumView.setTxtColor("#ff00ff");
        soilHumView.setLowerBound(0);
        soilHumView.setUpperBound(30);
        soilHumView.setTextSize(14);
        soilHumView.setInnerColor("#454500");
        soilHumView.setValueSetColor("#112300");


        temView.setRockerChangeListener(new TimeView.RockerChangeListener() {
            @Override
            public void report(float value) {
                lock[2]=(int)value;
            }
        });


        humView.setRockerChangeListener(new TimeView.RockerChangeListener() {
            @Override
            public void report(float value) {
                lock[3]=(int)value;
            }
        });

        soilHumView.setRockerChangeListener(new TimeView.RockerChangeListener() {
            @Override
            public void report(float value) {
                lock[4]=(int)value;
            }
        });

        return view;
    }



    //与MainActivity绑定
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        main_activity = (MainActivity) context;
        main_activity.setHandler(handler);
    }

    private void msgToView(){
        String tem="实时温度"+signal.temperature+"℃";
        temView.setOpenTime((int)signal.temperature);
        temView.setTXT(tem);

        String hum="实时湿度"+signal.humidity+"%";
        humView.setOpenTime((int)signal.humidity);
        humView.setTXT(hum);

        String soil_hum="土壤湿度"+signal.soilHumidity+"%";
        soilHumView.setOpenTime((int)signal.soilHumidity);
        soilHumView.setTXT(soil_hum);

        SignalBack ss=  SignalBack.newInstance();

        if(lock[2]!=(Integer.MAX_VALUE)){
            ss.setST(lock[2]);
        }
        else{
            ss.setST(signal.setTemperature);
        }

        if(lock[3]!=(Integer.MAX_VALUE)){
            ss.setSH(lock[3]);
        }
        else{
            ss.setSH(signal.setHumidity);
        }

        if(lock[4]!=(Integer.MAX_VALUE)){
            ss.setSHH(lock[4]);
        }
        else{
            ss.setSHH(signal.setSoilHumidity);
        }
        ss.setOPLT(signal.openLightTime);
        ss.setLtDl(signal.lightDelay);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("HistoryFragment", "onDestroyView");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("HistoryFragment", "onDestroy");
    }
}

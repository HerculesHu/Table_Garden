package com.zucc.g3.hzy.myapplication;

import android.app.Activity;
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
import org.json.JSONObject;


public class StateFragment extends Fragment {

    private final static int CONNECTED=1;
    private final static int LOST=2;
    private final static int FAIL=3;
    private final static int RECEIVE=4;
    private final static int MSGSEND=5;

    private TimeView timeView,timeDelayView;//自定义组件
    private MainActivity main_activity;
    private Signal signal;
    private int[] lock = new int[5];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_state, container, false);

        signal=new Signal();
        for (int i=0;i<5;i++){
            lock[i]=(Integer.MAX_VALUE);
        }


        timeView = (TimeView)view. findViewById(R.id.time_picker);
        timeDelayView=(TimeView)view. findViewById(R.id.time_delay_picker);

        timeDelayView.setTXT("光照时长");
        timeDelayView.setTxtColor("#669999");
        timeDelayView.setLowerBound(0);
        timeDelayView.setUpperBound(25);
        timeDelayView.setTextSize(18);
        timeDelayView.setInnerColor("#CCFFCC");
        timeDelayView.setValueSetColor("#996699");


        timeView.setTXT("开灯时间");
        timeView.setTxtColor("#99CC33");
        timeView.setLowerBound(0);
        timeView.setUpperBound(24);
        timeView.setTextSize(14);
        timeView.setInnerColor("#3399CC");
        timeView.setValueSetColor("#ffcccc");

        timeView.setRockerChangeListener(new TimeView.RockerChangeListener() {
            @Override
            public void report(float value) {
                lock[0]=(int)value;
            }
        });

        timeDelayView.setRockerChangeListener(new TimeView.RockerChangeListener() {
            @Override
            public void report(float value) {
                lock[1]=(int)value;
            }
        });

        return view;
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==CONNECTED){
                Toast.makeText(getActivity(),"连接成功",Toast.LENGTH_LONG).show();
            }else if(msg.what==LOST){
                Toast.makeText(getActivity(),"连接丢失，进行重连",Toast.LENGTH_SHORT).show();
            }else if(msg.what==FAIL){
                Toast.makeText(getActivity(),"连接失败",Toast.LENGTH_SHORT).show();
            }else if(msg.what==RECEIVE){   //有消息到来时
                try {
                    signal.ParseJson((String)msg.obj);//对消息的处理
                    msgToView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else if(msg.what==MSGSEND){
                //当设置已经被发送
                Toast.makeText(getActivity(),"发送成功！",Toast.LENGTH_SHORT).show();
                timeView.senting();
                timeDelayView.senting();
                for (int i=0;i<5;i++){
                    lock[i]=(Integer.MAX_VALUE);
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        main_activity = (MainActivity) context;
        main_activity.setHandler(handler);
    }


    private void msgToView(){
        String opLight="开灯时间"+signal.openLightTime+"点";
        timeView.setOpenTime(signal.openLightTime);
        timeView.setTXT(opLight);

        String ligthDelay="光照时长"+signal.lightDelay+"h";
        timeDelayView.setOpenTime(signal.lightDelay);
        timeDelayView.setTXT(ligthDelay);

        SignalBack ss=  SignalBack.newInstance();

        if(lock[0]!=(Integer.MAX_VALUE)){
            ss.setOPLT(lock[0]);
        }
        else{
            ss.setOPLT(signal.openLightTime);
        }

        if(lock[1]!=(Integer.MAX_VALUE)){
            ss.setLtDl(lock[1]);
        }
        else{
            ss.setLtDl(signal.lightDelay);
        }
        ss.setSH(signal.setHumidity);
        ss.setSHH(signal.setSoilHumidity);
        ss.setST(signal.setTemperature);
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
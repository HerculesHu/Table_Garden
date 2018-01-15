package com.zucc.g3.hzy.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zucc.g3.hzy.myapplication.Unit.Signal;
import com.zucc.g3.hzy.myapplication.view.TimeView;

import org.json.JSONException;
import org.json.JSONObject;


public class StateFragment extends Fragment {

    private final static int CONNECTED=1;
    private final static int LOST=2;
    private final static int FAIL=3;
    private final static int RECEIVE=4;
    private final static int MSGSEND=5;

    private TimeView timeView;//时间选择器
    private TextView M,K;
    private MainActivity main_activity;
    private Signal signal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_state, container, false);

        signal=new Signal();

         M=(TextView)view.findViewById(R.id.ST);
        K=(TextView)view.findViewById(R.id.test2);

        timeView = (TimeView)view. findViewById(R.id.time_picker);
        timeView.setTXT("开灯时间");
        timeView.setTxtColor("#ff00ff");
        timeView.setLowerBound(0);
        timeView.setUpperBound(24);
        timeView.setTextSize(14);
        timeView.setInnerColor("#454500");
        timeView.setValueSetColor("#112300");

        timeView.setRockerChangeListener(new TimeView.RockerChangeListener() {
            @Override
            public void report(float value) {

            K.setText("value: "+(int)value);
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
                timeView.senting();
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
        int v=signal.openLightTime;
        String opLight="开灯时间"+v+"点";
        timeView.setOpenTime(signal.openLightTime);
        timeView.setTXT(opLight);

    }



}
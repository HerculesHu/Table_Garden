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

public class StateFragment extends Fragment {



   private TextView M;
    private MainActivity main_activity;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                Toast.makeText(getActivity(),"连接成功",Toast.LENGTH_LONG).show();
            }else if(msg.what==2){
                Toast.makeText(getActivity(),"连接丢失，进行重连",Toast.LENGTH_SHORT).show();
            }else if(msg.what==3){
                Toast.makeText(getActivity(),"连接失败",Toast.LENGTH_SHORT).show();
            }else if(msg.what==4){
                M.setText((String)msg.obj);
            }
            super.handleMessage(msg);

        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_state, container, false);
         M=(TextView)view.findViewById(R.id.ST);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        main_activity = (MainActivity) context;
        main_activity.setHandler(handler);
    }





}
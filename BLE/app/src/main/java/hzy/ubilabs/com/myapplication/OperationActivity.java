package hzy.ubilabs.com.myapplication;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import hzy.ubilabs.com.myapplication.BLE.fastble.BluetoothService;
import hzy.ubilabs.com.myapplication.BLE.fastble.conn.BleCharacterCallback;
import hzy.ubilabs.com.myapplication.BLE.fastble.exception.BleException;
import hzy.ubilabs.com.myapplication.BLE.fastble.utils.HexUtil;

public class OperationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BluetoothService mBluetoothService;
    private BluetoothGatt gatt;
    private BluetoothGattService service;
    private String back="";
    private TextView backtxt;
    private Button pubbt;
    private EditText eddt;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                getService();
                BLE_start_listener();
            }else if(msg.what==2){
                BLE_start_writer();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backtxt=(TextView)findViewById(R.id.txt_read);
        eddt=(EditText)findViewById(R.id.edt);
        pubbt=(Button)findViewById(R.id.write_bt);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bindService();
    }

    public void writer(String hex){
        final BluetoothGattCharacteristic characteristic = mBluetoothService.getCharacteristic();
        mBluetoothService.write(
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                hex,
                new BleCharacterCallback() {

                    @Override
                    public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                        //成功写入操作
                    }

                    @Override
                    public void onFailure(final BleException exception) {
                        StartBLEWriterAfter(150);
                    }

                    @Override
                    public void onInitiatedResult(boolean result) {

                    }

                });
    }

    private void StartBLEListenerAfter(int time ) {
        Timer timer=new Timer();
        TimerTask task=new TimerTask(){
            public void run(){
                Message msg=new Message();
                msg.what=1;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(task, time);
    }

    private void StartBLEWriterAfter(int time ) {
        Timer timer=new Timer();
        TimerTask task=new TimerTask(){
            public void run(){
                Message msg=new Message();
                msg.what=2;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(task, time);
    }

    public void getService(){
        gatt = mBluetoothService.getGatt();
        mBluetoothService.setService(gatt.getServices().get(gatt.getServices().size()-1));
    }

    private void BLE_start_writer(){
        service = mBluetoothService.getService();
        mBluetoothService.setCharacteristic((service.getCharacteristics().get(service.getCharacteristics().size()-2)));
        mBluetoothService.setCharaProp(1);
        showData();
    }

    private void BLE_start_listener(){
        service = mBluetoothService.getService();
        mBluetoothService.setCharacteristic((service.getCharacteristics().get(service.getCharacteristics().size()-1)));
        mBluetoothService.setCharaProp(2);
        final BluetoothGattCharacteristic characteristic = mBluetoothService.getCharacteristic();
        mBluetoothService.notify(
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                new BleCharacterCallback() {

                    @Override
                    public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                        OperationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                backtxt.setText((String.valueOf(HexUtil.encodeHex(characteristic.getValue()))));
                                back=(String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                            }
                        });
                    }
                    @Override
                    public void onFailure(final BleException exception) {
                        OperationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                backtxt.setText((exception.toString()));
                                StartBLEListenerAfter(100);
                            }
                        });
                    }
                    @Override
                    public void onInitiatedResult(boolean result) {

                    }
                });
    }

    private void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
        StartBLEListenerAfter(100);
        StartBLEWriterAfter(150);
    }

    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            mBluetoothService.setConnectCallback(callback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

    private BluetoothService.Callback2 callback = new BluetoothService.Callback2() {
        @Override
        public void onDisConnected() {
            finish();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showData() {
                pubbt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String hex = eddt.getText().toString();
                        if (TextUtils.isEmpty(hex)) {
                            return;
                        }
                        writer(hex);
                    }
                });
            }

    private void unbindService() {
        this.unbindService(mFhrSCon);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null)
            mBluetoothService.closeConnect();
        unbindService();
    }
}

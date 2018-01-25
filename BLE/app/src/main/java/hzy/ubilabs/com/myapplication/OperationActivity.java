package hzy.ubilabs.com.myapplication;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hzy.ubilabs.com.myapplication.BLE.fastble.BluetoothService;
import hzy.ubilabs.com.myapplication.BLE.fastble.conn.BleCharacterCallback;
import hzy.ubilabs.com.myapplication.BLE.fastble.exception.BleException;
import hzy.ubilabs.com.myapplication.BLE.fastble.utils.HexUtil;

public class OperationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BluetoothService mBluetoothService;
    private String back="";


    private Button testw,tests;
    private TextView backtxt;
    private Button pubbt;
    private Button subbt;
    private EditText eddt;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backtxt=(TextView)findViewById(R.id.txt_read);
        eddt=(EditText)findViewById(R.id.edt);
        subbt=(Button)findViewById(R.id.okbt);
        testw=(Button)findViewById(R.id.conn);
        tests=(Button)findViewById(R.id.conn2);
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


        testw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getService();
                Toast.makeText(OperationActivity.this,"bt",Toast.LENGTH_SHORT).show();
                BluetoothGattService service = mBluetoothService.getService();
                mBluetoothService.setCharacteristic((service.getCharacteristics().get(service.getCharacteristics().size()-1)));
                mBluetoothService.setCharaProp(2);
                showData();
            }
        });

        tests.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getService();
                Toast.makeText(OperationActivity.this,"bt2",Toast.LENGTH_SHORT).show();
                BluetoothGattService service = mBluetoothService.getService();
                mBluetoothService.setCharacteristic((service.getCharacteristics().get(service.getCharacteristics().size()-2)));
                mBluetoothService.setCharaProp(1);
                showData();
            }
        });
    }

    private void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    public void getService(){
        BluetoothGatt gatt = mBluetoothService.getGatt();
        mBluetoothService.setService(gatt.getServices().get(gatt.getServices().size()-1));
    }

    private void unbindService() {
        this.unbindService(mFhrSCon);
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
        final BluetoothGattCharacteristic characteristic = mBluetoothService.getCharacteristic();
                pubbt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String hex = eddt.getText().toString();
                        if (TextUtils.isEmpty(hex)) {
                            return;
                        }
                        mBluetoothService.write(
                                characteristic.getService().getUuid().toString(),
                                characteristic.getUuid().toString(),
                                hex,
                                new BleCharacterCallback() {

                                    @Override
                                    public void onSuccess(final BluetoothGattCharacteristic characteristic) {
//                                            getActivity().runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    txt.append(String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
//                                                    txt.append("\n");
//                                                    int offset = txt.getLineCount() * txt.getLineHeight();
//                                                    if (offset > txt.getHeight()) {
//                                                        txt.scrollTo(0, offset - txt.getHeight());
//                                                    }
//                                                }
//                                            });
                                    }

                                    @Override
                                    public void onFailure(final BleException exception) {
//                                            getActivity().runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    txt.append(exception.toString());
//                                                    txt.append("\n");
//                                                    int offset = txt.getLineCount() * txt.getLineHeight();
//                                                    if (offset > txt.getHeight()) {
//                                                        txt.scrollTo(0, offset - txt.getHeight());
//                                                    }
//                                                }
//                                            });
                                    }

                                    @Override
                                    public void onInitiatedResult(boolean result) {

                                    }

                                });
                    }
                });

                subbt.setText("打开通知1");
                subbt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (subbt.getText().toString().equals("打开通知1")) {
                            subbt.setText("关闭通知1");
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
                                                }
                                            });
                                        }
                                        @Override
                                        public void onFailure(final BleException exception) {
                                            OperationActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    backtxt.setText((exception.toString()));
                                                }
                                            });
                                        }
                                        @Override
                                        public void onInitiatedResult(boolean result) {

                                        }
                                    });
                        } else {
                            subbt.setText("打开通知1");
                            mBluetoothService.stopNotify(
                                    characteristic.getService().getUuid().toString(),
                                    characteristic.getUuid().toString());
                        }
                    }
                });
            }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null)
            mBluetoothService.closeConnect();
        unbindService();
    }
}

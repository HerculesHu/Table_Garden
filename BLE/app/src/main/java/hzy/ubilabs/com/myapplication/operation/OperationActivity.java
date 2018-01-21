package hzy.ubilabs.com.myapplication.operation;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hzy.ubilabs.com.myapplication.R;
import hzy.ubilabs.com.myapplication.BluetoothService;
import hzy.ubilabs.com.myapplication.fastble.conn.BleCharacterCallback;
import hzy.ubilabs.com.myapplication.fastble.exception.BleException;
import hzy.ubilabs.com.myapplication.fastble.utils.HexUtil;

import java.util.ArrayList;
import java.util.List;

public class OperationActivity extends AppCompatActivity {



    private Toolbar toolbar;
    private List<Fragment> fragments = new ArrayList<>();
    private int currentPage = 0;
    private String[] titles = new String[]{"服务列表", "特征列表", "操作控制台"};

    private BluetoothService mBluetoothService;
    private String back="";

    private Button sebt;
    private TextView backtxt;
    private Button pubbt;
    private Button subbt;
    private EditText eddt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_operation);
        initView();
        bindService();
    }



    private void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        this.unbindService(mFhrSCon);
    }

    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            mBluetoothService.setConnectCallback(callback);
            initPage();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

    private void initPage() {
        prepareFragment();
        changePage(0);
    }

    private BluetoothService.Callback2 callback = new BluetoothService.Callback2() {

        @Override
        public void onDisConnected() {
            finish();
        }
    };





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentPage != 0) {
                currentPage--;
                changePage(currentPage);
                return true;
            } else {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backtxt=(TextView)findViewById(R.id.txt_read);
        eddt=(EditText)findViewById(R.id.edt);
        subbt=(Button)findViewById(R.id.okbt);
        pubbt=(Button)findViewById(R.id.write_bt);
        sebt=(Button)findViewById(R.id.sebt);
        sebt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BluetoothGatt gatt = mBluetoothService.getGatt();
                mBluetoothService.setService(gatt.getServices().get(gatt.getServices().size()-1));
               changePage(1);
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage != 0) {
                    currentPage--;
                    changePage(currentPage);
                } else {
                    finish();
                }
            }
        });

    }



    public void changePage(int page) {
        currentPage = page;
        toolbar.setTitle(titles[page]);
        updateFragment(page);
        if (currentPage == 1) {
            ((CharacteristicListFragment) fragments.get(1)).showData();
        } else if (currentPage == 2) {
            ((CharacteristicOperationFragment) fragments.get(2)).showData();
            showData();
        }
    }

    private void prepareFragment() {
        fragments.add(new ServiceListFragment());
        fragments.add(new CharacteristicListFragment());
        fragments.add(new CharacteristicOperationFragment());
        for (Fragment fragment : fragments) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).hide(fragment).commit();
        }
    }

    private void updateFragment(int position) {
        if (position > fragments.size() - 1) {
            return;
        }
        for (int i = 0; i < fragments.size(); i++) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = fragments.get(i);
            if (i == position) {
                transaction.show(fragment);
            } else {
                transaction.hide(fragment);
            }
            transaction.commit();
        }
    }

    public BluetoothService getBluetoothService() {
        return mBluetoothService;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null)
            mBluetoothService.closeConnect();
        unbindService();
    }


    public void showData() {
        final BluetoothGattCharacteristic characteristic = mBluetoothService.getCharacteristic();
        final int charaProp = mBluetoothService.getCharaProp();

        switch (charaProp) {
            case 1: {
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
            }
            break;

            case 2: {
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
            break;
        }

    }

}

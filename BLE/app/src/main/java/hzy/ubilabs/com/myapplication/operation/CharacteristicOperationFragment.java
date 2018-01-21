package hzy.ubilabs.com.myapplication.operation;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import hzy.ubilabs.com.myapplication.R;
import hzy.ubilabs.com.myapplication.BluetoothService;
import hzy.ubilabs.com.myapplication.fastble.conn.BleCharacterCallback;
import hzy.ubilabs.com.myapplication.fastble.exception.BleException;
import hzy.ubilabs.com.myapplication.fastble.utils.HexUtil;

import java.util.ArrayList;
import java.util.List;


public class CharacteristicOperationFragment extends Fragment {


    private BluetoothService mBluetoothService;
    private TextView backtxt;
    private Button pubbt;
    private Button subbt;
    private EditText eddt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getActivity(),"CharacteristicOperationFragment",Toast.LENGTH_SHORT).show();
        mBluetoothService = ((OperationActivity) getActivity()).getBluetoothService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_characteric_operation, null);

        backtxt=(TextView)v.findViewById(R.id.read_read);
        pubbt=(Button) v.findViewById(R.id.write_write);
        eddt=(EditText)v.findViewById(R.id.eddttxt);
        subbt=(Button) v.findViewById(R.id.read_bt_read);
        return v;
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
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        backtxt.setText((String.valueOf(HexUtil.encodeHex(characteristic.getValue()))));
                                                    }
                                                });
                                            }
                                            @Override
                                            public void onFailure(final BleException exception) {
                                                getActivity().runOnUiThread(new Runnable() {
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


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


        return v;
    }



    }


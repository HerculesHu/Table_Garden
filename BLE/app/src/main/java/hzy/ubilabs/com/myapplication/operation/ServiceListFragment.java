package hzy.ubilabs.com.myapplication.operation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import hzy.ubilabs.com.myapplication.R;
import hzy.ubilabs.com.myapplication.BluetoothService;

import java.util.ArrayList;
import java.util.List;


public class ServiceListFragment extends Fragment {

    private Button sebt;
    private BluetoothService mBluetoothService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getActivity(),"ServiceListFragment",Toast.LENGTH_SHORT).show();
        mBluetoothService = ((OperationActivity) getActivity()).getBluetoothService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_list, null);
        sebt=(Button)v.findViewById(R.id.sebt);
        sebt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(),"bt",Toast.LENGTH_SHORT).show();
                BluetoothGatt gatt = mBluetoothService.getGatt();
                mBluetoothService.setService(gatt.getServices().get(gatt.getServices().size()-1));
                ((OperationActivity) getActivity()).changePage(1);
            }
        });

        return v;
    }






}

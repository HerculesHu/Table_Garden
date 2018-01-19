package hzy.ubilabs.com.myapplication.fastble.exception.hanlder;

import hzy.ubilabs.com.myapplication.fastble.exception.BlueToothNotEnableException;
import hzy.ubilabs.com.myapplication.fastble.exception.ConnectException;
import hzy.ubilabs.com.myapplication.fastble.exception.GattException;
import hzy.ubilabs.com.myapplication.fastble.exception.NotFoundDeviceException;
import hzy.ubilabs.com.myapplication.fastble.exception.OtherException;
import hzy.ubilabs.com.myapplication.fastble.exception.ScanFailedException;
import hzy.ubilabs.com.myapplication.fastble.exception.TimeoutException;
import hzy.ubilabs.com.myapplication.fastble.utils.BleLog;

public class DefaultBleExceptionHandler extends BleExceptionHandler {

    private static final String TAG = "BleExceptionHandler";

    public DefaultBleExceptionHandler() {

    }

    @Override
    protected void onConnectException(ConnectException e) {
        BleLog.e(TAG, e.getDescription());
    }

    @Override
    protected void onGattException(GattException e) {
        BleLog.e(TAG, e.getDescription());
    }

    @Override
    protected void onTimeoutException(TimeoutException e) {
        BleLog.e(TAG, e.getDescription());
    }

    @Override
    protected void onNotFoundDeviceException(NotFoundDeviceException e) {
        BleLog.e(TAG, e.getDescription());
    }

    @Override
    protected void onBlueToothNotEnableException(BlueToothNotEnableException e) {
        BleLog.e(TAG, e.getDescription());
    }

    @Override
    protected void onScanFailedException(ScanFailedException e) {
        BleLog.e(TAG, e.getDescription());
    }

    @Override
    protected void onOtherException(OtherException e) {
        BleLog.e(TAG, e.getDescription());
    }
}

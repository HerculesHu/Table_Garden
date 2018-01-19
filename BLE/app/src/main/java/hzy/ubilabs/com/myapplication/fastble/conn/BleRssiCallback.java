package hzy.ubilabs.com.myapplication.fastble.conn;


public abstract class BleRssiCallback extends BleCallback {
    public abstract void onSuccess(int rssi);
}
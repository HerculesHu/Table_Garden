package com.zucc.g3.hzy.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.litepal.crud.DataSupport;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2018/1/1.
 */

public class ScanQRActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;
    private Vibrator vibrator;
    public Button button1 = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        ZXingLibrary.initDisplayOpinion(this);
        /**
         * 初始化组件
         */
        initView();
        chack();

    }

    //是否曾经扫过码
    private void chack(){
        List<QRcode> code= DataSupport.findAll(QRcode.class);
        if(code.isEmpty())
        {

        }
        else
        {
            QRcode id=  code.get(0);
           // Toast.makeText(this, "" + id.getSubtopic()+id.getPubtopic()+id.getIP(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ScanQRActivity.this, MainActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("IP", id.getIP()+"");
            bundle.putString("Pubtopic", id.getPubtopic()+"");
            bundle.putString("Subtopic", id.getSubtopic()+"");
            intent.putExtras(bundle);
            startActivity(intent);
            this.finish();
        }
    }
    private void initView() {
        button1 = (Button) findViewById(R.id.button1);
        /**
         * 打开默认二维码扫描界面
         */
        button1.setOnClickListener(new ButtonOnClickListener(button1.getId()));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Gson gson = new Gson();
//                    {"IP":"123.206.127.199","pubtopic":"hgzxyl1428577","subtopic":"farmbox"}
                    QRcode qrcode=new QRcode();
                    try {
                     qrcode=gson.fromJson(result, QRcode.class);
                     DataSupport.deleteAll(QRcode.class);
                     qrcode.save();
                    } catch (JsonParseException e) {
                        Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    }

                   // Toast.makeText(this, "解析结果:" + qrcode.getIP()+qrcode.getPubtopic()+qrcode.getSubtopic(), Toast.LENGTH_LONG).show();
                    vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    long [] pattern = {100,0,0,0};   // 停止 开启 停止 开启
                    vibrator.vibrate(pattern,-1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
                    chack();



                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(ScanQRActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }



        else if (requestCode == REQUEST_CAMERA_PERM) {
            Toast.makeText(this, "从设置页面返回...", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    /**
     * 请求CAMERA权限码
     */
    public static final int REQUEST_CAMERA_PERM = 101;
    /**
     * EsayPermissions接管权限处理逻辑
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @AfterPermissionGranted(REQUEST_CAMERA_PERM)
    public void cameraTask(int viewId) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
            onClick(viewId);
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "需要请求camera权限",
                    REQUEST_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Toast.makeText(this, "执行onPermissionsGranted()...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, "执行onPermissionsDenied()...", Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, "当前App需要申请camera权限,需要打开设置页面么?")
                    .setTitle("权限申请")
                    .setPositiveButton("确认")
                    .setNegativeButton("取消", null /* click listener */)
                    .setRequestCode(REQUEST_CAMERA_PERM)
                    .build()
                    .show();
        }
    }


    /**
     * 按钮点击监听
     */
    class ButtonOnClickListener implements View.OnClickListener{

        private int buttonId;

        public ButtonOnClickListener(int buttonId) {
            this.buttonId = buttonId;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.button1) {
                cameraTask(buttonId);
            }
        }
    }


    /**
     * 按钮点击事件处理逻辑
     * @param buttonId
     */
    private void onClick(int buttonId) {
        if(buttonId==R.id.button1) {
            Intent intent = new Intent(getApplication(), CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }


}

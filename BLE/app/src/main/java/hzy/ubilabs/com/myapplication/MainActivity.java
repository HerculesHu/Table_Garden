package hzy.ubilabs.com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


import hzy.ubilabs.com.myapplication.BLE.fastble.scan.AnyScanActivity;


/**
 * 可以作为测试工具
 */
public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

   private Button serach_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onClick(View v) {
        if(v==serach_bt){
            startActivity(new Intent(MainActivity.this, AnyScanActivity.class));
        }
    }


    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        serach_bt = (Button) findViewById(R.id.serach);
        serach_bt.setOnClickListener(this);

    }


}

package com.example.ts_quartetto.qrcodereader;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class Activity_3 extends AppCompatActivity {
    private String server_file_name = new String("file");
    private String server_addr = new String("http://203.137.178.56/PosTom/votes/upload");
    Utility utility = new Utility();
    HandlerQRCode qrHandler = new HandlerQRCode();
    private String macAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macAddr = wInfo.getMacAddress();
        macAddr = macAddr.replaceAll("[^0-9a-zA-Z]","");

        Button btn_uploadData = (Button)findViewById(R.id.btn_uploadData);
        btn_uploadData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            utility.HttpPost(qrHandler.ChangeFilePath(StateControl.eventid + "_" + StateControl.eventday + "_" + macAddr + "_" + utility.GetUNIXTime()), server_file_name, server_addr);
                        } catch (IOException e) {e.printStackTrace();}
                    }
                })).start();

                StateControl.state = StateControl.STATE_STEP_4;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

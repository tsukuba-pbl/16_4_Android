package com.example.ts_quartetto.qrcodereader;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class VoteActivity extends AppCompatActivity {
    private String server_file_name = new String("file");
    private String server_addr = new String("http://vm05.sit.cs.tsukuba.ac.jp/PosTom/votes/upload");
    private HandlerQRCode qrHandler = new HandlerQRCode();
    private Utility utility = new Utility();
    private String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_main);
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        mac = wInfo.getMacAddress();
        mac = mac.replaceAll("[^0-9a-zA-Z]","");

        TextView eventName  = (TextView)findViewById(R.id.text_event_name);
        eventName.setText(qrHandler.eventname);
        TextView eventId   = (TextView)findViewById(R.id.text_event_id);
        eventId .setText(qrHandler.eventid);

        //create QRCodeReader button
        //ボタン押されたらQRコードリーダの表示
        Button ReaderButton = (Button) findViewById(R.id.btn_vote_start);
        ReaderButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), VoteActivityReadVoteQRCode.class);
                startActivity(intent);
            }
        });

        //ファイルクリア、投票ファイルをクリアする
        Button ClearButton = (Button) findViewById(R.id.btn_clear_file);
        ClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //ファイルの中身削除
                    qrHandler.Clear();
                }catch(Exception e){}
            }
        });

        Button SubmitButton = (Button) findViewById(R.id.btn_upload_result);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            utility.HttpPost(qrHandler.ChangeFilePath(qrHandler.eventid + "_" + mac), server_file_name, server_addr);
                        } catch (IOException e) {e.printStackTrace();}
                    }
                })).start();
            }
        });

        //イベント終了、投票ファイルとイベント名IDをクリアする
        Button FinishButton = (Button) findViewById(R.id.btn_vote_finish);
        FinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrHandler.Clear();
                qrHandler.eventname = "";
                qrHandler.eventid = "";
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

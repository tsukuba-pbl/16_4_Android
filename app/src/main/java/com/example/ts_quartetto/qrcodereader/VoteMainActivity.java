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

public class VoteMainActivity extends AppCompatActivity {
    private String server_file_name = new String("file");
    private String server_addr = new String("http://vm05.sit.cs.tsukuba.ac.jp/PosTom/downloads/upload");
    private QRHandler qrHandler = new QRHandler();
    private Utility utility = new Utility();
    private String mac;

    Button b_voteStart, b_clearFile, b_uploadResult, b_voteFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_main);
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        mac = wInfo.getMacAddress();
        mac = mac.replaceAll("[^0-9a-zA-Z]","");

        TextView eventInfo = (TextView)findViewById(R.id.text_event_info);
        eventInfo.setText(qrHandler.eventname + "\n" + qrHandler.eventid);

        //create QRCodeReader button
        //ボタン押されたらQRコードリーダの表示
        Button ReaderButton = (Button) findViewById(R.id.btn_vote_start);
        ReaderButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), QRCode_ReadVoteInfo.class);
                startActivity(intent);
            }
        });

        //投票終了、投票結果とイベント名をクリアする
        Button ClearButton = (Button) findViewById(R.id.btn_clear_file);
        ClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //ファイルの中身削除
                    qrHandler.Clear();
                    qrHandler.eventname = "";
                    qrHandler.eventid = "";
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
                            utility.HttpPost(qrHandler.ChangeFilePath(qrHandler.eventid, mac), server_file_name, server_addr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })).start();
            }
        });

        //投票終了、投票結果とイベント名をクリアする
        Button FinishButton = (Button) findViewById(R.id.btn_vote_finish);
        FinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }




}

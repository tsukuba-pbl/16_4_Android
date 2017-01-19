package com.example.ts_quartetto.qrcodereader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private String macAddr;     // cannot get machineAddr except in AndroidActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_main);
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macAddr = wInfo.getMacAddress();
        macAddr = macAddr.replaceAll("[^0-9a-zA-Z]","");

        TextView eventName = (TextView)findViewById(R.id.text_event_name);
        eventName.setText(StateControl.eventname);
        TextView eventId = (TextView)findViewById(R.id.text_event_id);
        eventId.setText(StateControl.eventid);
        TextView eventDay = (TextView)findViewById(R.id.text_event_day);
        eventDay.setText(StateControl.eventday+"日目");

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
            public void onClick(View v)
            {
                new AlertDialog.Builder(VoteActivity.this)
                        .setTitle("注意！")
                        .setMessage( "ファイルをクリアしますか\n")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //ファイルの中身削除
                                qrHandler.Clear();
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), VoteActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), VoteActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        });
        // 管理者以外の場合、ファイルクリア禁止
        if(!StateControl.enableFileClear)
            ClearButton.setEnabled(false);

        Button SubmitButton = (Button) findViewById(R.id.btn_upload_result);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            utility.HttpPost(qrHandler.ChangeFilePath(StateControl.eventid + "_" + StateControl.eventday + "_" + macAddr + "_" + utility.GetUNIXTime()), server_file_name, server_addr);
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
                StateControl.eventname = "";
                StateControl.eventid = "";
                StateControl.eventday = 0;
                StateControl.enableFileClear = false;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

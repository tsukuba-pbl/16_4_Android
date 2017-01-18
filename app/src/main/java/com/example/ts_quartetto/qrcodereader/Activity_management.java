package com.example.ts_quartetto.qrcodereader;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

public class Activity_management extends AppCompatActivity {
    HandlerQRCode qrHandler = new HandlerQRCode();
    Utility utility = new Utility();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        ListView list = (ListView)findViewById(R.id.list_file);
        list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,getData()));

        Button btn_clearFile = (Button)findViewById(R.id.btn_clearFile);

    }
    private List<String> getData(){
        List<String> data = new ArrayList<String>();
        File folder = new File(qrHandler.GetBasePath());
        File[] files = folder.listFiles();
        for(File file:files)
        {
            String fileName = file.getName();
            if(fileName.contains(".csv") && fileName.length() <= 15)    // 8 + 1 + 2 + .csv
            {
                String id = fileName.substring(0, 8);   // each of eventid
                String day = fileName.substring(fileName.indexOf("_")+1, fileName.length()-".csv".length());
                data.add(id + "-" + day + "日目");
            }
        }
        return data;
    }

    /*
    * likeName is eventid
    * */
    public void SearchLikeNameFromSD(String filepath, String likeName)
    {
        System.out.println("**************************" + likeName);
        File folder = new File(filepath);
        File[] files = folder.listFiles();
        for(File file:files){
            String fileName = file.getName();
            if(fileName.contains(likeName)){
                System.out.println("-------------------------->>" + fileName);
                TextView t = new TextView(this);
                String day = fileName.substring(fileName.indexOf("_")+1, fileName.length()-".csv".length());
                t.setText(StateControl.lastEventname + "-" + day + "日目");
                setContentView(t);
            }
        }
    }
}
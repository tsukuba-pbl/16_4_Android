package com.example.ts_quartetto.qrcodereader;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Activity_management extends AppCompatActivity {
    HandlerQRCode qrHandler = new HandlerQRCode();
    Utility utility = new Utility();
    ArrayList<String> targetFile = new ArrayList<String>();
    ArrayList<String> clearFile = new ArrayList<String>();

    private ListView infoList;
    private List<Map<String, Object>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        infoList = (ListView)findViewById(R.id.list_file);

        data = getData();
        MyAdapter adapter = new MyAdapter(this);
        infoList.setAdapter(adapter);

        Button btn_clearFile = (Button)findViewById(R.id.btn_clearFile);
        btn_clearFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new android.app.AlertDialog.Builder(Activity_management.this)
                        .setTitle("注意！")
                        .setMessage( "ファイルをクリアしますか\n")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //ファイルの中身削除
                                for(int i = 0;i < clearFile.size(); i ++){
                                    qrHandler.Clear(clearFile.get(i));
                                    System.out.println("!!!!!-->" + clearFile.get(i));
                                }
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), Activity_management.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), Activity_management.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        });
    }

    private List<Map<String, Object>> getData()
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        File folder = new File(qrHandler.GetBasePath());
        File[] files = folder.listFiles();
        for(File file:files)
        {
            String fileName = file.getName();
            if(fileName.contains(".csv") && fileName.length() <= 15)    // 8 + 1 + 2 + .csv
            {
                map = new HashMap<String, Object>();
                String eventid = fileName.substring(0, 8);   // each of eventid
                String day = fileName.substring(fileName.indexOf("_")+1, fileName.length()-".csv".length());
                targetFile.add(eventid + "_" + day);
                map.put("info", eventid + "-" + day + "日目");
                list.add(map);
            }
        }
        return list;
    }

    static class ViewHolder
    {
        public TextView info;
        public CheckBox chk;
    }

    public class MyAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater = null;
        private MyAdapter(Context context)
        {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            //How many items are in the data set represented by this Adapter.
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            // Get the data item associated with the specified position in the data set.
            return null;
        }
        @Override
        public long getItemId(int position) {
            //Get the row id associated with the specified position in the list.
            return position;
        }

        //Get a View that displays the data at the specified position in the data set.
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null)
            {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_file, null);
                holder.info = (TextView)convertView.findViewById(R.id.t_fileinfo);
                holder.chk = (CheckBox) convertView.findViewById(R.id.t_chk);
                holder.chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked )
                        {
                            System.out.println("---------------->" + position + targetFile.get(position));
                            clearFile.add(targetFile.get(position));
                        }
                        else
                        {
                            System.out.println("****************>" + position + targetFile.get(position));
                            clearFile.remove(targetFile.get(position));
                        }
                    }

                });
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.info.setText((String)data.get(position).get("info"));

            return convertView;
        }

    }






}

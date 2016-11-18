package com.example.ts_quartetto.qrcodereader;

import android.provider.MediaStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by we on 2016/10/5.
 */
public class QRHandler extends FileHandler {
    private String[] myjson = {"voter_id", "voter_name", "name_1", "name_2", "name_3"};
    private String basepath = "/storage/emulated/0/";
    private String filepath = "/storage/emulated/0/target.csv";   // SDcard path
    private Utility utility = new Utility();

    public String GetBasePath(){
        return basepath;
    }

    public String GetFilePath(){
        return filepath;
    }

    /*
    *   copy file from filepath and rename as MacAddr_CurrentDate.csv
    *   filepath will not be deleted
    * */
    public String ChangeFilePath(String mac) throws IOException {
        String newFileName = GetBasePath() + mac + "_" + utility.GetFileDate() + ".csv";

        File from = new File(filepath);
        File to = new File(newFileName);

        FileChannel in = new FileInputStream(from).getChannel();
        FileChannel out = new FileOutputStream(to).getChannel();
        out.transferFrom( in, 0, in.size() );

        return newFileName;
    }

    public Boolean Check(JSONObject json_obj)
    {
        if(myjson.length != json_obj.length())
            return false;

        for(int i = 0; i < myjson.length; i++)
            if(!json_obj.has(myjson[i]))
                return false;

        return true;
    }

    public void Save(JSONObject json_obj) {
        try {
            WriteToSD(
                    GetFilePath(),
                    json_obj.getString("voter_id") + "," + json_obj.getString("name_1") + "," + json_obj.getString("name_2") + "," + json_obj.getString("name_3") + "," +
                        utility.GetVoteDate());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String Read() throws IOException {
        return ReadFromSD(filepath);
    }

    public void Clear() {
        DeleteFromSD(filepath);
    }

}

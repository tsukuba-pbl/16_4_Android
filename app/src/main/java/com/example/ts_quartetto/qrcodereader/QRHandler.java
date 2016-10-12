package com.example.ts_quartetto.qrcodereader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by we on 2016/10/5.
 */
public class QRHandler extends FileHandler {
    private String basepath = "/storage/emulated/0/";
    private String filepath = "/storage/emulated/0/target.csv";   // SDcard path
    private Utility utility = new Utility();

    public String GetBasePath(){
        return basepath;
    }

    public String GetFilePath(){
        return filepath;
    }

    public String ChangeFilePath(String mac){
        String newFileName = GetBasePath() + mac + "_" + utility.GetFileDate() + ".csv";

        File newFile = new File(filepath);
        newFile.renameTo(new File(newFileName));

        return newFileName;
    }

    public void Save(JSONObject json_obj) {
        try {
            WriteToSD(filepath, json_obj.getString("voter_id") + "," + json_obj.getString("name_1") + "," + json_obj.getString("name_2") + "," + json_obj.getString("name_3")
                    + "," + utility.GetVoteDate());
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

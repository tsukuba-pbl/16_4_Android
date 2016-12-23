package com.example.ts_quartetto.qrcodereader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by we on 2016/10/5.
 */
public class HandlerQRCode extends HandlerFile {
    static public String eventid = "";
    static public String eventname = "";

    private String[] jsonEventFormat = {"event_id", "event_name"};
    private String[] jsonVoteFormat = {"event_id", "voter_id", "voter_name", "name_1", "name_2", "name_3"};
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
    public String ChangeFilePath(String name) throws IOException {
        String newFileName = GetBasePath() + name + "_" + utility.GetUNIXTime() + ".csv";

        File from = new File(filepath);
        File to = new File(newFileName);

        FileChannel in = new FileInputStream(from).getChannel();
        FileChannel out = new FileOutputStream(to).getChannel();
        out.transferFrom( in, 0, in.size() );

        return newFileName;
    }

    public Boolean CheckEventId(String id)
    {
        return eventid.equals(id);
    }

    public Boolean CheckEventQRCode(JSONObject obj)
    {
        if(jsonEventFormat.length != obj.length())
            return false;
        for(int i = 0; i < jsonEventFormat.length; i++)
            if(!obj.has(jsonEventFormat[i]))
                return false;
        return true;
    }

    public Boolean CheckVoteQRCode(JSONObject obj)
    {
        if(jsonVoteFormat.length != obj.length())
            return false;
        for(int i = 0; i < jsonVoteFormat.length; i++)
            if(!obj.has(jsonVoteFormat[i]))
                return false;
        return true;
    }

    public void Save(JSONObject json_obj) {
        try {
            WriteToSD(  GetFilePath(),
                    json_obj.getString("voter_id") + "," +
                            json_obj.getString("name_1") + "," +
                            json_obj.getString("name_2") + "," +
                            json_obj.getString("name_3") + "," +
                            utility.GetVoteDate());
        }
        catch (JSONException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
    }

    public String Read() throws IOException { return ReadFromSD(filepath);}

    public void Clear() {
        DeleteFromSD(filepath);
    }

}
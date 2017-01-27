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
    private String[] jsonEventFormat = {"event_id", "event_name", "event_day"};
    private String[] jsonVoteFormat = {"voter_id", "event_id", "name_1", "name_2", "name_3"};
    private String basepath = "/storage/emulated/0/";
    private Utility utility = new Utility();

    public String GetBasePath(){
        return basepath;
    }
    public String GetFilePath() {
        return basepath + StateControl.eventid + "_" +  StateControl.eventday + ".csv";
    }

    /*
    *   copy file from filepath and rename as MacAddr_CurrentDate.csv
    *   filepath will not be deleted
    * */
    public String ChangeFilePath(String name) throws IOException {
        String newFileName = GetBasePath() + name + ".csv";

        File from = new File(new String(GetFilePath()));
        File to = new File(newFileName);

        FileChannel in = new FileInputStream(from).getChannel();
        FileChannel out = new FileOutputStream(to).getChannel();
        out.transferFrom( in, 0, in.size() );

        return newFileName;
    }

    public Boolean CheckEventId(String id)
    {
        return StateControl.eventid.equals(id);
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

    public void Save(String path, JSONObject json_obj) {
        try {
            WriteToSD(  path,
                    json_obj.getString("voter_id") + "," +
                    json_obj.getString("name_1") + "," +
                    json_obj.getString("name_2") + "," +
                    json_obj.getString("name_3") + "," +
                    utility.GetVoteDate());
        }
        catch (JSONException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
    }

    public String Read() throws IOException {
        return ReadFromSD(GetFilePath());
    }

    public String Read(String path) throws IOException {
        return ReadFromSD(path);
    }

    public void Clear() {
        DeleteFromSD(GetFilePath());
    }

    public void Clear(String likeName) {
        DeleteLikeNameFromSD(GetBasePath(), likeName);
    }
}
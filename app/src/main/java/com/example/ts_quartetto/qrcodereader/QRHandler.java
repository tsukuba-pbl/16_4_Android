package com.example.ts_quartetto.qrcodereader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by we on 2016/10/5.
 */
public class QRHandler extends FileHandler
{
    private String filepath = "/storage/emulated/0/target.csv";   // SDcard path

    public void Save(JSONObject json_obj) {
        try {
            WriteToSD(filepath, json_obj.getString("voter_id") + "," + json_obj.getString("name_1") + "," + json_obj.getString("name_2") + "," + json_obj.getString("name_3"));
        }
        catch (JSONException e) {e.printStackTrace();  }
        catch (IOException e)   {e.printStackTrace();  }
    }

    public String Read() throws IOException {
        return ReadFromSD(filepath);
    }

    public void Clear()
    {
        DeleteFromSD(filepath);
    }

    public String GetFilePath()
    {
        return filepath;
    }
}

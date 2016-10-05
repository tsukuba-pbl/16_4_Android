package com.example.ts_quartetto.qrcodereader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by we on 2016/10/5.
 */
public class FileHandler {
    private String filepath = "/storage/emulated/0/target.csv";   // SDcard path

    public void SaveJSON(JSONObject json_obj) {
        try {
            WriteToSD(json_obj.getString("voter_id") + "," + json_obj.getString("name_1") + "," + json_obj.getString("name_2") + "," + json_obj.getString("name_3"));
        } catch (JSONException e) {
            try { WriteToSD("JSONの型違う・・・");
            } catch (IOException e1) { e1.printStackTrace(); }
            e.printStackTrace();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void WriteToSD(String str) throws IOException {
        File myFile = new File(filepath);
        if (!myFile.exists())
            myFile.createNewFile();
        FileOutputStream fos;
        byte[] data = str.getBytes();
        try {
            fos = new FileOutputStream(myFile, true);
            fos.write(data);
            fos.write('\n');
            fos.flush();
            fos.close();
        } catch (Exception e) { System.out.println("e: " + e);}
    }

    public String ReadFromSD() throws IOException {
        FileInputStream is = null;
        String info = "";
        try {
            is = new FileInputStream(filepath);
            byte[] car = new byte[10];
            int len = 0;
            while(-1 != (len = is.read(car)))
            {
                info += new String(car, 0, len);
                System.out.println(info);
            }
        } catch (FileNotFoundException e1) { e1.printStackTrace(); }

        return info;
    }
}

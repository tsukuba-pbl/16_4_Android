package com.example.ts_quartetto.qrcodereader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by we on 2016/10/5.
 */
public class HandlerFile {

    // basePathの下で、すべてのファイルの中で、likeNameを含めているファイルを削除する
    // SaveファイルとUploadファイルの名前が違って、一緒に削除したいです
    public void DeleteLikeNameFromSD(String filepath, String likeName)
    {
        File folder = new File(filepath);
        File[] files = folder.listFiles();
        for(File file:files){
            if(file.getName().contains(likeName)){
                file.delete();
            }
        }
    }

    public void DeleteFromSD(String filepath)
    {
        File file = new File(filepath);
        file.delete();
    }

    public void WriteToSD(String path, String str) throws IOException {
        File myFile = new File(path);
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
        } catch (Exception e) { e.printStackTrace();}
    }

    public String ReadFromSD(String path) throws IOException {
        FileInputStream is = null;
        String info = "";
        try {
            is = new FileInputStream(path);
            byte[] car = new byte[10];
            int len = 0;
            while(-1 != (len = is.read(car)))
            {
                info += new String(car, 0, len);
            }
        } catch (FileNotFoundException e1) { e1.printStackTrace(); }

        return info;
    }
}

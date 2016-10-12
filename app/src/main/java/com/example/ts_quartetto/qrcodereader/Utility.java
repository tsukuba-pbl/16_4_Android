package com.example.ts_quartetto.qrcodereader;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;

/**
 * Created by we on 2016/10/5.
 */
public class Utility {

    public void HttpPost(String filepath, String filename, String addr)
    {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost httpPost = new HttpPost("http://ts-quartetto.herokuapp.com/sprint1_9/receive_file.php");
            HttpPost httpPost = new HttpPost(addr);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            FileBody fileBody = new FileBody(new File(filepath));
            multipartEntity.addPart(filename, fileBody);
            httpPost.setEntity(multipartEntity);
            httpClient.execute(httpPost, responseHandler);
            System.out.println("333AAAAAAAAAAAAAAAAAAAAAAAA");
        } catch (MalformedURLException e) {e.printStackTrace();
        } catch (IOException e) {e.printStackTrace();}
    }

    public String GetVoteDate()
    {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sDateFormat.format(new java.util.Date());
    }

    public String GetFileDate()
    {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_HHmm_ss");
        return sDateFormat.format(new java.util.Date());
    }

}

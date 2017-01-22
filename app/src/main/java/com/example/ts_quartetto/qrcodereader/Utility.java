package com.example.ts_quartetto.qrcodereader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.apache.http.HttpResponse;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by we on 2016/10/5.
 */
public class Utility {
    private Config config = new Config();
    private AlertDialog dia;

    public void HttpPost(String filepath, String filename, String addr)
    {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            addr = new String("http://203.137.178.56/PosTom/votes/upload");
            //HttpPost httpPost = new HttpPost("http://ts-quartetto.herokuapp.com/sprint1_9/receive_file.php");
            HttpPost httpPost = new HttpPost(addr);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            FileBody fileBody = new FileBody(new File(filepath));
            multipartEntity.addPart(filename, fileBody);
            httpPost.setEntity(multipartEntity);
            String response = httpClient.execute(httpPost, responseHandler);
            Log.d("response", "data :" + response);
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

    public String GetUNIXTime()
    {
        String ret = String.valueOf(System.currentTimeMillis()/1000);   //  Mill/1000- > seconds
        return ret;
    }

    public boolean StrLike(String str,String regex,boolean bool)
    {
        regex = regex.replaceAll("\\*", ".*");
        regex = regex.replaceAll("\\?", ".");
        Pattern pattern = Pattern.compile(regex,bool? Pattern.CASE_INSENSITIVE:0);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    public void WriteDebugLog(String s1, String s2)
    {
        if(config.isDebug())
            Log.d(s1, s2);
    }

}

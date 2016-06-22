package com.example.ts_quartetto.qrcodereader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.common.StringUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRCodeReaderActivity extends AppCompatActivity {
    private Boolean isJSONFile = false;
    private String filename = "target.csv";                         // app path
    private String myfilepath = "/storage/emulated/0/target.csv";   // SDcard path
    private FileOutputStream fout;
    private FileInputStream fin;
    public String file_content = "";
    public JSONObject file_content_json;
   // private  CompoundBarcodeView mBarcodeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_reader);
        //QRCodeScannerの起動
        IntentIntegrator ii = new IntentIntegrator(this);
        ii.setScanningRectangle(700, 700);
        //インカメ
        ii.setCameraId(1);
        ii.initiateScan();
    }
    //スキャンした時の結果の処理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "onActivityResult Start");
        Log.d("requestCode", "requestCode: " + requestCode + " resultCode: " + resultCode + " data: " + data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // null の場合
        if (intentResult == null) {
            Log.d("TAG", "Weird");
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (intentResult.getContents() == null) {
            // 戻るボタンをタップした場合
            Log.d("TAG", "Cancelled Scan");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            // カメラで読み取った情報をラベルに表示
            Log.d("TAG", "Scanned! "+intentResult.getContents());
            try {
                //読み取ったJSONデータをパース
                //file_content = intentResult.getContents();
                /*20160621 DUYAN :
                    If received info which from QRcode is not a standard JSON file,
                    myStr could not be printf or compare, why ???
                */
                String myStr = new String(intentResult.getContents());
                file_content_json = new JSONObject(myStr);
                // isJOSNFile set to true only if received a standard JSON file, no exception occurred
                isJSONFile = true;
            }catch (JSONException e){
                e.printStackTrace();
            }
            // QRコードを読み取った場合アラートダイアログを表示する。
            if(isJSONFile)
            {
                try {
                    new AlertDialog.Builder(QRCodeReaderActivity.this)
                        .setTitle("QRコードリーダー")
                        .setMessage("読み取ったデータ: " + intentResult.getContents() + "\n" + "書き込む前のファイルの中身\n" + MyReadFromSD())
                        .setNegativeButton("終了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //取得したデータをファイルに書き込む
                                //MyWrite(file_content);

                                try {
                                    MyWriteToSD(file_content_json.getString("voter_name") + "," + file_content_json.getString("voter_id") + "," + file_content_json.getString("name_1") + "," + file_content_json.getString("name_2") + "," + file_content_json.getString("name_3"));
                                } catch (JSONException e) {
                                    try {
                                        MyWriteToSD("JSONの型違う・・・");
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                // ファイルに書き込んだ内容をアラートダイアログを表示する。
                                try {
                                    new AlertDialog.Builder(QRCodeReaderActivity.this)
                                            .setTitle("ファイルに書き込み後")
                                            .setMessage( "書き込み後のファイルの中身\n" + MyReadFromSD())
                                            .setNegativeButton("終了", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // "Cancel"ボタンが押されたらアラートダイアログを閉じる。
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(getApplicationContext(), QRCodeReaderActivity.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(QRCodeReaderActivity.this, "Not a standard JSON file !!!, Pls try again ~~", Toast.LENGTH_SHORT).show();
            }
            //Intent intent = new Intent(getApplicationContext(), QRCodeReaderActivity.class);
            //startActivity(intent);
        }
    }

    private void MyWriteToSD(String str) throws IOException {
        //  File myFile = new File(Environment.getExternalStorageDirectory(), filename);
        File myFile = new File(myfilepath);
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
        } catch (Exception e) {
            System.out.println("e: " + e);
        }
    }

    private String MyReadFromSD() throws IOException {
        FileInputStream is = null;
        String info = "";
        //   String fpath = Environment.getExternalStorageDirectory() + filename;
        try {
            is = new FileInputStream(myfilepath);
            byte[] car = new byte[10];
            int len = 0;
            while(-1 != (len = is.read(car)))
            {
                info += new String(car, 0, len);
                System.out.println(info);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        return info;
    }
}

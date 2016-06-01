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

import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.common.StringUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRCodeReaderActivity extends AppCompatActivity {
    private String filename = "target.csv";
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
                file_content_json = new JSONObject(intentResult.getContents());
            }catch (JSONException e){
                e.printStackTrace();
            }
            // QRコードを読み取った場合アラートダイアログを表示する。
            new AlertDialog.Builder(QRCodeReaderActivity.this)
                    .setTitle("QRコードリーダー")
                    .setMessage("読み取ったデータ: " + intentResult.getContents() + "\n" + "書き込む前のファイルの中身\n" + MyRead())
                    .setNegativeButton("終了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //取得したデータをファイルに書き込む
                            //MyWrite(file_content);

                            try {
                                MyWrite(file_content_json.getString("voter") + "," + file_content_json.getString("name_1") + "," + file_content_json.getString("name_2") + "," + file_content_json.getString("name_3"));
                            } catch (JSONException e) {
                                MyWrite("JSONの型違う・・・");
                                e.printStackTrace();
                            }
                            // ファイルに書き込んだ内容をアラートダイアログを表示する。
                            new AlertDialog.Builder(QRCodeReaderActivity.this)
                                    .setTitle("ファイルに書き込み後")
                                    .setMessage( "書き込み後のファイルの中身\n" + MyRead())
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
                            dialog.dismiss();
                        }
                    })
                    .show();
            //Intent intent = new Intent(getApplicationContext(), QRCodeReaderActivity.class);
            //startActivity(intent);
        }
    }

    /**
     * 指定されたファイルにデータを書き込む関数
     * @param data
     */
    private void MyWrite(String data)
    {
        try {
            //testText.setText("Write button");
            fout = openFileOutput(filename, Activity.MODE_APPEND);
            fout.write(data.getBytes());
            fout.write("\n".getBytes());
            fout.flush();
            fout.close();
        }catch(Exception e){}
    }

    /**
     * 指定されたファイルからデータを読み込んでデータを返却する関数
     * @return
     */
    private String MyRead() {
        try {
            fin = this.openFileInput(filename);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (fin.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            fin.close();
            arrayOutputStream.close();
            //ファイルの中のデータを取得
            String content = new String(arrayOutputStream.toByteArray());
            return content;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

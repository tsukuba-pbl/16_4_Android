package com.example.ts_quartetto.qrcodereader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class QRCodeReaderActivity extends AppCompatActivity {
    private Boolean isJSONFile = false;
    public JSONObject file_content_json;

    public QRHandler qrHandler = new QRHandler();

    public Config config = new Config();
    private Utility utility = new Utility();

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
        utility.WriteDebugLog("onActivityResult", "onActivityResult Start");
        utility.WriteDebugLog("requestCode", "requestCode: " + requestCode + " resultCode: " + resultCode + " data: " + data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // null の場合
        if (intentResult == null) {
            utility.WriteDebugLog("TAG", "Weird");

            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (intentResult.getContents() == null) {
            // 戻るボタンをタップした場合
            utility.WriteDebugLog("TAG", "Cancelled Scan");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            // カメラで読み取った情報をラベルに表示
                utility.WriteDebugLog("TAG", "Scanned! " + intentResult.getContents());
            try {
                file_content_json = new JSONObject(new String(intentResult.getContents()));

                /*20160621 DUYAN :
                    If received info which from QRcode is not a standard JSON file,
                    exception occurred & isJSONFile = false.
                    isJOSNFile set to true only if received a standard JSON file, no exception occurred
                */
                isJSONFile = true;
            }catch (JSONException e){e.printStackTrace();}

            if(!isJSONFile) {
                Toast.makeText(QRCodeReaderActivity.this, "Not a standard JSON file !!!, Pls try again ~~", Toast.LENGTH_SHORT).show();
                return;
            }

            // QRコードを読み取った場合アラートダイアログを表示する。
            if(config.isDebug())
            {
                try {
                    new AlertDialog.Builder(QRCodeReaderActivity.this)
                            .setTitle("QRコードリーダー")
                            .setMessage("読み取ったデータ: " + intentResult.getContents() + "\n" + "書き込む前のファイルの中身\n" + qrHandler.Read())
                            .setNegativeButton("終了", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //取得したデータをファイルに書き込む
                                    qrHandler.Save(file_content_json);

                                    // ファイルに書き込んだ内容をアラートダイアログを表示する。
                                    try {
                                        new AlertDialog.Builder(QRCodeReaderActivity.this)
                                                .setTitle("ファイルに書き込み後")
                                                .setMessage( "書き込み後のファイルの中身\n" + qrHandler.Read())
                                                .setNegativeButton("終了", new DialogInterface.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // "Cancel"ボタンが押されたらアラートダイアログを閉じる。
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(getApplicationContext(), QRCodeReaderActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .show();
                                    } catch (IOException e) { e.printStackTrace();}
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } catch (IOException e) {e.printStackTrace();}
            }
            else
            {
                qrHandler.Save(file_content_json);
                Intent intent = new Intent(getApplicationContext(), QRCodeReaderActivity.class);
                startActivity(intent);
            }
        }
    }
}

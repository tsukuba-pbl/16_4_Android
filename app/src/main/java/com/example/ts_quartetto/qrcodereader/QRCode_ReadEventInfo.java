package com.example.ts_quartetto.qrcodereader;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class QRCode_ReadEventInfo extends AppCompatActivity {
    private Boolean isJSONFile = false;
    public JSONObject event_json;

    public QRHandler qrHandler = new QRHandler();

    public Config config = new Config();
    private Utility utility = new Utility();

    AlertDialog dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_event_id);

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
                event_json = new JSONObject(new String(intentResult.getContents()));

                /*20160621 DUYAN :
                    If received info which from QRcode is not a standard JSON file,
                    exception occurred & isJSONFile = false.
                    isJOSNFile set to true only if received a standard JSON file, no exception occurred
                */
                isJSONFile = true;
            }catch (JSONException e){e.printStackTrace();}

            if(!isJSONFile) {
                Toast.makeText(QRCode_ReadEventInfo.this, "Not a standard JSON file !!!, Pls try again ~~", Toast.LENGTH_SHORT).show();
                return;
            }

            // QRにイベント名がないので、読み取る再開
            if(!qrHandler.CheckEventQRCode(event_json))
            {
                Toast.makeText(QRCode_ReadEventInfo.this, "Not found event_name !!!, Pls check your QR code ~~", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), QRCode_ReadEventInfo.class);
                startActivity(intent);
            }

            // イベント名を抽出し保存してから、投票画面に行く
            try {
                qrHandler.eventid = event_json.getString("event_id");
                qrHandler.eventname = event_json.getString("event_name");
                Intent intent = new Intent(getApplicationContext(), VoteMainActivity.class);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

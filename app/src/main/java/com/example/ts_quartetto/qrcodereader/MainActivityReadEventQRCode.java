package com.example.ts_quartetto.qrcodereader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivityReadEventQRCode extends AppCompatActivity {
    private Boolean isJSONFile = false;
    public JSONObject event_json;

    public HandlerQRCode qrHandler = new HandlerQRCode();
    private Utility utility = new Utility();

    AlertDialog dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_event_id);

        //QRCodeScannerの起動
        IntentIntegrator ii = new IntentIntegrator(this);
        ii.setScanningRectangle(700, 700);
        //Prefer to using back camera when reading event QR code
        if(utility.hasBackCamera())
            ii.setCameraId(0);
        else if(utility.hasFrontCamera())
            ii.setCameraId(1);
        else {
            String title = new String("投票できません");
            String msg = new String("カメラを搭載する集計機をご利用ください");
            String btn = new String("　");
            int displayTime = 2000;

            dia = new AlertDialog.Builder(MainActivityReadEventQRCode.this)
                    .setTitle(title)
                    .setMessage(msg)
                    .setNegativeButton(btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    dia.dismiss();
                    Intent intent = new Intent(getApplicationContext(), VoteActivityReadVoteQRCode.class);
                    startActivity(intent);
                }
            }, displayTime);
            return;
        }
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

            if(!isJSONFile || !qrHandler.CheckEventQRCode(event_json))
            {
                String title = new String("投票");
                String msg = new String();
                String btn = new String("　");
                int displayTime = 2000;

                if(!isJSONFile)
                    msg = new String("データ形式が違います。QRコードを確認してください");
                else
                    msg = new String("イベント用のQRコードではありません。QRコードを確認してください");
                //QRコード読み取り終了したら、1秒ぐらい提示メッセージを表示する
                dia = new AlertDialog.Builder(MainActivityReadEventQRCode.this)
                        .setTitle(title)
                        .setMessage(msg)
                        .setNegativeButton(btn, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .show();

                //2秒後で、上の提示メッセージを隠す
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        dia.dismiss();
                        Intent intent = new Intent(getApplicationContext(), Activity_1.class);
                        startActivity(intent);
                    }
                }, displayTime);
                return;
            }

            // イベント名を抽出し保存してから、投票画面に行く
            try {
                StateControl.eventid = event_json.getString("event_id");
                StateControl.eventname = event_json.getString("event_name");
                StateControl.eventday = event_json.getInt("event_day");
                StateControl.state = StateControl.STATE_STEP_2;

                // Refer to UI design, we need clear file after Process_1 or 4, only assign the value at here and do not init it
                StateControl.lastEventname = StateControl.eventname;
                StateControl.lastEventid = StateControl.eventid;

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

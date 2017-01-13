package com.example.ts_quartetto.qrcodereader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity_4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4);

        Button btn_voteFinish = (Button)findViewById(R.id.btn_voteFinish);
        btn_voteFinish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                StateControl.state = StateControl.STATE_STEP_1;
                StateControl.eventname = "";
                StateControl.eventid = "";
                StateControl.eventday = 0;

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

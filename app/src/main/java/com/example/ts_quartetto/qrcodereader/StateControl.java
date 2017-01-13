package com.example.ts_quartetto.qrcodereader;

/**
 * Created by DUYAN on 2017/1/13.
 */

public class StateControl {
    public static final int STATE_STEP_INIT = 0;
    public static final int STATE_STEP_1 = 1;
    public static final int STATE_STEP_2 = 2;
    public static final int STATE_STEP_3 = 3;
    public static final int STATE_STEP_4 = 4;

    public static int state;
    public static String eventid = " ";
    public static String eventname = " ";
    public static Integer eventday = 0;
    public static Boolean enableFileClear = false;
}

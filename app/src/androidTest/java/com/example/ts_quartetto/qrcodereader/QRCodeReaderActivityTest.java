package com.example.ts_quartetto.qrcodereader;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by tsuruda_tomohiro on 2016/06/30.
 */
public class QRCodeReaderActivityTest {
    private JSONObject jsonObject;
    QRHandler qh = new QRHandler();
    private String json_msg =  "{\"ID\":\"test\",\"Name\":\"テスト\",\"Name_1\":\"222\",\"Name_2\":\"999\",\"Name_3\":\"333\"}";
    private String correct_json_msg = "{\"voter_id\":\"test\",\"voter_name\":\"テスト\",\"name_1\":\"222\",\"name_2\":\"999\",\"name_3\":\"333\"}";

    private String mytestpath = "/storage/emulated/0/test.csv";   // SDcard path
    FileHandler fh = new FileHandler();

    @Before
    public void setUp() throws Exception {
        System.out.println("setUp----------------------------------------");
        fh.DeleteFromSD(mytestpath);
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("tearDown----------------------------------------");
    }

    /*
    *   target format is voter_id, name_1, name_2, name_3
    *   Write will fail if format is not correct
    * */
    @Test
    public void WriteIncorrectJson() throws Exception {
        try {
            jsonObject = new JSONObject(json_msg);
        } catch (JSONException e) {}
        qh.Clear();
        qh.Save(jsonObject);
      //  assertEquals(" ", qh.Read());             // fail
      //  assertEquals("asda", qh.Read());          // fail
        assertEquals("", qh.Read());
    }

    /*
    *   target format is voter_id, name_1, name_2, name_3
    *   Write will success if format is correct
    * */
    @Test
    public void WriteCorrectJson() throws Exception {
        try {
            jsonObject = new JSONObject(correct_json_msg);
        } catch (JSONException e) {}
        qh.Save(jsonObject);
        qh.Clear();
        qh.Save(jsonObject);
    //    assertEquals("", qh.Read());              // fail
    //    assertEquals(" ", qh.Read());              // fail
    //    assertEquals("asdaf", qh.Read());              // fail
        assertEquals("test,222,999,333", qh.Read().substring(0,16));    // 16.10.12 as CSV added current date, equal substring will be ok
    }

    /*
    *  confirm target file has been deleted after Clear() be called
    * */
    @Test
    public void ClearFile() throws IOException {
        try{
            jsonObject = new JSONObject(correct_json_msg);
        }catch  (JSONException e){}
        qh.Save(jsonObject);
    //  assertNull(qh.Read());                 // fail
        assertNotNull(qh.Read());
    //    assertEquals("asd", qh.Read());      // fail

        qh.Clear();

     //   assertEquals(" ", qh.Read());        // fail
     //   assertEquals("asdf", qh.Read());     // fail
        assertEquals("", qh.Read());
    }

    /*
    *  confirm target file contains correct datetime BY regular expression
    * */
    @Test
    public void ContainDateByRegExp() throws IOException {
        try{
            jsonObject = new JSONObject(correct_json_msg);
        }catch  (JSONException e){}
        qh.Save(jsonObject);

        // yyyy/mm/dd
        String reg3 = "((((1[6-9]|[2-9]\\d)\\d{2})/(1[02]|0?[13578])/([12]\\d|3[01]|0?[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})/(1[012]|0?[13456789])/([12]\\d|30|0?[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(1\\d|2[0-8]|0?[1-9]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))";
        // HH:mm:ss
        String reg4 = "([01]?\\d|2[0-3]):[0-5]?\\d:[0-5]?\\d";

        Boolean result1 = Pattern.matches(reg3, qh.Read().substring(17,27));    // yyyy/mm/dd
        Boolean result2 = Pattern.matches(reg4, qh.Read().substring(28,35));    // HH:mm:ss (exclude '\n')
        assertEquals(result1, true);
        assertEquals(result2, true);
    }

    /*
    *  confirm target file contains correct datetime BY dateformat
    * */
    @Test
    public void ContainDateByDateFormat() throws IOException {
        try{
            jsonObject = new JSONObject(correct_json_msg);
        }catch  (JSONException e){}
        qh.Save(jsonObject);
        String res = qh.Read().substring(17, 36);
        System.out.println(res + "------------------------------------------------------------");
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try{
            format.parse(res);
        } catch (ParseException e) {
            assertEquals(1, 0);     // if format is not correct, here will occur a fail case
        }
    }

    /*
    *
    *
    * */
    @Test
    public void WriteDate() throws IOException {
        try {
            jsonObject = new JSONObject(correct_json_msg);
        } catch (JSONException e) {}
        qh.Save(jsonObject);
        assertEquals("test,222,999,333", qh.Read().substring(0,16));
//        assertEquals("test,222,999,333\n", qh.Read().substring(0,17));    // fail
//        assertEquals("test,222,999,333", qh.Read());                      // fail
    }

    /**
     * ファイル書き込みが出来るかどうか
     * @throws Exception
     */
    @Test
    public void isWrite() throws Exception{
        String data = "test,111,222,333";
        fh.WriteToSD(mytestpath, data);
        assertEquals(data+"\n", fh.ReadFromSD(mytestpath));
    }
}
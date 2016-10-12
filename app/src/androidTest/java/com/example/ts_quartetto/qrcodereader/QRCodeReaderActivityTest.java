package com.example.ts_quartetto.qrcodereader;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

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
    *
    *
    * */
    @Test
    public void WriteDate() throws IOException {
        try {
            jsonObject = new JSONObject(correct_json_msg);
        } catch (JSONException e) {}
        qh.Save(jsonObject);
        assertNotSame("test,222,999,333", qh.Read());

//        assertEquals("test,222,999,333", qh.Read());  // fail
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
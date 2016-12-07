package com.example.ts_quartetto.qrcodereader;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by tsuruda_tomohiro on 2016/06/30.
 */
public class QRCodeReaderActivityTest {
    private JSONObject jsonObject;
    HandlerQRCode qh = new HandlerQRCode();
    private String json_msg =  "{\"ID\":\"test\",\"Name\":\"テスト\",\"Name_1\":\"222\",\"Name_2\":\"999\",\"Name_3\":\"333\"}";
    private String correct_json_msg = "{\"voter_id\":\"test\",\"voter_name\":\"テスト\",\"name_1\":\"222\",\"name_2\":\"999\",\"name_3\":\"333\"}";

    private String json_event = "{\"event_id\":\"007\",\"event_name\":\"enpit\"}";
    private String json_vote = "{\"event_id\":\"007\",\"voter_id\":\"test\",\"voter_name\":\"テスト\",\"name_1\":\"222\",\"name_2\":\"999\",\"name_3\":\"333\"}";

    private String mytestpath = "/storage/emulated/0/test.csv";   // SDcard path
    HandlerFile fh = new HandlerFile();

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
    *  confirm target file contains correct datetime BY dateformat
    * */
    @Test
    public void ContainDateByDateFormat() throws IOException, ParseException {
        try{
            jsonObject = new JSONObject(correct_json_msg);
        }catch  (JSONException e){}
        qh.Save(jsonObject);
        String res = qh.Read().substring(17, 36);
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            assertNotNull(format.parse(res));   // if res is incorrect, return NULL
        } catch (ParseException e) {
            e.printStackTrace();
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

    /*
    *   投票のJSONファイル各Key値が正しいかどうかをチェックする
    * */
    @Test
    public void CheckVoteJsonFormat() throws JSONException {
        //　Key値不十分です、name_2とname_3がない
        JSONObject j1 = new JSONObject("{\"voter_id\":\"test\",\"voter_name\":\"テスト\",\"name_1\":\"222\"}");
        //　Key値間違い、idとnameは認識できません
        JSONObject j2 = new JSONObject("{\"id\":\"test\",\"name\":\"テスト\",\"name_1\":\"222\",\"name_2\":\"999\",\"name_3\":\"333\"}");
        //　認識できないKey値がある、name_4
        JSONObject j3 = new JSONObject("{\"voter_id\":\"test\",\"voter_name\":\"テスト\",\"name_1\":\"222\",\"name_2\":\"999\",\"name_3\":\"333\", \"name_4\":\"444\"}");
        // 認識できないKey値がある、event_name
        JSONObject j4 = new JSONObject("{\"event_name\":\"enpit\",\"voter_id\":\"test\",\"voter_name\":\"テスト\",\"name_1\":\"222\",\"name_2\":\"999\",\"name_3\":\"333\"}");
        //　正しいKey値を含めたJSONメッセージです
        JSONObject j_ok = new JSONObject(json_vote);

        assertEquals(qh.CheckVoteQRCode(j1), false);
        assertEquals(qh.CheckVoteQRCode(j2), false);
        assertEquals(qh.CheckVoteQRCode(j3), false);
        assertEquals(qh.CheckVoteQRCode(j4), false);
        assertEquals(qh.CheckVoteQRCode(j_ok), true);
    }

    /*
    *   イベントのJSONファイル各Key値が正しいかどうかをチェックする
    * */
    @Test
    public void CheckEventJsonFormat() throws JSONException {
        //　Key値不十分です、event_nameがない
        JSONObject j1 = new JSONObject("{\"event_id\":\"007\"}");
        //　Key値不十分です、event_idがない
        JSONObject j2 = new JSONObject("{\"event_name\":\"enpit\"}");
        // 認識できないKey値がある、name_1
        JSONObject j3 = new JSONObject("{\"event_id\":\"007\",\"event_name\":\"enpit\",\"name_1\":\"222\"}");
        //　正しいKey値を含めたJSONメッセージです
        JSONObject j_ok = new JSONObject(json_event);

        assertEquals(qh.CheckEventQRCode(j1), false);
        assertEquals(qh.CheckEventQRCode(j2), false);
        assertEquals(qh.CheckEventQRCode(j3), false);
        assertEquals(qh.CheckEventQRCode(j_ok), true);
    }
}
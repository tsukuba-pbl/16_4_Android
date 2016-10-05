package com.example.ts_quartetto.qrcodereader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by tsuruda_tomohiro on 2016/06/30.
 */
public class QRCodeReaderActivityTest {
    private String mytestpath = "/storage/emulated/0/test.csv";   // SDcard path
    FileHandler fh = new FileHandler();
    @Before
    public void setUp() throws Exception {
        fh.DeleteFromSD(mytestpath);
//        try {
//            //ファイルの中身削除
//            File file = new File(myfilepath);
//            file.delete();
//        }catch(Exception e){}
//        qa = new QRCodeReaderActivity();
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * ファイル書き込みが出来るかどうか
     * @throws Exception
     */
    @Test
    public void isWrite() throws Exception{
        fh.DeleteFromSD(mytestpath);
        String data = "test,111,222,333";
        fh.WriteToSD(mytestpath, data);
        assertEquals(data+"\n", fh.ReadFromSD(mytestpath));
    }
}
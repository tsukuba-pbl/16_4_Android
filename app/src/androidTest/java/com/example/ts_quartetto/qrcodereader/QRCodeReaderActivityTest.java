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
    private String myfilepath = "/storage/emulated/0/target.csv";   // SDcard path
    QRCodeReaderActivity qa;
    @Before
    public void setUp() throws Exception {
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
    @Test
    public void read_CSV() throws Exception{
//        qa.MyWriteToSD("test");
//        assertEquals(qa.MyReadFromSD(),"test");
        assertEquals(4, 2 + 3);
    }
}
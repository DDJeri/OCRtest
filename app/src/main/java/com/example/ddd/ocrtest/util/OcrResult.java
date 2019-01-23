package com.example.ddd.ocrtest.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class OcrResult {
    private String ocrResult;
    private String impagePath;

    public OcrResult(String ocrResult,String impagePath){
        this.ocrResult = ocrResult;
        this.impagePath = impagePath;
    }

    public Bitmap GetBitmap(){
        FileInputStream in = null;
        Bitmap bitmap;
        try {
            in = new FileInputStream(impagePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File(impagePath);

        if(file.exists()) {
            bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        }
        return null;
    }

    public String getOcrResult(){
        return ocrResult;
    }
    public String getImpagePath(){
        return impagePath;
    }
}

package com.example.ddd.ocrtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.ddd.ocrtest.db.Picture;
import com.example.ddd.ocrtest.util.OcrAdapter;
import com.example.ddd.ocrtest.util.OcrResult;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private List<OcrResult> ocrShow = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        List<Picture> pictures = LitePal.findAll(Picture.class);
        for(Picture picture:pictures){
            ocrShow.add(new OcrResult(picture.getOcrResult(),picture.getPath()));
        }

        OcrAdapter adapter = new OcrAdapter(ResultActivity.this,R.layout.image_item,ocrShow);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }
}

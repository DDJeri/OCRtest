package com.example.ddd.ocrtest.service.picture;

public interface pictureOcrListener {

    void onProgress(Integer... params);

    void onSuccess();

    void onCancel();

}

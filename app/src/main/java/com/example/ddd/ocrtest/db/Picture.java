package com.example.ddd.ocrtest.db;

import org.litepal.crud.LitePalSupport;

public class Picture extends LitePalSupport {

    private String imageId;
    private String path;
    private String ocrResult;
    private boolean hasWord;
    private boolean hasWrite;

    public boolean isHasWrite() {
        return hasWrite;
    }

    public void setHasWrite(boolean hasWrite) {
        this.hasWrite = hasWrite;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOcrResult() {
        return ocrResult;
    }

    public void setOcrResult(String ocrResult) {
        this.ocrResult = ocrResult;
    }

    public boolean isHasWord() {
        return hasWord;
    }

    public void setHasWord(boolean hasWord) {
        this.hasWord = hasWord;
    }
}

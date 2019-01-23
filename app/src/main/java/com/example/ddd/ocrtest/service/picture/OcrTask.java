package com.example.ddd.ocrtest.service.picture;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.ddd.ocrtest.db.Picture;
import com.example.ddd.ocrtest.util.Base64Util;
import com.example.ddd.ocrtest.util.FileUtil;
import com.example.ddd.ocrtest.util.jsonUtil;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class OcrTask extends AsyncTask<Void, Integer, Integer> {

    private pictureOcrListener listener;
    private int progressNum;
    private Context context;
    public static final int TYPE_PAUSED = -2;
    public static final int TYPE_CANCELED = -3;

    private boolean isCanceled = false;
    private boolean isPaused = false;

    public OcrTask(Context context, pictureOcrListener listener){
        this.context = context;
        this.listener = listener;
        /* 同步数据库 */
        syncDatabase();
    }

    @Override
    protected  void onPreExecute(){

    }

    @Override
    protected Integer doInBackground(Void... params) {

        int num = 0;
        /* 读取数据库 */
        List<Picture> pictures = LitePal.findAll(Picture.class);
        progressNum = pictures.size();
        String access_token = "24.4e6322ae0571f09b024edcb33b8dfa21.2592000.1550740532.282335-15468864";

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        /* 做ocr */
        for(Picture picture: pictures){
            if(isCanceled){
                LitePal.deleteAll(Picture.class);
                return TYPE_CANCELED;
            }
            publishProgress(num);
            Bitmap bitmap = null;
            if(picture.isHasWrite() == false){         //图片已经不存在
                picture.delete();
                continue;
            }else{

                if(picture.getOcrResult() == null){
                    try{
                        String filePath = picture.getPath();

                        URL url = new URL("https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic"+"?access_token="+access_token);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                        byte[] imgData = Imgcompress(filePath);
                        if(imgData == null){
                            Log.e("imgsize","出错");
                            continue;
                        }
                        String imgStr = Base64Util.encode(imgData);
                        String param = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imgStr, "UTF-8");
                        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                        out.writeBytes(param);

                        InputStream in = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while((line = reader.readLine()) != null){
                            response.append(line);
                        }

                        String result = jsonUtil.getResults(response.toString());
                        picture.setOcrResult(result);
                        picture.setHasWrite(false);
                        picture.save();

                        Log.e("imgsize",  result+ "\n" + filePath);

                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if(reader != null){
                            try{
                                reader.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                            if(connection != null){
                                connection.disconnect();
                            }
                        }
                    }
                }
            }
            num++;
        }
        return num;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        listener.onProgress(progress,progressNum);
    }

    @Override
    protected void onPostExecute(Integer status) {
        if(status == TYPE_CANCELED){
            listener.onCancel();
        }else{
            listener.onSuccess();
        }
    }

    private byte[] Imgcompress(String filePath){
        File file = new File(filePath);
        Log.e("imgsize","原来图片大小,单位k" + file.length()/1024);
        if(file.length()/1024 > 300){
            Bitmap image = GetBitmap(filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 15, baos);
            Log.e("imgsize","压缩后图片大小,单位k " + baos.toByteArray().length/1024 +" "+filePath);
            return baos.toByteArray();
        }
        else{
            try{
                Log.e("imgsize","原来图片大小,不需要压缩,单位k" + file.length()/1024);
                return FileUtil.readFileByBytes(filePath);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }


    public void cancelOcr(){
        isCanceled = true;
    }

    private Bitmap GetBitmap(String path){
        FileInputStream in = null;
        Bitmap bm,bitmap;
        try {
            in = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File(path);

        if(file.exists()) {
            bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        }
        return null;
    }

    private void syncDatabase(){
        String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME};
        //asc 按升序排列
        //    desc 按降序排列
        //projection 是定义返回的数据，selection 通常的sql 语句，例如  selection=MediaStore.Images.ImageColumns.MIME_TYPE+"=? " 那么 selectionArgs=new String[]{"jpg"};
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns.DATE_MODIFIED + "  desc");
        String imageId;
        String fileName;
        String filePath;

        while (cursor.moveToNext()) {
            imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
            fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
            filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));

            List<Picture> pictures = LitePal.where("path=?",filePath).find(Picture.class);
            if(pictures.isEmpty()){
                Picture picture = new Picture();
                picture.setImageId(imageId);
                picture.setPath(filePath);
                picture.setHasWrite(true);
                picture.save();
                Log.v("photos4wwwwww", imageId + " -- " + fileName + " -- " + filePath);
            }else{
                Picture picture = pictures.get(0);
                picture.setHasWrite(true);
                picture.save();
                Log.v("photos4ttttttt", imageId + " -- " + fileName + " -- " + filePath);
            }
        }
        cursor.close();
    }
}

package com.example.ddd.ocrtest.service.message;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.ddd.ocrtest.db.Message;

import org.litepal.LitePal;

import java.util.List;

public class MessageDetection extends Service {
    public MessageDetection() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        LitePal.deleteAll(Message.class);
        syncDatabase();
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        stopSelf();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        Toast.makeText(MessageDetection.this, "Messages get successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void syncDatabase() {
        Uri uri = Uri.parse("content://sms/");
        String[] projection = new String[]{"_id", "address", "person",
                "body", "date", "type",};
        Cursor cur = getContentResolver().query(uri, projection, null,
                null, "date desc"); // 获取手机内部短信

        String strAddress;
        int intPerson;
        String strbody;
        int intType;

        while (cur.moveToNext()) {

            strAddress = cur.getString(cur.getColumnIndex("address"));
            intPerson = cur.getInt(cur.getColumnIndex("person"));
            strbody = cur.getString(cur.getColumnIndex("body"));
            intType = cur.getInt(cur.getColumnIndex("type"));

            List<Message> messages = LitePal.where("body=?", strbody).find(Message.class);
            if (messages.isEmpty()) {
                Message message = new Message();
                message.setAddress(strAddress);
                message.setBody(strbody);
                message.setPerson(intPerson);
                message.setType(intType);
                message.save();
                Log.e("messages", strAddress + " -- " + intPerson + " -- " + strbody + " -- " + intType);
            }
        }
        cur.close();
    }
}

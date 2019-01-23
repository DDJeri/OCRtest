package com.example.ddd.ocrtest.service.Appinformation;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.ddd.ocrtest.db.AppInfo;

import org.litepal.LitePal;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

public class AppInformation extends Service {
    public AppInformation() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        LitePal.deleteAll(AppInfo.class);
        syncDatabase();
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        stopSelf();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        Toast.makeText(AppInformation.this, "publickey get successfully", Toast.LENGTH_SHORT).show();
    }

    private void syncDatabase(){

        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_SIGNATURES | PackageManager.GET_ACTIVITIES | PackageManager.GET_PERMISSIONS);
        for(int i=0;i<packages.size();i++) {
            PackageInfo packageInfo = packages.get(i);
            if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0) {

                Signature[] arrayOfSignature = packageInfo.signatures;
                byte[] sign = arrayOfSignature[0].toByteArray();
                try {
                    CertificateFactory certFactory = CertificateFactory
                            .getInstance("X.509");
                    X509Certificate cert = (X509Certificate) certFactory
                            .generateCertificate(new ByteArrayInputStream(sign));
                    String publickey = cert.getPublicKey().toString();
                    Log.d("TRACK", publickey);
                    publickey = publickey.substring(publickey.indexOf("modulus=") + 8,
                            publickey.indexOf(","));
                    Log.d("TRACK", publickey+" :"+publickey.length());

                    String[] permissions = packageInfo.requestedPermissions;
                    String a = "";
                    if(permissions != null){
                        for(String permission : permissions)
                            a += permission + "\n";
                    }

                    List<AppInfo> apps = LitePal.where("packageName=?",packageInfo.packageName).find(AppInfo.class);
                    if(apps.isEmpty()){
                        AppInfo appinfo = new AppInfo();
                        appinfo.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                        appinfo.setPackageName(packageInfo.packageName);
                        appinfo.setPublickey(publickey);
                        if(!a.isEmpty()){appinfo.setPermissions(a);}
                        appinfo.save();
                    }

                } catch (CertificateException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

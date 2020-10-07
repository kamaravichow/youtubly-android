package com.aravi.tunecatcher;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MediaCaptureService extends Service {

    public MediaCaptureService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

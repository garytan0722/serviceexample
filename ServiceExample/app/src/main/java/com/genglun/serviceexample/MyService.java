package com.genglun.serviceexample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

public class MyService extends Service {
    final static String TAG="MyTag";

    private final LocalBinder mBinder=new LocalBinder();

   public class LocalBinder extends Binder {
       MyService getservice(){
           return MyService.this;
       }
   }

    public  int getRandomNumber(){
        Random random=new Random();
        return random.nextInt(100);
    }

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "MySerive onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "MySerive onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "MySerive onStartCommand");

//        HandlerThread thread=new HandlerThread("HandlerThread");
//        thread.start();
//        Handler handler =new Handler(thread.getLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG,"Run my task");
//                //do something
//
//                Log.d(TAG,"MyService stopSelf");
//                stopSelf();
//            }
//        });
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MySerive onDestory");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"MySerive onCreate");
    }
}

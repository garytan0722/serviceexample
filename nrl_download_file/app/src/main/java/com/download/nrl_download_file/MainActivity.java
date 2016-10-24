package com.download.nrl_download_file;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.download.nrl_download_file.android_gcm.RegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Button download;
    public static String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        download=(Button)findViewById(R.id.dowload);
        download.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        Log.d(TAG,"get extra");
        if(extras!=null){
            Log.d(TAG,"extra is not null");
            String title=extras.getString("title");
            String content=extras.getString("content");
            if(title!=null&&content!=null){
                alert(title,content);
            }
        }
//        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
//        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
//            @Override
//            public void onTabSelected(@IdRes int tabId) {
//                if (tabId == R.id.tab_favorites) {
//                    // The tab with id R.id.tab_favorites was selected,
//                    // change your content accordingly.
//                }
//            }
//        });
        if (checkPlayServices()) {
             //Start IntentService to register this application with GCM.
            Log.d(TAG,"go GCM");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }
    public void alert(String title,String contnet){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(contnet);
        dialog.setIcon(R.drawable.ic_stat_ic_notification);
        dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub

            }

        });
        dialog.show();
    }



    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dowload:
                Process p;
                String link;
                String cmd ="mount -o rw,remount rootfs / \n";
                String cmd2="mount -o rw,remount /data \n";
                File tmp=new File("/amds/");
                if(!tmp.exists()){
                    Log.d(TAG,"not exist");
                    link="ln -s /data/data/com.download.nrl_download_file amds";
                }else{
                    link=" ";
                }
                try {
                    // Preform su to get root privledges
                    p = Runtime.getRuntime().exec("su");
                    DataOutputStream dos = new DataOutputStream(p.getOutputStream());
                    dos.writeBytes(cmd);
                    dos.flush();
                    dos.writeBytes(cmd2);
                    dos.flush();
                    dos.writeBytes(link);
                    dos.flush();
                    dos.close();
//                    BufferedReader reader = new BufferedReader(
//                            new InputStreamReader(p.getInputStream()));
//                    char[] buffer = new char[4096];
//                    int read;
//                    StringBuffer output = new StringBuffer();
//                    while ((read = reader.read(buffer)) > 0) {
//                        output.append(buffer, 0, read);
//                    }
//                    reader.close();
                    try {
                        p.waitFor();
                        if (p.exitValue() != 255) {
                            // TODO Code to run on success
                            Toast.makeText(MainActivity.this,"root",Toast.LENGTH_LONG).show();
                        }
                        else {
                            // TODO Code to run on unsuccessful
                            Toast.makeText(MainActivity.this,"run on unsuccessful",Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException e) {
                        // TODO Code to run in interrupted exception
                        Toast.makeText(MainActivity.this,"run in interrupted exception",Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    // TODO Code to run in input/output exception
                    Toast.makeText(MainActivity.this,"input/output exception",Toast.LENGTH_LONG).show();
                }
         //---------------------------check files have been download
                File monitor_file=new File("/amds/monitor.bin");
                File post_file=new File("/amds/post.bin");
                if(!monitor_file.exists()&&!post_file.exists()){
                    Log.d(TAG,"monitor_file and post_file not exist");
                    final DownloadMonitor downloadmonitor = new DownloadMonitor(MainActivity.this);
                    downloadmonitor.execute();
                }else{
                    try{
                        Log.d(TAG,"monitor.bin post.bin exist");
                        Process process = Runtime.getRuntime().exec("su");
                        String cd ="cd /amds/\n";
                        String chmod ="chmod 777 monitor.bin \n";
                        String cmd3 ="./monitor.bin\n";
                        //String cmd4="exit\n";
                        DataOutputStream dos = new DataOutputStream(process.getOutputStream());
                        dos.writeBytes(cd);
                        dos.writeBytes(chmod);
                        dos.writeBytes(cmd3);
                        //dos.writeBytes(cmd4);
                        dos.flush();
                        dos.close();
                        Log.d(TAG,"123");
//                        BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(process.getInputStream()));
//                        char[] buffer = new char[4096];
//                        int read;
//                        StringBuffer output = new StringBuffer();
//                        while ((read = reader.read(buffer)) > 0) {
//                            output.append(buffer, 0, read);
//                        }
//                        reader.close();
                       // Log.d(TAG,"Execute:"+output.toString());
                        process.destroy();
                    }catch (IOException e){
                        Toast.makeText(MainActivity.this,"input/output exception",Toast.LENGTH_LONG).show();
                    }

                }

                break;
        }
    }

    private class DownloadMonitor extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;
        public DownloadMonitor(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpsURLConnection connection = null;
            try {
                URL url = new URL("https://120.125.85.4/amds/monitor.bin");
                InputStream instream = getResources().openRawResource(R.raw.server2);
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                try {
                    trustStore.load(instream, null);
                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "NoSuchAlgorithmException");
                    e.printStackTrace();
                } catch (CertificateException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "CertificateException");
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "IOException");
                    e.printStackTrace();
                } finally {
                    instream.close();
                }
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(trustStore);
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(context.getSocketFactory());
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                String filetype=connection.getContentType();
                Log.d("MainActivity","File type:"+filetype);
                Log.d("MainActivity","File size:"+fileLength);
                //File data_path = Environment.getExternalStorageDirectory();
                String data_path="/amds/";
                input = connection.getInputStream();
                Log.d("MainActivity","InputStream::"+input);
                Log.d("TAG","PATH:"+data_path);
                output = new FileOutputStream(data_path+"monitor.bin");
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count=input.read(data)) !=-1) {
                    Log.d("MainActivity","Monitor file reading");
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        //publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                Log.d("MainActivity","done while");
            } catch (Exception e) {
                Log.d("MainActivity",e.toString());
                return e.toString();
            } finally {
                Log.d("MainActivity","finally");
            }

            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                Toast.makeText(context, "monitor.bin download error: " + result, Toast.LENGTH_LONG).show();
                Log.d(TAG,"Eorror:"+result);
            }
            else{

                final DownloadPost downloadpost=new DownloadPost(MainActivity.this);
                downloadpost.execute();


            }
        }
    }
    //----------------------download post.bin
    private class DownloadPost extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;
        public DownloadPost(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpsURLConnection connection = null;
            try {
                URL url = new URL("https://120.125.85.4/amds/post.bin");
                InputStream instream = getResources().openRawResource(R.raw.server2);
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                try {
                    trustStore.load(instream, null);
                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "NoSuchAlgorithmException");
                    e.printStackTrace();
                } catch (CertificateException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "CertificateException");
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "IOException");
                    e.printStackTrace();
                } finally {
                    instream.close();
                }
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(trustStore);
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(context.getSocketFactory());
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                String filetype=connection.getContentType();
                Log.d("MainActivity","File type:"+filetype);
                Log.d("MainActivity","File size:"+fileLength);
                //File data_path = Environment.getExternalStorageDirectory();
                String data_path="/amds/";
                input = connection.getInputStream();
                Log.d("MainActivity","InputStream::"+input);
                Log.d("TAG","PATH:"+data_path);
                output = new FileOutputStream(data_path+"post.bin");
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count=input.read(data)) !=-1) {
                    Log.d("MainActivity","Post file reading");
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        //publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                Log.d("MainActivity","done while");
            } catch (Exception e) {
                Log.d("MainActivity",e.toString());
                return e.toString();
            } finally {
                Log.d("MainActivity","finally");
            }

            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            //post_ProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                Toast.makeText(context, "Post.bin download error: " + result, Toast.LENGTH_LONG).show();
                Log.d(TAG,"Eorror:"+result);
            }
            else{
                Toast.makeText(context, "File downloaded comlpete", Toast.LENGTH_SHORT).show();
                try {
                    Process p = Runtime.getRuntime().exec("su");
                    String cmd ="cd /amds/\n";
                    String cmd2 ="chmod 777 monitor.bin && chmod 777 post.bin\n";
                    String cmd3 ="/amds/monitor.bin\n";
                    //String cmd4="exit\n";
                    DataOutputStream dos = new DataOutputStream(p.getOutputStream());
                    dos.writeBytes(cmd);
                    dos.writeBytes(cmd2);
                    dos.writeBytes(cmd3);
                    //dos.writeBytes(cmd4);
                    dos.flush();
                    dos.close();
//                    BufferedReader reader = new BufferedReader(
//                            new InputStreamReader(p.getInputStream()));
//                    char[] buffer = new char[4096];
//                    int read;
//                    StringBuffer output = new StringBuffer();
//                    while ((read = reader.read(buffer)) > 0) {
//                        output.append(buffer, 0, read);
//                    }
//                    reader.close();
//                    try {
//                        p.waitFor();
//                        //Log.d(TAG,"Execute:"+output.toString());
//                        if (p.exitValue() != 255) {
//                            // TODO Code to run on success
//                            Toast.makeText(MainActivity.this,"root!!!!",Toast.LENGTH_LONG).show();
//                            //p.destroy();
//                        }
//                        else {
//                            // TODO Code to run on unsuccessful
//                            Toast.makeText(MainActivity.this,"run on unsuccessful",Toast.LENGTH_LONG).show();
//                        }
//                    } catch (InterruptedException e) {
//                        // TODO Code to run in interrupted exception
//                        Toast.makeText(MainActivity.this,"run in interrupted exception",Toast.LENGTH_LONG).show();
//                    }
                } catch (IOException e) {
                    // TODO Code to run in input/output exception
                    Toast.makeText(MainActivity.this,"input/output exception",Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}

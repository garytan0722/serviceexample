package com.example.nrl.appname.adapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.example.nrl.appname.R;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PackageAdapter  extends BaseAdapter {

	private List<PackageItem> data;
    private Context context;
    private String res;
    public static String TAG="PackageAdapter";
    public static String dir;
    
    public PackageAdapter(Context context, List<PackageItem> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public PackageItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PackageItem item = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
          convertView = View.inflate(context,R.layout.package_row, null);
            holder = new ViewHolder();
            holder.ivImage = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_name);
           
          convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

       

        holder.ivImage.setImageDrawable(item.getIcon());
        holder.tvTitle.setText(item.getName());
        return convertView;
    }

    static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
       
       
    }

    
    
	
	

 

}

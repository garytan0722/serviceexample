package com.example.nrl.appname;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;































import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.example.nrl.appname.adapter.PackageAdapter;
import com.example.nrl.appname.adapter.PackageItem;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;



public class MainActivity extends FragmentActivity {
	private String total;
	private static String TAG="MainActivity";
	public String dir;
	private String res;
	private List<PackageItem> data;
	private ProgressDialog progressDialog;
	private PackageAdapter adapter;
	private ProgressDialog postDialog;
   
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = new ArrayList<PackageItem>();
        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);
        adapter = new PackageAdapter(this, data);
        listView.setAdapter(adapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem postItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				// set item width
				postItem.setWidth(convertDpToPixel(90));
				// set item title
				postItem.setIcon(R.drawable.upload);
				// set item title fontsize
				postItem.setTitleSize(18);
				// set item title font color
				postItem.setBackground(new ColorDrawable(Color.parseColor("#009EE0")));
				// add to menu
				
				menu.addMenuItem(postItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(convertDpToPixel(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		listView.setMenuCreator(creator);
		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				PackageItem item = data.get(position);
				
				switch (index) {
				case 0:
					// post
					SwipeMenuItem postitem=menu.getMenuItems().get(0);
					postitem.setBackground(new ColorDrawable(Color.parseColor("#005E86")));
					dir=item.getdir();
					new postfile().execute();
					break;
				case 1:
					// delete
//					delete(item);
					data.remove(position);
					adapter.notifyDataSetChanged();
					break;
				}
				return false;
			}
		});
        new ListAppTask().execute();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        }
    
   
    public int convertDpToPixel(float dp) {
    	Log.d(TAG,"convertDpToPixel");
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
    
   public class ListAppTask extends AsyncTask<Void, Void, List<PackageItem>> {
		protected List<PackageItem> doInBackground(Void... args) {
		Log.d(TAG, "AsyncTask");
        PackageManager appInfo = getPackageManager();
        List<ApplicationInfo> listInfo = appInfo.getInstalledApplications(0);
        Collections.sort(listInfo, new ApplicationInfo.DisplayNameComparator(appInfo));
        List<PackageItem> data = new ArrayList<PackageItem>();
        for (int index = 0; index < listInfo.size(); index++) {
            try {
                ApplicationInfo content = listInfo.get(index);
                if ((content.sourceDir.startsWith("/data/app/")) && content.enabled) {
                    if (content.icon != 0) {
                        PackageItem item = new PackageItem();
                        item.setName(getPackageManager().getApplicationLabel(content).toString());
                        item.setPackageName(content.packageName);
                        item.setdir(content.sourceDir);
                        item.setIcon(getPackageManager().getDrawable(content.packageName, content.icon, content));
                        data.add(item);
                    }
                }
            } catch (Exception e) {

            }
        }

        return data;
    }

    protected void onPostExecute(List<PackageItem> result) {
        data.clear();
        data.addAll(result);
        adapter.notifyDataSetChanged();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        
       
    }
}
    
  
   class postfile extends AsyncTask<Integer, Integer, String> {
   	
   	
   	@Override
   	protected String doInBackground(Integer... countTo) {
   		// TODO Auto-generated method stub
   		

   		try {
   			 
   			Log.d(TAG,"Try Post file");
   			
   			postfile_function(dir);
   			
   		

   		}

   		catch (KeyManagementException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		} catch (KeyStoreException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		} catch (NoSuchAlgorithmException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		} catch (UnrecoverableKeyException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		} catch (IOException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   			
   		} finally {

   		}
   		return "";
   	}

   	@Override
   	protected void onPreExecute() {
   		// TODO Auto-generated method stub
   		super.onPreExecute();
   		Log.d(TAG," before post");
   		postDialog = new ProgressDialog(MainActivity.this);
   		postDialog.setMessage("loading");
   		postDialog.setCancelable(false);
   		postDialog.show();
   	}

   	@Override
   	protected void onProgressUpdate(Integer... values) {
   		// TODO Auto-generated method stub
   		super.onProgressUpdate(values);
   		
   		Log.d(TAG," posting");
   	}

   	@Override
   	protected void onPostExecute(String result) {
   		// TODO Auto-generated method stub	
   		super.onPostExecute(result);
   		
   		Log.d("Appname","Return::"+res);
   	 if (postDialog != null) {
   		postDialog.dismiss();
   		postDialog = null;
     }
   		
   		
   	}

   	@Override
   	protected void onCancelled() {
   		// TODO Auto-generated method stub
   		super.onCancelled();
   		
   	}
   	public String postfile_function(String path)
   			throws KeyStoreException, IOException, KeyManagementException,
   			NoSuchAlgorithmException, UnrecoverableKeyException {
   		
   		Log.d(TAG,"Fuction......");
   		String url = "https://nrl.cce.mcu.edu.tw/pgi/uploadfile.php";
   		InputStream instream =getResources().openRawResource(R.raw.syl);
   		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());//

   		try {
   			trustStore.load(instream, null);
   		} catch (NoSuchAlgorithmException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		} catch (CertificateException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		} catch (IOException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		} finally {
   			instream.close();
   		}

   		SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
   		Scheme sch = new Scheme("https", socketFactory, 443);
   		File file = new File(dir);
   		MultipartEntity mpEntity = new MultipartEntity();
   		 ContentBody cbFile = new FileBody(file, "image/jpeg");
   		 StringBody username = new StringBody("username");
   		 mpEntity.addPart("userfile", cbFile);
   		 mpEntity.addPart("username", username);
   		 HttpPost request = new HttpPost(url);
   	    request.setEntity(mpEntity);
   	    HttpClient client = new DefaultHttpClient();
   		client.getConnectionManager().getSchemeRegistry().register(sch);
   	    HttpResponse response = client.execute(request);
   	    res = EntityUtils.toString(response.getEntity());
   	    return res;
   	}

   }
   private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
   
}

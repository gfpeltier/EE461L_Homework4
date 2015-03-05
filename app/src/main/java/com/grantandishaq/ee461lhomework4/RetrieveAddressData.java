package com.grantandishaq.ee461lhomework4;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/**
 * Created by Grant Peltier on 3/4/15.
 */
public class RetrieveAddressData extends AsyncTask<String, Void, String> {


    private OnTaskComplete onTaskComplete;

    @Override
    protected String doInBackground(String... params) {
        URL url;
        HttpURLConnection conn = null;
        String baseUrl = params[0];
        String address = params[1];
        InputStream output = null;
        String apiKey = params[2];
        String charset = "UTF-8";
        try{
            address = URLEncoder.encode(address, "UTF-8");
            address.replaceAll("%2C", ",");
            url = new URL(baseUrl + address + "&sensor=false");
            Log.v("Query", url.toString());
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("Accept-Charset", charset);
            //byte[] out = new byte[1024];
            output = new BufferedInputStream(conn.getInputStream());
            Log.v("Message",conn.getResponseMessage());
            Log.v("Code", ""+conn.getResponseCode());
            int amt = output.read();
            Log.v("NUMBYTESREAD", ""+amt);
            byte[] xmlOut = new byte[100000];
            output.read(xmlOut);
            String oString = new String(xmlOut, "UTF-8");
            //Log.v("XML",oString);
            //output.close();
            return oString;
        }catch (Exception e){
            e.printStackTrace();
            Log.e("ERROR", ""+e.getMessage());
            Log.e("ERROR", ""+e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String xml){
        onTaskComplete.setMyTaskComplete(xml);
    }

    public interface OnTaskComplete {
        public void setMyTaskComplete(String message);
    }

    public void setMyTaskCompleteListener(OnTaskComplete onTaskComplete) {
        this.onTaskComplete = onTaskComplete;
    }
}

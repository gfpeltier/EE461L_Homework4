package com.grantandishaq.ee461lhomework4;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.ProgressDialog;
import android.app.Activity;

/**
 * Created by grantpeltier93 on 3/6/15.
 */
public class RetrievePlacesData extends AsyncTask<String, Void, String> {

    private OnTaskComplete onTaskComplete;
    private ProgressDialog progDailog;
    private Context context;


    public RetrievePlacesData(Context ctx){
        context = ctx;
        progDailog = new ProgressDialog(ctx);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDailog.setMessage("Loading...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
    }


    @Override
    protected String doInBackground(String... params) {
        URL url;
        HttpURLConnection conn = null;
        BufferedReader read = null;
        String baseUrl = params[0];
        //String address = params[1];
        InputStream output = null;
        StringBuilder builder = new StringBuilder();
        //String apiKey = params[2];
        String charset = "UTF-8";
        try{
            //address = URLEncoder.encode(address, "UTF-8");
            //address.replaceAll("%2C", ",");
            url = new URL(baseUrl);
            Log.v("Query", url.toString());
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15*1000);
            conn.connect();
            //conn.setRequestProperty("Accept-Charset", charset);
            //byte[] out = new byte[1024];
            //output = conn.getInputStream();
            read = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String ln = null;
            while((ln = read.readLine()) != null){
                builder.append(ln);
            }
            Log.v("Message",conn.getResponseMessage());
            Log.v("Code", ""+conn.getResponseCode());
            return builder.toString();
            //int amt = output.read();
            //Log.v("NUMBYTESREAD", ""+amt);
            //byte[] xmlOut = new byte[100000];
            //output.read(xmlOut);
            //String oString = new String(xmlOut, "UTF-8");
            //Log.v("XML",oString);
            //output.close();
            //return oString;
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
        progDailog.dismiss();
    }

    public interface OnTaskComplete {
        public void setMyTaskComplete(String message);
    }

    public void setMyTaskCompleteListener(OnTaskComplete onTaskComplete) {
        this.onTaskComplete = onTaskComplete;
    }
}

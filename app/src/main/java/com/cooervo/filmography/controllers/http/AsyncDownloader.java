package com.cooervo.filmography.controllers.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import okhttp3.Call;
//import com.squareup.okhttp.OkHttpClient;

import okhttp3.Request;
import okhttp3.Response;

import okhttp3.OkHttpClient;
//import com.squareup.okhttp3.OkHttpClient;

//import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Public class to be re used for downloading JSON response using library
 * OkHttp.
 *
 * We extend AsyncTask because so we can make an Asynchronous call to retrieve the
 * response from theMovieDB.org API without affecting the GUI.
 *
 * Also this class is reused in the application to reduce boiler plate code. If needed we can subclass
 * this class and override method onPostExecute() which can be used to do specific things in the
 * main thread (GUI).
 */
public class AsyncDownloader extends AsyncTask<String, Integer, String> {

  //  OkHttpClient client = new OkHttpClient();

    public static final String TAG = AsyncDownloader.class.getSimpleName();

    private Context context;
    private Class classToLoad;
    private ProgressDialog dialog;

    private String url;

    public AsyncDownloader(Context ctx, Class c) {
        context = ctx;
        classToLoad = c;
    }

    /**
     * onPreExecute runs on the UI thread before doInBackground.
     * This will start showing a small dialog that says Loading with a spinner
     * to let the user know download is in progress
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.setProgressStyle(dialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * doInBackground() runs in the background on a worker thread. This is where code that can block the GUI should go.
     *  Since we are using asynctask this is already in background threas we use okHttp method
     *  call.execute() which executes in current thread (which is the background threas of this Async class)
     *  Once we finish retrieving jsonData it is passed to method onPostExecute()
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(String... params) {

        String url = params[0];



        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();


        Call call = client.newCall(request);

        Response response = null;

        String jsonData = null;

        try {
            response = call.execute();

            if (response.isSuccessful()) {
                jsonData = response.body().string();

            } else {
                jsonData = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData; //This is returned to onPostExecute()
    }

    /**
     * onPostExecute runs on the  main (GUI) thread and receives
     * the result of doInBackground.
     *
     * Here we pass a string representation of jsonData to the child/receiver
     * activity.
     *
     * @param jsonData
     */
    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);
        dialog.dismiss();

        Intent i = new Intent(context, classToLoad);
        i.putExtra("jsonData", jsonData);
        context.startActivity(i);
    }
}
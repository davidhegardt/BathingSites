package se.miun.dahe1501.bathingsites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
*   Class used to display a WebView of sites to download to the database.
*   Uses a download-listener and custom WebClient to download sites.
*   Also uses AsyncTask to download data in separate thread.
 */
public class DownloadSitesActivity extends AppCompatActivity {

    private WebView webView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_sites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());           // Retrieve preferences
        //editor = preferences.edit();
        //editor.putString("Default_download",getResources().getString(R.string.Default_download));
        //editor.apply();


        webView = (WebView) findViewById(R.id.downloadWebView);             // Find the webview to use
        webView.setWebViewClient(new MyWebViewClient());                    // Sets the Webclient to custom client . used to override downloadlistener and start bg thread
        webView.loadUrl(preferences.getString("koordinat_url",""));         // Retrieve preferences - the URL to connect to
        //webView.setDownloadListener((DownloadListener) this);




    }

    /* Inner class used to override URL loading so sites can be downloaded, launches new Thread for download */
    private class MyWebViewClient extends WebViewClient implements DownloadListener {

        //Context ctx;
        private ProgressDialog mProgressDialog;
        private File fileInput;

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

            new DownloadFile().execute(url);            // Call async task with the URL that user clicks on
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            view.setDownloadListener(this);                             // Sets downloadListener for links that user can click on
            //return super.shouldOverrideUrlLoading(view, request);
            return false;
        }

        /* Inner class to handle downloading of files and call function to parse data */
        private class DownloadFile extends AsyncTask<String, Integer, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mProgressDialog = new ProgressDialog(DownloadSitesActivity.this);           // Specify the progressdialog to be displayed during download
                mProgressDialog.setTitle(getResources().getString(R.string.downlading_site_text));
                mProgressDialog.setMessage(getResources().getString(R.string.download_text));
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);          // Sets bar so percentage can be used
                mProgressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                URL url;

                String fullpath = params[0];                                            // Path to CSV file as argument
                String fileName = fullpath.substring(fullpath.indexOf("koordinater-utf8/") +17,fullpath.indexOf("csv") + 3);        // parse the filename of CSV file

                fileInput = new File(getExternalCacheDir(),fileName);                       // Create file based on csv-filename

                try {
                    url = new URL(params[0]);                                           // Create new URL from input link
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();            // Open connection to the link specified

                    long fileSize = connection.getContentLength();                      // Get the total filesize
                    long totalDataRead = 0;                                             // Current read data

                    BufferedInputStream in = new BufferedInputStream(connection.getInputStream());          // Open BufferedInputstream to read data from connection
                    FileOutputStream fos = new FileOutputStream(fileInput);                                 // Open fileinpustream to save data to file

                    BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);                        // Setup bufferedOutputstream with default byte size
                    byte[] data = new byte[1024];                                                           // Create new array of bytes
                    int i;

                    while ((i = in.read(data, 0, 1024)) >= 0) {                                     // Loop read the data
                        totalDataRead = totalDataRead + i;                              // Increment counter
                        bout.write(data, 0, i);                                         // Write the data to the bufferedoutputstream
                        long tmpPercent = (totalDataRead * 100) / fileSize;             // Calculate percentage of current download progress
                        int percent = (int) tmpPercent;
                        if (percent > 100) {                                            // If percent is invalid
                            percent = 99;
                        }
                        publishProgress(percent);                                       // Call to update progress bar of percentage progress
                    }

                    bout.close();

                    CSVparser parser = new CSVparser(fileInput.getAbsolutePath(),DownloadSitesActivity.this);       // Call parser-class to parse and save data to database from csv file
                    parser.ParseData();                                             // Call function to parse the CSV file

                } catch (Exception e) {
                    e.printStackTrace();
                }


                return fileInput.getAbsolutePath();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                mProgressDialog.setProgress(values[0]);                     // Update progressDialog
            }

            @Override
            protected void onPostExecute(String s) {

                mProgressDialog.dismiss();                                  // Once file is downloaded, stop showing progress
                fileInput.delete();                                         // Delete the file stored in cache

                super.onPostExecute(s);
            }
        }
    }

}

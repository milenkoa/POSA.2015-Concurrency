package com.mooc.imageviewer;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.app.FragmentManager;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;
import android.widget.ProgressBar;

public class DownloadActivity extends LifeCycleLoggingActivity {
    private final String TAG = getClass().getSimpleName();

    private FragmentManager mFragmentManager;
    private DownloaderFragment mDownloaderFragment;

    private ProgressBar mLoadingProgressBar;

    private static final String TAG_DOWNLOADER_FRAGMENT = "downloader_fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_activity);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.progressbar);


        mFragmentManager = getFragmentManager();
        mDownloaderFragment = (DownloaderFragment) mFragmentManager.findFragmentByTag(TAG_DOWNLOADER_FRAGMENT);

        if (mDownloaderFragment == null) {
            Log.d(TAG, "first time in onCreate(): create DownloaderFragment");
            //this is first time: it means this is not on configuration change
            // retrieve URL from Intent
            Uri uriToDownload = getIntent().getData();
            // create a new fragment and commit it - this fragment will be retained fragment
            mDownloaderFragment = new DownloaderFragment();
            mFragmentManager.beginTransaction().add(mDownloaderFragment, TAG_DOWNLOADER_FRAGMENT).commit();
            mDownloaderFragment.put(DownloaderFragment.URL, uriToDownload);
        }
        else {
            Log.d(TAG, "not a first time in onCreate()");
            //this is not first time: there is/there was AsyncTask started
            // check if it has finished or wait for it to finish
            Uri resultImagePath = mDownloaderFragment.get(DownloaderFragment.IMAGE);
            if (resultImagePath != null) {
                Log.d(TAG, "download already finished");
                //background AsyncTask is finished
                // finish this activity
                Intent resultIntent = new Intent();
                resultIntent.setData(resultImagePath);

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLoadingProgressBar.setVisibility(View.VISIBLE);

        DownloadTask downloadTask = mDownloaderFragment.get(DownloaderFragment.ASYNCTASK);

        if (downloadTask == null) {
            //create new instance of DownloadTask
            Log.d(TAG, "creating new download task");
            Uri uriToDownload = mDownloaderFragment.get(DownloaderFragment.URL);
            downloadTask = new DownloadTask();
            mDownloaderFragment.put(DownloaderFragment.ASYNCTASK, downloadTask);
            downloadTask.execute(uriToDownload);
        }
        else {
            Log.d(TAG, "download task already running");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
    }

    public class DownloadTask extends AsyncTask<Uri, Void, Uri> {
        private static final String TAG = "DownloadTask";

        @Override
        protected Uri doInBackground(Uri... arg0) {
            // TODO Auto-generated method stub
            Uri inputUrl = arg0[0];
            Log.d(TAG, "downloading image from: " + inputUrl.toString());
            Uri downloadResult = Utils.downloadImage(DownloadActivity.this, inputUrl);
            if (downloadResult == null) {
                //something went wrong: external storage is not writable, invalid bitmap or
                // download failed
                return null;
            }
            Log.d(TAG, "image downloaded to: " + downloadResult.toString());
            Uri grayscaleResult = Utils.grayScaleFilter(DownloadActivity.this, downloadResult);
            if (grayscaleResult == null) {
                //something went wrong
                return null;
            }
            Log.d(TAG, "grayscale image stored at: " + grayscaleResult.toString());
            return grayscaleResult;
        }

        protected void onPostExecute(Uri result) {
            Log.d(TAG, "onPostExecute()");
            if (result != null) {
                mDownloaderFragment.put(DownloaderFragment.IMAGE, result);
                Intent resultIntent = new Intent();
                resultIntent.setData(result);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
            else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

}

package com.mooc.imageviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends LifeCycleLoggingActivity {
	
	private final String TAG = getClass().getSimpleName();
	
	private EditText mUrlEditText;

    private static final int DOWNLOAD_IMAGE_REQUEST = 1;
    private Uri mDefaultUrl =
            Uri.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        mUrlEditText = (EditText) this.findViewById(R.id.url);
    }
    
    
    /**
     * Called by the Android Activity framework when the user clicks
     * the "Find Address" button.
     *
     * @param view The view.
     */
    public void downloadImage(View view) {
        Log.d(TAG, "downloadImage");

        try {
            // Hide the keyboard.
            hideKeyboard(this,
                         mUrlEditText.getWindowToken());

            // Call the makeDownloadImageIntent() factory method to
            // create a new Intent to an Activity that can download an
            // image from the URL given by the user.  In this case
            // it's an Intent that's implemented by the
            // DownloadImageActivity.
            Uri url = getUrl();
            if (url != null) {
            	//mDownloaderFragment.initiateDownload(url);
                Intent downloadIntent = makeDownloadImageIntent(url);
                // Start the Activity associated with the Intent, which
                // will download the image and then return the Uri for the
                // downloaded image file via the onActivityResult() hook
                // method.
                startActivityForResult(downloadIntent, DOWNLOAD_IMAGE_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        // Check if the started Activity completed successfully.
        // code.
        if (resultCode == RESULT_OK) {
            // Check if the request code is what we're expecting.
            if (requestCode == DOWNLOAD_IMAGE_REQUEST) {
                // Call the makeGalleryIntent() factory method to
                // create an Intent that will launch the "Gallery" app
                // by passing in the path to the downloaded image
                // file.
                Uri resultUri = data.getData();
                String resultPath = resultUri.getPath();
                Intent galleryIntent = makeGalleryIntent(resultPath);

                // Start the Gallery Activity.
                startActivity(galleryIntent);
            }
        }
        // Check if the started Activity did not complete successfully
        // and inform the user a problem occurred when trying to
        // download contents at the given URL.
        else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Failed to download image", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Get the URL to download based on user input.
     */
    protected Uri getUrl() {

        Log.d(TAG, "getUrl() in");
        Uri url = null;

        // Get the text the user typed in the edit text (if anything).
        url = Uri.parse(mUrlEditText.getText().toString());

/*        Log.d(TAG, "getURL(): input URL is " + url.toString());

        if (!URLUtil.isValidUrl(url.toString())) {
            Toast.makeText(this,
                    "Invalid URL",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        else {
            String uri = url.toString();
            if (uri == null || uri.equals(""))
                url = mDefaultUrl;
            return url;
        }*/

        // If the user didn't provide a URL then use the default.
        String uri = url.toString();
        if (uri == null || uri.equals(""))
            url = mDefaultUrl;

        // Do a sanity check to ensure the URL is valid, popping up a
        // toast if the URL is invalid.
        Log.d(TAG, "getUrl(): " + url.toString());
        if (Patterns.WEB_URL.matcher(url.toString()).matches())
            return url;
        else {
            Toast.makeText(this,
                           "Invalid URL",
                           Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    
    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public void hideKeyboard(Activity activity,
                             IBinder windowToken) {
        InputMethodManager mgr =
            (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                                    0);
    }

    private Intent makeDownloadImageIntent(Uri url) {
        // Create an intent that will download the image from the web.
        Intent downloadImage = new Intent(this, DownloadActivity.class);
        downloadImage.setData(url);
        return downloadImage;
    }
	
    private Intent makeGalleryIntent(String pathToImageFile) {
        // Create an intent that will start the Gallery app to view
        // the image.
        //return new Intent(Intent.ACTION_VIEW, Uri.parse(pathToImageFile));
    	Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + pathToImageFile), "image/*");
        return i;
    }
}

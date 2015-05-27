package com.mooc.imageviewer;


import java.util.HashMap;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class DownloaderFragment extends Fragment {
	private Context mContext;

    public static final String URL = "url";
    public static final String IMAGE = "image_uri";
    public static final String ASYNCTASK = "download_async_task";

    private final static String TAG = "DownloaderFragment";

    private HashMap<String, Object> mData = new HashMap<>();

    public void put(String key, Object data) {
        mData.put(key, data);
    }

    public <T> T get(String key) {
        return (T) mData.get(key);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");

		// Preserve across reconfigurations
		setRetainInstance(true);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

        Log.d(TAG, "onAttach()");

		mContext = activity.getApplicationContext();
	}
	
	@Override
	public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
	}
}

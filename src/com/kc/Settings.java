package com.kc;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class Settings extends PreferenceActivity 
implements SharedPreferences.OnSharedPreferenceChangeListener, OnPreferenceClickListener {
	
	public static final String SHARE_KEY = "share";
	public static final String FEEDBACK_KEY = "send_feedback";
	public static boolean didUnlock = false;
	
    @Override
    public void onCreate(Bundle icicle) {
    	super.onCreate(icicle);
        //getPreferenceManager().setSharedPreferencesName(KCWallpaperService.SHARED_PREFS_NAME);
        addPreferencesFromResource(R.xml.settings);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    	findPreference(SHARE_KEY).setOnPreferenceClickListener(this);
    	findPreference(FEEDBACK_KEY).setOnPreferenceClickListener(this);
        Preference scenePref = (Preference) findPreference("select_scene");
        scenePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
        	
			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				
				SharedPreferences customSharedPreference = getSharedPreferences(
                        KCWallpaperService.SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = customSharedPreference.edit();
				editor.putString("sceneName",(String)newValue);
				editor.commit();
				
				String REGISTER = customSharedPreference.getString("sceneName", "scene not found");
				Log.w("KICK", "preference is :" + preference);
				Log.w("KICK", "new value is :" + newValue);
				Log.w("KICK", "what was registered was :" + REGISTER);
								
				finish();
				return true;
			}

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
    	finish();
        super.onDestroy();
        System.gc();
    }
    
    @Override
    protected void onStop(){
    	finish();
    	super.onDestroy();
    	System.gc();
    }
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
    }
    
    @Override
    public boolean onPreferenceClick(Preference pref) {
    	if(pref.getKey().equals(SHARE_KEY)){
    		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
    		shareIntent.setType("text/plain");
    		shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out Kick Cinema: stylish Android live wallpapers http://kickcinema.com");
    		startActivity(Intent.createChooser(shareIntent, "Share using"));
    		Log.w( "KICK" ,"share was selected");
    	} 
    	else if(pref.getKey().equals(FEEDBACK_KEY)){
    		Intent feedbackIntent = new Intent(android.content.Intent.ACTION_SEND);
    		feedbackIntent.setType("message/rfc822");
    		feedbackIntent.putExtra(Intent.EXTRA_EMAIL, "andrew@kickcinema.com");
    		feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, "andrew@kickcinema.com");
    		startActivity(Intent.createChooser(feedbackIntent, "Send Feedback"));
    	}
    	return true;
    }
    
}
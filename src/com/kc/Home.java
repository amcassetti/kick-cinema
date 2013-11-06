package com.kc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Home extends Activity {

	Button button;
	Button button2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//startActivity(new Intent(android.app.WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
		setContentView(R.layout.home);
		addListenerOnButton();

	}

	public void addListenerOnButton() {
		 
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View view) {
			  Intent settingsIntent = new Intent(android.app.WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
			  startActivity(settingsIntent);
			}
		});
		
		button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			 
			@Override
			public void onClick(View view) {
				//PanescService.sceneHandler.removeCallbacks(null);
			}
		});
 
	}

	
}

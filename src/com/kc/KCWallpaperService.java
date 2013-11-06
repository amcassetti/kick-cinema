package com.kc;

import java.io.IOException;
import java.io.InputStream;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.service.wallpaper.WallpaperService.Engine;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;

import com.kc.R;

public class KCWallpaperService extends WallpaperService{
	static final String TAG = "KICK";
    static final Handler mHandler = new Handler();
    public static String SHARED_PREFS_NAME = "kc_settings";
    Long serviceTimeStamp;
    
//    Movie mScene;
//    int mSceneDuration;
//    Runnable mRunnable;
//    InputStream is = null;
//    Canvas canvas = null;
//    SurfaceHolder surfaceHolder = null;
//    int currentSceneID;
    
    @Override
    public void onCreate() {
    	serviceTimeStamp = System.currentTimeMillis();
    	Log.w(TAG,"SERVICE ONCREATE "+ serviceTimeStamp);
        super.onCreate();
    }
    
    @Override
    public void onDestroy() {
    	Log.w(TAG,"SERVICE DESTORY "+ serviceTimeStamp);
    	super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
    	Log.w(TAG,"SERVICE ONCREATE ENGINE "+ System.currentTimeMillis());
    	try {
    		return new KCEngine();
        } catch (IOException e) {
            Log.w(TAG, "Error creating Engine", e);
            stopSelf();
            return null;
        }
    }

    class KCEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener{
        Movie mScene;
        int mSceneDuration;
        InputStream is = null;
        Canvas canvas = null;
        SurfaceHolder surfaceHolder = null;
        int currentSceneID;
        
        float mScaleX;
        float mScaleY;
        int mWhen;
        long mStart;
        
        SharedPreferences mPrefs;
        Long timeStamp;
        boolean mVisible;
        Runnable mRunnable;
        boolean mRefresh = false;
        
        
//        Runnable mRunnable = new Runnable() {
//            public void run() {
//            	step();
//            }
//        };

        KCEngine() throws IOException {
        	timeStamp = System.currentTimeMillis();
        	Log.d(TAG,"ENGINE CONSTRUCTOR "+ timeStamp);
        	
        	clearMemory();
        	
        	mPrefs = KCWallpaperService.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
            mPrefs.registerOnSharedPreferenceChangeListener(this);
            onSharedPreferenceChanged(mPrefs, null);
            
            mRunnable = new Runnable() {
                public void run() {
                	step();
                }
            };
        }
        
//        @Override
//        public void onCreate(SurfaceHolder surfaceHolder) {
//            super.onCreate(surfaceHolder);
//            Log.d(TAG,"ENGINE CREATE");
//        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.e(TAG,"ENGINE ONDESTORY "+timeStamp);
			clearMemory();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            //super.onVisibilityChanged(visible);
            mVisible = visible;
            if (visible) {
            	Log.d(TAG,"ENGINE VISIBLE");
            	step();
            } else {
            	Log.d(TAG,"ENGINE INVISIBLE");
                mHandler.removeCallbacks(mRunnable);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d(TAG,"ENGINE SURFACE CHANGED");
            mScaleY = height / (1f * mScene.height());
            mScaleX = mScaleY;
            if (height < width){
            	mScaleX = width / (1f * mScene.width());
            	mScaleY = mScaleX;
            }
			step();
        }

        @Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			Log.e(TAG,"ENGINE SURFACE DESTOYED " + timeStamp);
			mVisible = false;
			clearMemory();
		}
        
//        public void onSurfaceRedrawNeeded(SurfaceHolder holder) {
//			super.onSurfaceRedrawNeeded(holder);
//			Log.d(TAG,"ENGINE SURFACE REDRAW NEEDED");
//		}
        
		@Override
		public void onSharedPreferenceChanged(SharedPreferences prefs,
				String key) {
			Log.d(TAG,"ENGINE SHAREDPREFS CHANGED " + timeStamp);
			String sceneName = prefs.getString("sceneName", "scene not found");
			Log.w(TAG,"CURRENT SCENE NAME: " + sceneName);
			if(sceneName == "scene not found"){
				//DEFINE DEFAULT SCENE
				sceneName = "subway";
			}
			currentSceneID = getResources().getIdentifier(sceneName, "raw", "com.kc");
			
			clearMemory();
			try {
				loadScene(currentSceneID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			mScaleY = getSurfaceHolder().getSurfaceFrame().height() / (1f * mScene.height());
            mScaleX = mScaleY;
            if (getSurfaceHolder().getSurfaceFrame().height() < getSurfaceHolder().getSurfaceFrame().width()){
            	mScaleX = getSurfaceHolder().getSurfaceFrame().width() / (1f * mScene.width());
            	mScaleY = mScaleX;
            }
            
		}

		///////////////////////////////////////////////////////////
        public void step() {  	
            if (mWhen == -1L) {
                mWhen = 0;
                mStart = SystemClock.uptimeMillis();
            } else {
                long mDiff = SystemClock.uptimeMillis() - mStart;
                mWhen = (int) (mDiff % mSceneDuration);
            }
            surfaceHolder = getSurfaceHolder();
            canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    canvas.save();
                    canvas.scale(mScaleX, mScaleY);
                    mScene.setTime(mWhen);
                    mScene.draw(canvas, 0, 0);
                    canvas.restore();
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            mHandler.removeCallbacks(mRunnable);
            if (mVisible) {
                mHandler.postDelayed(mRunnable, 1000L/25L);
            }     
        }
        
        public void loadScene(int id) throws IOException {
        	Log.d(TAG,"ENGINE LOAD SCENE");
            is = getResources().openRawResource(id);
            if (is != null) {
                try {
                    mScene = Movie.decodeStream(is);
                    mSceneDuration = mScene.duration();
                } finally {
                    is.close();
                }
            } else {
                throw new IOException("Unable to open scene");
            }
            mWhen = -1;
        }
        
        public void clearMemory(){
        	Log.e(TAG,"ENGINE CLEAR MEMORY");
        	is = null;
        	mScene = null;
            surfaceHolder = null;
            canvas = null;
            mHandler.removeCallbacks(mRunnable);
        	System.gc();
        }
    }
	
	
}

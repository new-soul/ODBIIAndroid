package com.law.odbii.android;


import com.law.odbii.android.ODBIIView.ODBIIThread;

import android.app.Activity;
//import android.content.Context;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
import android.os.Bundle;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import android.widget.Button;
import android.widget.TextView;
//import android.widget.TextView;

public class ODBIIApp extends Activity {
	private static final int MENU_EXIT = 1;
	
	
	private ODBIIView mODBIIView;
	private ODBIIThread mODBIIThread;
	
	/**
     * Invoked during init to give the Activity a chance to set up its Menu.
     * 
     * @param menu the Menu to which entries may be added
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_EXIT, 0, R.string.menu_exit);

        return true;
    }
	
    /**
     * Invoked when the user selects an item from the Menu.
     * 
     * @param item the Menu entry which was selected
     * @return true if the Menu item was legit (and we consumed it), false
     *         otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_EXIT:
            	System.exit(0);
                
        }
        return true;
    }
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        
        mODBIIView = (ODBIIView)findViewById(R.id.odbii);
        mODBIIThread = mODBIIView.getThread();
        
        // status text:
        mODBIIView.setTextStatusView((TextView) findViewById(R.id.status));
        mODBIIView.setTextModeView((TextView) findViewById(R.id.simulation));
        
        
        mODBIIView.setStatus("No WIFI");
        mODBIIView.setMode("SIMULATION MODE");
        //WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        //WifiInfo info = wifi.getConnectionInfo();
        
        /*
        if (info.getBSSID() == null)
        	mODBIIView.setStatus("No WIFI");
        else
        	mODBIIView.setStatus(info.getBSSID());
        */	
        
        
        // set engine start button:
        Button startEngineButton = (Button)findViewById(R.id.start_button);
        startEngineButton.setOnTouchListener(new EngineStartButtonListener());
        
        // set brake button:
        Button brakeButton = (Button)findViewById(R.id.brake_button);
        brakeButton.setOnTouchListener(new BrakeButtonListener());        
        
        // set gas button:
        Button gasButton = (Button)findViewById(R.id.gas_button);
        gasButton.setOnTouchListener(new GasButtonListener());
        
        
        //
        
        
        
    }
    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state.
     * 
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
    }
    
    class GasButtonListener implements OnTouchListener
	{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_CANCEL)
			{
				mODBIIThread.footIsOffGas();
			}
			else if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				mODBIIThread.footIsOnGas();
			}
			else
			{
				mODBIIThread.footIsOffGas();
			}
			
			return false;
		}
	}
    class BrakeButtonListener implements OnTouchListener
	{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_CANCEL)
			{
				mODBIIThread.footIsOffBrake();
			}
			else if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				mODBIIThread.footIsOnBrake();
			}
			else
			{
				mODBIIThread.footIsOffBrake();
			}
			
			return false;
		}
	}
    class EngineStartButtonListener implements OnTouchListener
	{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				mODBIIThread.toggleEngine();
			}
			
			return false;
		}
	}
	
}
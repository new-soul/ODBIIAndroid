package com.law.odbii.android;


import com.law.odbii.android.ODBIIView.ODBIIThread;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import android.widget.Button;
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
        
        Log.w(this.getClass().getName(), "Setting content view:");
        setContentView(R.layout.main);
        Log.w(this.getClass().getName(), "OK!");
        
        
        mODBIIView = (ODBIIView)findViewById(R.id.odbii);
        mODBIIThread = mODBIIView.getThread();
        
        // set engine start button:
        Button startEngineButton = (Button)findViewById(R.id.start_button);
        startEngineButton.setOnTouchListener(new EngineStartButtonListener());
        
        // set brake button:
        Button brakeButton = (Button)findViewById(R.id.brake_button);
        brakeButton.setOnTouchListener(new BrakeButtonListener());        
        
        // set gas button:
        Button gasButton = (Button)findViewById(R.id.gas_button);
        gasButton.setOnTouchListener(new GasButtonListener());
       
        
        
        
        
        //mODBIIView.setTextView((TextView) findViewById(R.id.frameLayout1));
        
        
        
        if (savedInstanceState == null)
        {
        	mODBIIThread.setState(ODBIIThread.STATE_READY);
        	Log.w(this.getClass().getName(), "SIS is null");
        }
        else
        {
        	mODBIIThread.restoreState(savedInstanceState);
        	Log.w(this.getClass().getName(), "SIS is nonnull");
        	
        }
        
        
        
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
        //mODBIIThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }
    
    class GasButtonListener implements OnTouchListener
	{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_CANCEL)
			{
				mODBIIView.footIsOffGas();
			}
			else if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				mODBIIView.footIsOnGas();
			}
			else
			{
				mODBIIView.footIsOffGas();
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
				mODBIIView.footIsOffBrake();
			}
			else if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				mODBIIView.footIsOnBrake();
			}
			else
			{
				mODBIIView.footIsOffBrake();
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
				mODBIIView.toggleEngine();
			}
			
			return false;
		}
	}
	
}
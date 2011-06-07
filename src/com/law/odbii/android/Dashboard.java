package com.law.odbii.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Typeface;

import com.law.odbii.android.ODBIIView.ODBIIThread;

public class Dashboard {
	
	final int DRAWDASHBOARD = 0;
	final int UPDATEDASHBOARD = 1;
	private int state; // = DRAWDASHBOARD;
	
	protected static final float fullAccel = 12.0f; // miles / sec / sec
	protected static final float fullDecel = 50.0f; // miles/s/s when foot is on the brake
	protected static final float minDecel  = 1.0f;  // miles/s/s when foot is off the gas
	
	protected static final int ODOMETER_CENTER_X_PERCENT_PORT = 50;
	protected static final int ODOMETER_CENTER_Y_PERCENT_PORT = 20;
	protected static final double ODOMETER_RADIUS_PERCENT_PORT = 22.0;
	
	protected static final float idleRPM = 1500;
	protected static final int TACHOMETER_CENTER_X_PERCENT_PORT = 13;
	protected static final int TACHOMETER_CENTER_Y_PERCENT_PORT = 18;
	protected static final double TACHOMETER_RADIUS_PERCENT_PORT = 12.0;
	

	protected static final int FUELGUAGE_CENTER_X_PERCENT_PORT = 90;
	protected static final int FUELGUAGE_CENTER_Y_PERCENT_PORT = 15;
	protected static final double FUELGUAGE_RADIUS_PERCENT_PORT = 12.0;

	protected static final int TEMPGUAGE_CENTER_X_PERCENT_PORT = 90;
	protected static final int TEMPGUAGE_CENTER_Y_PERCENT_PORT = 30;
	protected static final double TEMPGUAGE_RADIUS_PERCENT_PORT = 12.0;

	protected static float mph = 0.1f;
	protected static float rpm = 0;
	protected static float gasLeft = 8;
	protected static float waterTemp = 150;
	
	
	//Button gasButton;
	protected static boolean gasPedalOn = false;
	protected static boolean brakePedalOn = false;
	protected static boolean engineOn = false;
	
	protected Point odometerCenter;
	protected Point tachometerCenter;
	protected Point fuelGaugeCenter;
	protected Point tempGaugeCenter;
	
	
	// reading for this screen:
	
	
	ODBIIThread mThread;
	Dashboard(ODBIIThread thread)
	{
		mThread = thread;
    	int a_x = (mThread.mCanvasWidth * Dashboard.ODOMETER_CENTER_X_PERCENT_PORT) / 100;
    	int a_y = (mThread.mCanvasHeight * Dashboard.ODOMETER_CENTER_Y_PERCENT_PORT) / 100;
    	
    	odometerCenter = new Point(a_x, a_y);
    	
    	
    	a_x = (mThread.mCanvasWidth * Dashboard.TACHOMETER_CENTER_X_PERCENT_PORT) / 100;
    	a_y = (mThread.mCanvasHeight * Dashboard.TACHOMETER_CENTER_Y_PERCENT_PORT) / 100;
    	tachometerCenter = new Point(a_x, a_y);
    	
    	a_x = (mThread.mCanvasWidth * Dashboard.FUELGUAGE_CENTER_X_PERCENT_PORT) / 100;
    	a_y = (mThread.mCanvasHeight * Dashboard.FUELGUAGE_CENTER_Y_PERCENT_PORT) / 100;            	
    	fuelGaugeCenter = new Point(a_x, a_y);
    	
    	a_x = (mThread.mCanvasWidth * Dashboard.TEMPGUAGE_CENTER_X_PERCENT_PORT) / 100;
    	a_y = (mThread.mCanvasHeight * Dashboard.TEMPGUAGE_CENTER_Y_PERCENT_PORT) / 100;            	
    	tempGaugeCenter = new Point(a_x, a_y);
    	
    	state = DRAWDASHBOARD;
		
	}
	protected void dashBoardState(Canvas canvas, Context mContext)
	{
		//drawDashBoard(canvas, mContext);
		
		switch (state)
		{
		case DRAWDASHBOARD:
			//drawDashBoard(c);
			drawDashBoard(canvas, mContext);
			state = UPDATEDASHBOARD;
			break;
		case UPDATEDASHBOARD:
			updatePhysics();
			doDraw(canvas);
		}
				
	}
	void drawDashBoard(Canvas canvas, Context mContext)
	{
		mThread.background = new Picture();
		canvas = mThread.background.beginRecording(mThread.mCanvasWidth, mThread.mCanvasHeight);
		// background:
		//Rect r = new Rect(0, 0, mCanvasWidth, mCanvasHeight);
		Paint p = new Paint();
		
		
		mThread.tach = new Tachnometer();
		mThread.odo = new Odometer();
		mThread.fuel = new FuelGauge(mContext);
		mThread.temp = new TempGauge(mContext);
		
		//canvas.drawBitmap(mBackgroundImage, 0, 0, null);
		p.setColor(Color.BLACK);
		
		canvas.drawARGB(255, 0, 0, 0);			
		
		Typeface typeface;
		typeface = Typeface.create("sans", Typeface.BOLD);
		
		p.setAntiAlias(true);
		
		p.setTextSize(18);
		p.setTypeface(typeface);			
		mThread.tach.drawTachnometer(	canvas, 
								p, 
								tachometerCenter, 
								(double)mThread.mCanvasWidth * TACHOMETER_RADIUS_PERCENT_PORT / 100.0, 
								Math.PI, Math.PI / 4.0,
								0, 7000,
								8);
		mThread.odo.drawOdometer(	canvas, 
				p, 
				odometerCenter, 
				(double)mThread.mCanvasWidth * ODOMETER_RADIUS_PERCENT_PORT / 100.0, 
				Math.PI, -Math.PI / 4.0,
				0, 150,
				16);
		
		
		mThread.fuel.drawFuelGuage(	canvas, 
				p, 
				fuelGaugeCenter, 
				(double)mThread.mCanvasWidth * FUELGUAGE_RADIUS_PERCENT_PORT / 100.0, 
				Math.PI, Math.PI / 2.0,
				0, 15,
				4);
		
		mThread.temp.drawTempGauge(	canvas, 
				p, 
				tempGaugeCenter, 
				(double)mThread.mCanvasWidth * TEMPGUAGE_RADIUS_PERCENT_PORT / 100.0, 
				Math.PI, Math.PI / 2.0,
				100, 260,
				4);
		mThread.background.endRecording();	
		
	}
	public void updatePhysics()
	{
		
		if ((gasPedalOn) && (engineOn))
		{
			mph += fullAccel / 2.0;
			rpm += 100;
		}
		else
		{
			mph -= minDecel / 2.0;
		}			
		if (brakePedalOn)
		{
			mph -= fullDecel / 2.0;
		}
		
		// do not let speed go past 150 or less than zero:
		mph = (mph > 150.0)?150.0f:mph;
		mph = (mph < 0.0)?0.0f:mph;
		
		
		
	}
	protected void doDraw(Canvas canvas) {
		
		mThread.background.draw(canvas);
		
		
		mThread.paint.setTextSize(22);
		mThread.paint.setColor(Color.CYAN);
		
		
		
		// Odometer:
		int mphLocX_txt = odometerCenter.x - mThread.percentWidth(5);
		int mphLocY_txt = odometerCenter.y + mThread.percentHeight(3);
		canvas.drawText(mThread.get3digit(mph), mphLocX_txt, mphLocY_txt, mThread.paint);
		mThread.odo.drawNeedle(canvas, (double)mph);
		
		
		// Tachometer:
		int rpmLocX_txt = tachometerCenter.x - mThread.percentWidth(5);
		int rpmLocY_txt = tachometerCenter.y + mThread.percentHeight(3);
		canvas.drawText(mThread.get4digit(rpm), rpmLocX_txt, rpmLocY_txt, mThread.paint);
		mThread.tach.drawNeedle(canvas, (double)rpm);
		
		

		// Fuel:
		mThread.fuel.drawNeedle(canvas, 10.0);
		
		
		// Water temperature:
		mThread.temp.drawNeedle(canvas, 101.0);
		
		canvas.drawText(mThread.get3digit(mThread.mCanvasHeight), 300, 600, mThread.paint);
		
		
	}

}

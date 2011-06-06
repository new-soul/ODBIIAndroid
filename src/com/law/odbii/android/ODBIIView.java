package com.law.odbii.android;


import java.text.DecimalFormat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Typeface;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.view.View;

import android.widget.TextView;

public class ODBIIView extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final float fullAccel = 12.0f; // miles / sec / sec
	private static final float fullDecel = 50.0f; // miles/s/s when foot is on the brake
	private static final float minDecel  = 1.0f;  // miles/s/s when foot is off the gas
	
	private static final int ODOMETER_CENTER_X_PERCENT_PORT = 50;
	private static final int ODOMETER_CENTER_Y_PERCENT_PORT = 20;
	private static final double ODOMETER_RADIUS_PERCENT_PORT = 22.0;
	
	
	private static final float idleRPM = 1500;
	private static final int TACHOMETER_CENTER_X_PERCENT_PORT = 13;
	private static final int TACHOMETER_CENTER_Y_PERCENT_PORT = 18;
	private static final double TACHOMETER_RADIUS_PERCENT_PORT = 12.0;
	

	private static final int FUELGUAGE_CENTER_X_PERCENT_PORT = 90;
	private static final int FUELGUAGE_CENTER_Y_PERCENT_PORT = 15;
	private static final double FUELGUAGE_RADIUS_PERCENT_PORT = 12.0;

	private static final int TEMPGUAGE_CENTER_X_PERCENT_PORT = 90;
	private static final int TEMPGUAGE_CENTER_Y_PERCENT_PORT = 30;
	private static final double TEMPGUAGE_RADIUS_PERCENT_PORT = 12.0;

	
	// reading for this screen:
	
	private float mph = 0.1f;
	private float rpm = 0;
	private float gasLeft = 8;
	private float waterTemp = 150;
	
	
	//Button gasButton;
	private boolean gasPedalOn = false;
	private boolean brakePedalOn = false;
	private boolean engineOn = false;
	
	/** Image for the needle of odometer:
	 * 
	 */	
	private Point odometerCenter;
	
	/** Image for the needle of tachometer:
	 * 
	 */
	private Point tachometerCenter;	
	
	
	private Point fuelGaugeCenter;
	
	private Point tempGaugeCenter;
	
	
	
	/** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;

    /** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;

    /** The thread that actually draws the animation */
    private ODBIIThread thread;
    
    
    /** CONSTRUCTOR */
    public ODBIIView(Context context, AttributeSet attrs) {
    	super(context, attrs);
        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
        
        
        
        // create thread only; it's started in surfaceCreated()
        thread = new ODBIIThread(holder, context, new Handler()
        {
        	@Override
            public void handleMessage(Message m) {
                //mStatusText.setVisibility(m.getData().getInt("viz"));
                //mStatusText.setText(m.getData().getString("text"));
            }
        });
        
        setFocusable(true);
        
        
        //gasButton = (Button)findViewById(R.id.button3);
           	
    	
    	
    }
    
    public ODBIIView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    
    
    
    
    public void footIsOffGas()
    {
    	gasPedalOn = false;
    }
    public void footIsOnGas()
    {
    	gasPedalOn = true;
    }
    
    public void footIsOffBrake()
    {
    	brakePedalOn = false;
    }
    public void footIsOnBrake()
    {
    	brakePedalOn = true;
    }
    public void toggleEngine()
    {
    	engineOn = !engineOn;
    	if (engineOn)
    		rpm = idleRPM;
    	else
    		rpm = 0.0f;
    }
    /**
     * Fetches the animation thread corresponding to this LunarView.
     * 
     * @return the animation thread
     */
    public ODBIIThread getThread() {
        return thread;
    }
    
    
    
    

	
	
	/**
     * Installs a pointer to the text view used for messages.
     */
    public void setTextView(TextView textView) {
        mStatusText = textView;
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		thread.setSurfaceSize(width, height);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
		
		
        //gasButton.setOnClickListener(this);
		
		
        thread.setRunning(true);
        thread.start();
		
        
        //gasButton.setOnClickListener(this);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
		
	}
	class ODBIIThread extends Thread {

		public static final int STATE_PAUSE = 2;
		public static final int STATE_READY = 3;
		public static final int STATE_RUNNING = 4;
	       /*
         * Member (state) fields
         */
        /** The drawable to use as the background of the animation canvas */
        private Bitmap mBackgroundImage;

        /**
         * Current height of the surface/canvas.
         * 
         * @see #setSurfaceSize
         */
        private int mCanvasHeight = 1;

        /**
         * Current width of the surface/canvas.
         * 
         * @see #setSurfaceSize
         */
        private int mCanvasWidth = 1;
 
        /** Message handler used by thread to interact with TextView */
        private Handler mHandler;
        
        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;
 
        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRun = false;
        
 
        /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
        private int mMode;
        
        private DecimalFormat df3, df4;
        
        
        private Picture background;
        
        
        private Paint paint;
        
        private Odometer odo;
        private Tachnometer tach;
        private FuelGauge fuel;
        private TempGauge temp;

        public ODBIIThread (SurfaceHolder surfaceHolder, Context context,
        Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;
            df3 = new DecimalFormat("###");
            df4 = new DecimalFormat("####");
            
            Resources res = context.getResources();
            
            mBackgroundImage = BitmapFactory.decodeResource(res,
            		R.drawable.dashboard);
            
            
            
        }
        
        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;

                // don't forget to resize the background image
                mBackgroundImage = mBackgroundImage.createScaledBitmap(
                        mBackgroundImage, width, height, true);
                
                
             // TODO Find the center of the odometer:            	
            	int a_x = (mCanvasWidth * ODOMETER_CENTER_X_PERCENT_PORT) / 100;
            	int a_y = (mCanvasHeight * ODOMETER_CENTER_Y_PERCENT_PORT) / 100;
            	
            	odometerCenter = new Point(a_x, a_y);
            	
            	
            	a_x = (mCanvasWidth * TACHOMETER_CENTER_X_PERCENT_PORT) / 100;
            	a_y = (mCanvasHeight * TACHOMETER_CENTER_Y_PERCENT_PORT) / 100;
            	tachometerCenter = new Point(a_x, a_y);
            	
            	a_x = (mCanvasWidth * FUELGUAGE_CENTER_X_PERCENT_PORT) / 100;
            	a_y = (mCanvasHeight * FUELGUAGE_CENTER_Y_PERCENT_PORT) / 100;            	
            	fuelGaugeCenter = new Point(a_x, a_y);
            	
            	a_x = (mCanvasWidth * TEMPGUAGE_CENTER_X_PERCENT_PORT) / 100;
            	a_y = (mCanvasHeight * TEMPGUAGE_CENTER_Y_PERCENT_PORT) / 100;            	
            	tempGaugeCenter = new Point(a_x, a_y);

            	
            	Typeface typeface;
            	typeface = Typeface.create("sans", Typeface.BOLD);
            	paint = new Paint();
    			paint.setAntiAlias(true);
    			paint.setColor(Color.CYAN);
    			paint.setTextSize(24);
    			paint.setTypeface(typeface);
    			
    			
    			
                
            }
        }
		public void doStart() {
			synchronized (mSurfaceHolder) {
				// TODO Add initialization code for starting of thread
			}
		}
		public void pause() {
			synchronized (mSurfaceHolder) {
				// TODO Add code for pausing of thread
			}
		}
		private int percentHeight(int amount)
		{
			return (mCanvasHeight * amount) / 100;
		}
		private int percentWidth(int amount)
		{
			return (mCanvasWidth * amount) / 100;
		}
		private String get3digit(float value)
		{
			String valueStr = new String("");
			valueStr = df3.format(value);
			
			if (valueStr.length() == 0)
				valueStr += "000";
			else if (valueStr.length() == 1)
				valueStr = "00" + valueStr;
			else if (valueStr.length() == 2)
				valueStr = "0" + valueStr;
			
			return valueStr;
		}
		private String get4digit(float value)
		{
			String valueStr = new String("");
			valueStr = df3.format(value);
			if (valueStr.length() == 0)
				valueStr += "0000";
			else if (valueStr.length() == 1)
				valueStr += "000";
			else if (valueStr.length() == 2)
				valueStr = "00" + valueStr;
			else if (valueStr.length() == 3)
				valueStr = "0" + valueStr;
			
			return valueStr;
		}

		private void drawDashBoard(Canvas canvas) {
			
			background = new Picture();
			canvas = background.beginRecording(mCanvasWidth, mCanvasHeight);
			// background:
			//Rect r = new Rect(0, 0, mCanvasWidth, mCanvasHeight);
			Paint p = new Paint();
			
			
			tach = new Tachnometer();
			odo = new Odometer();
			fuel = new FuelGauge(mContext);
			temp = new TempGauge(mContext);
			
			//canvas.drawBitmap(mBackgroundImage, 0, 0, null);
			p.setColor(Color.BLACK);
			
			canvas.drawARGB(255, 0, 0, 0);			
			
			Typeface typeface;
			typeface = Typeface.create("sans", Typeface.BOLD);
			
			p.setAntiAlias(true);
			
			p.setTextSize(18);
			p.setTypeface(typeface);			
			tach.drawTachnometer(	canvas, 
									p, 
									tachometerCenter, 
									(double)mCanvasWidth * TACHOMETER_RADIUS_PERCENT_PORT / 100.0, 
									Math.PI, Math.PI / 4.0,
									0, 7000,
									8);
			odo.drawOdometer(	canvas, 
					p, 
					odometerCenter, 
					(double)mCanvasWidth * ODOMETER_RADIUS_PERCENT_PORT / 100.0, 
					Math.PI, -Math.PI / 4.0,
					0, 150,
					16);
			
			
			fuel.drawFuelGuage(	canvas, 
					p, 
					fuelGaugeCenter, 
					(double)mCanvasWidth * FUELGUAGE_RADIUS_PERCENT_PORT / 100.0, 
					Math.PI, Math.PI / 2.0,
					0, 15,
					4);
			
			temp.drawTempGauge(	canvas, 
					p, 
					tempGaugeCenter, 
					(double)mCanvasWidth * TEMPGUAGE_RADIUS_PERCENT_PORT / 100.0, 
					Math.PI, Math.PI / 2.0,
					100, 260,
					4);
			background.endRecording();	
						
		}
		
		private void doDraw(Canvas canvas) {
			
			background.draw(canvas);
			
			
			paint.setTextSize(22);
			paint.setColor(Color.CYAN);
			
			
			
			// Odometer:
			int mphLocX_txt = odometerCenter.x - percentWidth(5);
			int mphLocY_txt = odometerCenter.y + percentHeight(3);
			canvas.drawText(get3digit(mph), mphLocX_txt, mphLocY_txt, paint);
			odo.drawNeedle(canvas, (double)mph);
			
			
			// Tachometer:
			int rpmLocX_txt = tachometerCenter.x - percentWidth(5);
			int rpmLocY_txt = tachometerCenter.y + percentHeight(3);
			canvas.drawText(get4digit(rpm), rpmLocX_txt, rpmLocY_txt, paint);
			tach.drawNeedle(canvas, (double)rpm);
			
			

			// Fuel:
			fuel.drawNeedle(canvas, 10.0);
			
			
			// Water temperature:
			temp.drawNeedle(canvas, 101.0);
			
			
			
		}
		
		
		 /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         * 
         * @param b true to run, false to shut down
         */
        public void setRunning(boolean b) {
            mRun = b;
        }
        
        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         * 
         * @see #setState(int, CharSequence)
         * @param mode one of the STATE_* constants
         */
        public void setState(int mode) {
            synchronized (mSurfaceHolder) {
                setState(mode, null);
            }
        }
        
        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         * 
         * @param mode one of the STATE_* constants
         * @param message string to add to screen or null
         */
        public void setState(int mode, CharSequence message) {
        	synchronized (mSurfaceHolder) {
        		mMode = mode;
        		Message msg = null;
        		Bundle b = null;
        		switch (mMode)
        		{
        		case STATE_RUNNING:
        			msg = mHandler.obtainMessage();
        			b = new Bundle();
        			b.putString("text", "");
        			b.putInt("viz", View.INVISIBLE);
        			msg.setData(b);
        			mHandler.sendMessage(msg);
        		default:
        			msg = mHandler.obtainMessage();
        			b = new Bundle();
        			b.putString("text", "");
        			b.putInt("viz", View.VISIBLE);
        			msg.setData(b);
        			mHandler.sendMessage(msg);
        			
        		}
        		
        	}
        }
        public void restoreState(Bundle savedState) {
        	synchronized (mSurfaceHolder) {
        		setState(STATE_PAUSE);        		
        	}
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
		@Override
		public void run()
		{
			final int DRAWDASHBOARD = 1;
			final int UPDATEDASHBOARD = 2;
			
			int state = DRAWDASHBOARD;
			
			while (mRun) {
				Canvas c = null;
				//Rect r = new Rect(0,0,400,300);
				
				
				
				try {
					
					
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
												
						switch (state)
						{
						case DRAWDASHBOARD:
							drawDashBoard(c);
							state = UPDATEDASHBOARD;
							break;
						case UPDATEDASHBOARD:
							updatePhysics();
							doDraw(c);
							
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						break;
						}
					}
				} finally {
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
				
			}
		}
	} // end of ODBIIThread class!
	

}

package com.law.odbii.android;


import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Typeface;

import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;




public class ODBIIView extends SurfaceView implements SurfaceHolder.Callback {
	
	/** Pointer to the text view to display status */
    private TextView mStatusText;

    /** Pointer to the text view to display status */
    private TextView mModeText;
    
    /** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;

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
    	
    }
    
    public ODBIIView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
 
    /**
     * Fetches the animation thread corresponding to this ODBII.
     * 
     * @return the animation thread
     */
    public ODBIIThread getThread() {
        return thread;
    }
    
    
    
    

	
	
	/**
     * Installs a pointer to the text view used for messages.
     */
    public void setTextStatusView(TextView textView) {
        mStatusText = textView;
    }
    public void setTextModeView(TextView textView) {
    	mModeText = textView;
    }
    public void setStatus(String value)
    {
    	mStatusText.setText(value);
    }
    public void setMode(String value)
    {
    	mModeText.setText(value);
    }    

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		thread.setSurfaceSize(width, height);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
		
        
        thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
       
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
        //private Bitmap mBackgroundImage;

        /**
         * Current height of the surface/canvas.
         * 
         * @see #setSurfaceSize
         */
        protected int mCanvasHeight = 1;

        /**
         * Current width of the surface/canvas.
         * 
         * @see #setSurfaceSize
         */
        protected int mCanvasWidth = 1;
 
        /** Message handler used by thread to interact with TextView */
        //private Handler mHandler;
        
        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder; 
        
        private DecimalFormat df3, df4;
        
        
        protected Picture background;
        
        
        protected Paint paint;
        
        protected Odometer odo;
        protected Tachnometer tach;
        protected FuelGauge fuel;
        protected TempGauge temp;

        public ODBIIThread (SurfaceHolder surfaceHolder, Context context,
        Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            //mHandler = handler;
            mContext = context;
            df3 = new DecimalFormat("###");
            df4 = new DecimalFormat("####");
            
            //Resources res = context.getResources();
            
            //mBackgroundImage = BitmapFactory.decodeResource(res,
            //		R.drawable.dashboard);
            
            
            
        }
        
        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;

                // don't forget to resize the background image
               // mBackgroundImage = mBackgroundImage.createScaledBitmap(
                //        mBackgroundImage, width, height, true);
            	
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
		protected int percentHeight(int amount)
		{
			return (mCanvasHeight * amount) / 100;
		}
		protected int percentWidth(int amount)
		{
			return (mCanvasWidth * amount) / 100;
		}
		protected String get3digit(float value)
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
		protected String get4digit(float value)
		{
			String valueStr = new String("");
			valueStr = df4.format(value);
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


		 
        public void footIsOffGas()
        {
        	Dashboard.gasPedalOn = false;
        }
        public void footIsOnGas()
        {
        	Dashboard.gasPedalOn = true;
        }
        
        public void footIsOffBrake()
        {
        	Dashboard.brakePedalOn = false;
        }
        public void footIsOnBrake()
        {
        	Dashboard.brakePedalOn = true;
        }
        public void toggleEngine()
        {
        	Dashboard.engineOn = !Dashboard.engineOn;
        	if (Dashboard.engineOn)
        		Dashboard.rpm = Dashboard.idleRPM;
        	else
        		Dashboard.rpm = 0.0f;
        }
		
		@Override
		public void run()
		{
			final int DASHBOARD = 0;
			int state = DASHBOARD;

	       
			Dashboard dash = null;
			
			
			while (true) {
				Canvas c = null;
				
				try {
					
					
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
						switch (state)
						{
						case DASHBOARD:
							if (dash == null)
								dash = new Dashboard(this);
							dash.dashBoardState(c, mContext);
							
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

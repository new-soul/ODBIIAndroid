package com.law.odbii.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Gauge {
	
	public double minValue;
	public double minAngle;
	public double maxValue;
	public double maxAngle;
	public double r;
	public Point  c;
	
	
	void drawNeedle(
			Canvas canvas, double value)
	{	
		// define needle at zero degrees:
		float pts[] = {0.0f, 0.0f, (float)r, (float)r};
		
		// draw needle:
		Paint paint = new Paint();
		paint.setStrokeWidth(2);
		paint.setColor(Color.WHITE);
		
		double m = - (maxAngle - minAngle) / (maxValue - minValue);
		
		//   theta(minValue) = m * minValue + b = minAngle  
		double b = minAngle - m * minValue;		
		double angle_radians = m * value + b;
		
		// now rotate:
		boolean isX = true;
		for (int i = 0; i < pts.length; i++)
		{
			if (isX)
				pts[i] = (float)((double)pts[i] * Math.cos(angle_radians));
			else
				pts[i] = (float)((double)pts[i] * Math.sin(angle_radians));
			isX = !isX;
		}		
		
		// now translate:
		isX = true;
		for (int i = 0; i < pts.length; i++)
		{
			if (isX)
				pts[i] = pts[i] + c.x;
			else
				pts[i] = pts[i] + c.y;
			isX = !isX;
		}		
		
		canvas.drawLines(pts, paint);
		
	}

}

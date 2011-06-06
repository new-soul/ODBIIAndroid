package com.law.odbii.android;

import java.text.NumberFormat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Tachnometer extends Gauge {
	
	
	

	Tachnometer() {}
	
	
	
	void drawTachnometer(
			Canvas canvas, 
			Paint paint, 
			Point center, 
			double radius, 
			double startingAngle,  // in radians
			double endingAngle,    // in radians
			double startingValue,
			double endingValue,
			int    numberOfMajorIncrements)
	{
		double increment = -(endingAngle - startingAngle) / (double)(numberOfMajorIncrements - 1);
		
		
		minValue = startingValue;
		maxValue = endingValue; // + increment;
		
		minAngle = startingAngle;		
		maxAngle = endingAngle;
		
		r = radius;
		c = center;
		
		
		
		
		
		int x_offsets[] = {5, 6, 6, 1, -4, -5, -10, -12,0};
		int y_offsets[] = {5, 5, 8, 11, 13, 16, 16, 10,0};
		
		paint.setColor(Color.WHITE);
		Paint paintText = new Paint(paint);
		
		
		paint.setStrokeWidth(7);
		
		Paint paintThinLine = new Paint(paint);		
		paintThinLine.setStrokeWidth(2);
		
		
		
		
		//canvas.drawLine(0, 0, 500, 500, paint);
		double inner1Radius = radius - (radius / 8.0);
		double inner2Radius = radius - (radius / 12.0);
		
		double theta = startingAngle;
		double theta2 = startingAngle + increment / 2.0;
		for (int i = 0; i < numberOfMajorIncrements; i++)
		{
			if (i > 5)
			{
				paint.setColor(Color.RED);
				paint.setColor(Color.RED);
			}
			
			// thick line:
			int startX = (int)(inner1Radius * Math.cos(theta) + (double)center.x);
			int startY = (int)(inner1Radius * Math.sin(theta) + (double)center.y);
			int stopX = (int)(radius * Math.cos(theta) + (double)center.x);
			int stopY = (int)(radius * Math.sin(theta) + (double)center.y);
			canvas.drawLine(startX, startY, stopX, stopY, paint);

			// text:
			canvas.drawText(NumberFormat.getInstance().format(i), 
					startX + x_offsets[i], startY + y_offsets[i], paintText);
			

			// thin line:
			startX = (int)(inner2Radius * Math.cos(theta2) + (double)center.x);
			startY = (int)(inner2Radius * Math.sin(theta2) + (double)center.y);
			stopX = (int)(radius * Math.cos(theta2) + (double)center.x);
			stopY = (int)(radius * Math.sin(theta2) + (double)center.y);
			if (i != numberOfMajorIncrements - 1)
				canvas.drawLine(startX, startY, stopX, stopY, paintThinLine);
			

			
			
			theta += increment;
			theta2 += increment;
			
			
			
		}
	}
}

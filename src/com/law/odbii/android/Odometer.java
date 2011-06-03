package com.law.odbii.android;

import java.text.NumberFormat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;


public class Odometer {
	Odometer() {}
	void drawOdometer(
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
		
		// Interpolate instead???
		//                 0  1  2  3  4   5    6    7   8    9   10  11  12  13  14  15
		int x_offsets[] = {5, 6, 6, 1, -4, -5, -10, -12, -10, -12,-25,-28,-30,-30,-30,-35};
		int y_offsets[] = {5, 5, 8, 11, 13, 16, 16, 12,  14,  14,  14, 12, 10, 8,  8,  5};
		
		
		paint.setColor(Color.WHITE);
		
		
		Paint paint2 = new Paint(paint);
		paint.setStrokeWidth(7);
		
		paint2.setStrokeWidth(2);
		
		//canvas.drawLine(0, 0, 500, 500, paint);
		double inner1Radius = radius - (radius / 8.0);
		double inner2Radius = radius - (radius / 12.0);
		double increment = -(endingAngle - startingAngle) / (double)numberOfMajorIncrements;
		double theta = startingAngle;
		double theta2 = startingAngle + increment / 2.0;
		for (int i = 0; i < numberOfMajorIncrements; i++)
		{
			
			
			int startX = (int)(inner1Radius * Math.cos(theta) + (double)center.x);
			int startY = (int)(inner1Radius * Math.sin(theta) + (double)center.y);
			int stopX = (int)(radius * Math.cos(theta) + (double)center.x);
			int stopY = (int)(radius * Math.sin(theta) + (double)center.y);
			canvas.drawLine(startX, startY, stopX, stopY, paint);
			canvas.drawText(NumberFormat.getInstance().format(i * 10), 
					startX + x_offsets[i], startY + y_offsets[i], paint);

			startX = (int)(inner2Radius * Math.cos(theta2) + (double)center.x);
			startY = (int)(inner2Radius * Math.sin(theta2) + (double)center.y);
			stopX = (int)(radius * Math.cos(theta2) + (double)center.x);
			stopY = (int)(radius * Math.sin(theta2) + (double)center.y);
			

			if (i != numberOfMajorIncrements - 1)
				canvas.drawLine(startX, startY, stopX, stopY, paint2);
			
			theta += increment;
			theta2 += increment;
			
		}
	}
}

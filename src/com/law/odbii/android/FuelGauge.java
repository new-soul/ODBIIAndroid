package com.law.odbii.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class FuelGauge extends Gauge {
	
	
	FuelGauge() {}
	
	void drawFuelGuage(
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
		
		paint.setColor(Color.WHITE);
		Paint paintText = new Paint(paint);		
		Paint paintThinLine = new Paint(paint);
		
		paint.setStrokeWidth(7);
		paintThinLine.setStrokeWidth(2);
		paintText.setTextSize(22.0f);
		
		//canvas.drawLine(0, 0, 500, 500, paint);
		double inner1Radius = radius - (radius / 8.0);
		double inner2Radius = radius - (radius / 12.0);
		
		double theta = startingAngle;
		double theta2 = startingAngle + increment / 2.0;
		for (int i = 0; i < numberOfMajorIncrements; i++)
		{
			if (i == 0)
				paint.setColor(Color.RED);
			else
				paint.setColor(Color.WHITE);
			
			
			int startX = (int)(inner1Radius * Math.cos(theta) + (double)center.x);
			int startY = (int)(inner1Radius * Math.sin(theta) + (double)center.y);
			int stopX = (int)(radius * Math.cos(theta) + (double)center.x);
			int stopY = (int)(radius * Math.sin(theta) + (double)center.y);
			canvas.drawLine(startX, startY, stopX, stopY, paint);

			startX = (int)(inner2Radius * Math.cos(theta2) + (double)center.x);
			startY = (int)(inner2Radius * Math.sin(theta2) + (double)center.y);
			stopX = (int)(radius * Math.cos(theta2) + (double)center.x);
			stopY = (int)(radius * Math.sin(theta2) + (double)center.y);
			

			if (i != numberOfMajorIncrements - 1)
				canvas.drawLine(startX, startY, stopX, stopY, paintThinLine);
			
			theta += increment;
			theta2 += increment;
			
		}
		paint.setTextSize(22);
		int x_pos = (int)(radius * Math.cos(startingAngle) + (double)center.x);
		int y_pos = (int)(radius * Math.sin(startingAngle) + (double)center.y);
		canvas.drawText("E", 
				x_pos + 0, y_pos + 22, paintText);
		x_pos = (int)(radius * Math.cos(theta) + (double)center.x);
		y_pos = (int)(radius * Math.sin(theta) + (double)center.y);
		canvas.drawText("F", 
				x_pos - 27, y_pos + 5, paintText);
	}
}

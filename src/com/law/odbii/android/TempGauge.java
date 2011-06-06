package com.law.odbii.android;

import java.text.NumberFormat;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class TempGauge extends Gauge {
	
	private Drawable mWaterIcon;
	
	
	TempGauge(Context context) {
		
		mWaterIcon = context.getResources().
			getDrawable(R.drawable.water);
	}

	void drawTempGauge(
			Canvas canvas, 
			Paint paint, 
			Point center, 
			double radius, 
			double startingAngle,  // in radians
			double endingAngle,    // in radians
			int startingValue,
			int endingValue,
			int numberOfMajorIncrements)
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
		
		//canvas.drawLine(0, 0, 500, 500, paint);
		double inner1Radius = radius - (radius / 8.0);
		double inner2Radius = radius - (radius / 12.0);
		double theta = startingAngle;
		double theta2 = startingAngle + increment / 2.0;
		for (int i = 0; i < numberOfMajorIncrements; i++)
		{
			if (i >= numberOfMajorIncrements - 1)
				paint.setColor(Color.RED);
			
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
		
		int x_pos = (int)(radius * Math.cos(startingAngle) + (double)center.x);
		int y_pos = (int)(radius * Math.sin(startingAngle) + (double)center.y);
		canvas.drawText(NumberFormat.getInstance().format(startingValue), 
				x_pos + 0, y_pos + 22, paintText);
		x_pos = (int)(radius * Math.cos(theta) + (double)center.x);
		y_pos = (int)(radius * Math.sin(theta) + (double)center.y);
		canvas.drawText(NumberFormat.getInstance().format(endingValue), 
				x_pos - 27, y_pos + 5, paintText);

		//paint.setColor(Color.GREEN);
		//canvas.drawRect(x_pos - 15, y_pos + 15, x_pos + 10,  y_pos + 50, paintThinLine);
		mWaterIcon.setBounds(x_pos - 15, y_pos + 15, x_pos + 10, y_pos + 50);
		mWaterIcon.draw(canvas);

	}
}

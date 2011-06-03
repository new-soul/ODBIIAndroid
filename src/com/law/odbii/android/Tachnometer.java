package com.law.odbii.android;

import java.text.NumberFormat;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class Tachnometer {

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
		
		paint.setStrokeWidth(5);
		
		//canvas.drawLine(0, 0, 500, 500, paint);
		double inner1Radius = radius - (radius / 6);
		double increment = -(endingAngle - startingAngle) / (double)numberOfMajorIncrements;
		double theta = startingAngle;
		for (int i = 0; i < numberOfMajorIncrements; i++)
		{
			
			
			int startX = (int)(inner1Radius * Math.cos(theta) + (double)center.x);
			int startY = (int)(inner1Radius * Math.sin(theta) + (double)center.y);
			int stopX = (int)(radius * Math.cos(theta) + (double)center.x);
			int stopY = (int)(radius * Math.sin(theta) + (double)center.y);
			canvas.drawLine(startX, startY, stopX, stopY, paint);
			
			switch (i) 
			{
			case 0:
				canvas.drawText(NumberFormat.getInstance().format(i),
						startX + 5, startY + 5, paint);
				break;
			case 1:
				canvas.drawText(NumberFormat.getInstance().format(i),
						startX + 6, startY + 5, paint);
				break;
			case 2:
				canvas.drawText(NumberFormat.getInstance().format(i),
						startX + 6, startY + 8, paint);
				break;
			case 3:
				canvas.drawText(NumberFormat.getInstance().format(i),
						startX + 1, startY + 11, paint);
				break;
			case 4:
				canvas.drawText(NumberFormat.getInstance().format(i),
						startX - 4, startY + 13, paint);
				break;
			case 5:
				canvas.drawText(NumberFormat.getInstance().format(i),
						startX - 5, startY + 16, paint);
				break;
			case 6:
				canvas.drawText(NumberFormat.getInstance().format(i),
						startX - 10, startY + 16, paint);
				break;
			case 7:
				canvas.drawText(NumberFormat.getInstance().format(i),
						startX - 12, startY + 10, paint);
				break;
			}
			
			
			theta += increment;
			
		}
	}
}

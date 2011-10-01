package com.arcao.sayitloud.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class HighlightView extends View {
	protected HighlightThread highlightThread;
	protected Object highlightLock = new Object();
	protected final Paint paint;
	protected boolean isHighlight = false;
	protected boolean touchHighlightAllowed = true; 
	
	protected int foregroundColor = Color.WHITE;
	protected int backgroundColor = Color.BLACK;

	public HighlightView(Context context) {
		super(context);
		paint = new Paint(); 
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (touchHighlightAllowed)
			highlight();
		
		return true;
	}
	
	public Paint getPaint() {
		return paint;
	}

	public void highlight() {
		synchronized (highlightLock) {
			if (highlightThread != null && highlightThread.isAlive())
				return;
			
			highlightThread = new HighlightThread();
			highlightThread.start();
		}
	}
	
	/**
	 * Stops highlight thread if is running
	 */
	public void shutdown() {
		if (highlightThread != null)
			highlightThread.shutdown();
	}
	
	public void setTouchHighlightAllowed(boolean touchHighlightAllowed) {
		this.touchHighlightAllowed = touchHighlightAllowed;
	}
	
	public boolean isTouchHighlightAllowed() {
		return touchHighlightAllowed;
	}
		
	public void setForegroundColor(int color) {
		this.foregroundColor = color;
	}
	
	public int getForegroundColor() {
		return foregroundColor;
	}
	
	@Override
	public void setBackgroundColor(int color) {
		super.setBackgroundColor(color);
		backgroundColor = color;
	}
	
	protected void drawBackground(Canvas canvas) {
		if (isHighlight) {
			paint.setColor(foregroundColor);
		} else {
			paint.setColor(backgroundColor);
		}
		
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
	}
	
	protected void drawForeground(Canvas canvas) {
		if (isHighlight) {
			paint.setColor(backgroundColor);
		} else {
			paint.setColor(foregroundColor);
		}
	}
	
	class HighlightThread extends Thread {
		private static final int ITERATIONS = 6;
		private static final int WAIT_MS = 100;
		private boolean stop = false;
		
		@Override
		public void run() {		
			for (int i = 0; i < ITERATIONS; i++) {
				if (stop)
					break;
				
				isHighlight = (i % 2 == 0);
				postInvalidate();
				
				try {
					sleep(WAIT_MS);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		
		public void shutdown() {
			stop = true;
		}
	}
}

package com.arcao.sayitlouder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.arcao.sayitlouder.shake.ShakeDetector;
import com.arcao.sayitlouder.shake.ShakeListener;

public class HighlightView extends View implements ShakeListener {
	protected HighlightThread highlightThread;
	protected Object highlightLock = new Object();
	protected final Paint paint;
	protected boolean isHighlight = false;
	protected boolean touchHighlightAllowed = true; 
	protected boolean shakeHighlightAllowed = true;
	
	protected int foregroundColor = Color.WHITE;
	protected int backgroundColor = Color.BLACK;
	private final ShakeDetector detector;

	public HighlightView(Context context) {
		super(context);
		
		detector = new ShakeDetector(context);
		detector.addListener(this);

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
	
	public void onResume() {
		detector.start();
	}
	
	public void onPause() {
		detector.stop();
		if (highlightThread != null)
			highlightThread.shutdown();
	}
	
	@Override
	public void onShake() {
		if (shakeHighlightAllowed)
			highlight();
	}
		
	public void setTouchHighlightAllowed(boolean touchHighlightAllowed) {
		this.touchHighlightAllowed = touchHighlightAllowed;
	}
	
	public boolean isTouchHighlightAllowed() {
		return touchHighlightAllowed;
	}
	
	public void setShakeHighlightAllowed(boolean shakeHighlightAllowed) {
		this.shakeHighlightAllowed = shakeHighlightAllowed;
	}
	
	public boolean isShakeHighlightAllowed() {
		return shakeHighlightAllowed;
	}
		
	public void setForegroundColor(int color) {
		this.foregroundColor = color;
	}
	
	public int getForegroundColor() {
		return foregroundColor;
	}
	
	public void setShakeThresholdForce(int force) {
		detector.setThresholdForce(force);
	}
	
	public int getShakeThresholdForce() {
		return detector.getThresholdForce();
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
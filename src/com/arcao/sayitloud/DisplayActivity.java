package com.arcao.sayitloud;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.arcao.sayitloud.shake.ShakeDetector;
import com.arcao.sayitloud.shake.ShakeListener;

public class DisplayActivity extends Activity implements ShakeListener {
	public static final String INTENT_EXTRA_MESSAGE = "MESSAGE";
	
	private ShakeDetector detector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.display);
		LinearLayout v = (LinearLayout) findViewById(R.id.displayHolder);
		v.addView(new MessageView(this, getIntent().getStringExtra(INTENT_EXTRA_MESSAGE)));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		detector = new ShakeDetector(this);
		detector.addListener(this);
		detector.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		detector.stop();
		detector.removeListener(this);
	}
	
	private class MessageView extends View {
		private final String message;
		private int textSize = -1;
		
		public MessageView(Context context, String message) {
			super(context);
			this.message = message;
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			
			if (textSize == -1)
				textSize = computeSize(message);
			
			Paint paint = new Paint();
			Rect bounds = new Rect();
			
	    paint.setTextSize(textSize);
	    paint.getTextBounds(message, 0, message.length(), bounds);
	    paint.setTextAlign(Align.CENTER);
			paint.setARGB(255, 0, 255, 0);
			canvas.drawText(message, bounds.centerX(), (getWidth() - bounds.centerY()) / 2, paint);
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			
			textSize = -1;
		}
		
		private int computeSize(String text) {
			Paint paint = new Paint();
			Rect bounds = new Rect();
			
			int width = getWidth();
			int height = getHeight();

			int size = 0;

			while (bounds.width() < width && bounds.height() < height){
					size++;
			    paint.setTextSize(size);
			    paint.getTextBounds(text, 0, text.length(), bounds);
			}
			
			return size - 1; 
		}
	}

	@Override
	public void onShake() {
		
	}
}

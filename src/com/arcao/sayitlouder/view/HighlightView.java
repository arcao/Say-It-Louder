package com.arcao.sayitlouder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.arcao.sayitlouder.configuration.BasicConfiguration;
import com.arcao.sayitlouder.shake.ShakeDetector;
import com.arcao.sayitlouder.shake.ShakeListener;

public abstract class HighlightView extends SurfaceView implements SurfaceHolder.Callback, ShakeListener {
	public static final int HIGHLIGHT_MODE__INVERSE = 0;
	public static final int HIGHLIGHT_MODE__BLINK = 1;

	protected final Paint paint = new Paint();

	protected boolean doHighlight = false;
	protected boolean isHighlight = false;

	private final ShakeDetector detector;
	private final AnimThread animThread;

	private final BasicConfiguration configuration;

	public HighlightView(Context context, BasicConfiguration configuration) {
		super(context);

		this.configuration = configuration;
		
		detector = new ShakeDetector(context);
		detector.setThresholdForce(configuration.getShakeThresholdForce());
		detector.addListener(this);

		getHolder().addCallback(this);

		paint.setAntiAlias(true);
		paint.setTypeface(Typeface.DEFAULT_BOLD);

		animThread = new AnimThread(this);
		setFocusable(true);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (configuration.isTouchHighlightAllowed())
			highlight();
		
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		animThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		animThread.terminate();
	}

	public void highlight() {
		doHighlight = true;
	}
	
	public void onResume() {
		detector.start();
	}
	
	public void onPause() {
		detector.stop();
	}
	
	@Override
	public void onShake() {
		if (configuration.isShakeHighlightAllowed())
			highlight();
	}

	protected void prepareBackground(Canvas canvas) {
		if (isHighlight && configuration.getHighlightMode() != HIGHLIGHT_MODE__BLINK) {
			canvas.drawColor(configuration.getForegroundColor());
		} else {
			canvas.drawColor(configuration.getBackgroundColor());
		}
	}
	
	protected void prepareForeground() {
		if (isHighlight) {
			paint.setColor(configuration.getBackgroundColor());
		} else {
			paint.setColor(configuration.getForegroundColor());
		}
	}

	protected abstract void drawFrame(Canvas canvas);

	private long lastHighlight = 0;
	private int highlightIteration = 0;
	private static final long HIGHLIGHT_STEP_DURATION_MS = 80;
	private static final long HIGHLIGHT_COUNT = 4;

	protected void handleHighlight() {
		if (doHighlight) {
			if (System.currentTimeMillis() - lastHighlight > HIGHLIGHT_STEP_DURATION_MS) {
				isHighlight = !isHighlight;

				if (isHighlight) {
					highlightIteration++;
					if (highlightIteration > HIGHLIGHT_COUNT) {
						doHighlight = false;
						highlightIteration = 0;
						lastHighlight = 0;
						isHighlight = false;
					}
				}
				lastHighlight = System.currentTimeMillis();
			}
		} else {
			isHighlight = false;
			lastHighlight = 0;
		}
	}
}
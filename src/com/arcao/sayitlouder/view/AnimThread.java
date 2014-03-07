package com.arcao.sayitlouder.view;

import android.graphics.Canvas;
import android.view.Surface;

public class AnimThread implements Runnable {
	// desired fps
	private final static int MAX_FPS = 50;
	// the frame period
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;

	private final HighlightView highlightView;
	private Thread thread;

	public AnimThread(HighlightView highlightView) {
		this.highlightView = highlightView;
	}

	public void terminate(){
		if (thread == null)
			return;

		Thread t = thread;
		thread = null;

		boolean retry = true;
		while (retry) {
			try {
				t.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void start() {
		if (thread == null){
			thread = new Thread(this);
			thread.start();  // Start a new thread
		}
	}

	@Override
	public void run() {
		long lastRenderTime = FRAME_PERIOD;

		while(thread != null) {
			Canvas canvas = null;
			long sleepTime = 0;

			Surface surface = highlightView.getHolder().getSurface();
			if (surface == null || !surface.isValid()) {
				Thread.yield();
				continue;
			}

			try {
				canvas = highlightView.getHolder().lockCanvas(null);
				if (canvas == null) {
					Thread.yield();
					continue;
				}

				synchronized (highlightView.getHolder()) {
					long beginTime = System.currentTimeMillis();

					// draw
					highlightView.handleHighlight();
					highlightView.prepareBackground(canvas);
					highlightView.prepareForeground();
					highlightView.drawFrame(canvas, lastRenderTime);

					lastRenderTime = System.currentTimeMillis() - beginTime;
					// calculate sleep time
					sleepTime = FRAME_PERIOD - lastRenderTime;
				}
			} finally {
				if (canvas != null) {
					highlightView.getHolder().unlockCanvasAndPost(canvas);
				}
			}

			if (sleepTime > 0 && thread != null) {
				// if sleepTime > 0 we're OK
				try {
					// send the thread to sleep for a short period
					// very useful for battery saving
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
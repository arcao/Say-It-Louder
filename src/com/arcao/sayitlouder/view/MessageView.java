package com.arcao.sayitlouder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import com.arcao.sayitlouder.configuration.BasicConfiguration;
import com.arcao.sayitlouder.text.TextWrapper;

public class MessageView extends HighlightView {
	private static final float INITIAL_FONT_SIZE = 24f;

	private final String message;
	private final Rect bounds = new Rect();

	private float width = 0;
	private float height = 0;

	private float textSize = 0;
	private String[] messageToDraw = new String[0];
	
	public MessageView(Context context, BasicConfiguration configuration, String message) {
		super(context, configuration);

		if (message == null) {
			this.message = " ";
		} else {
			this.message = message;
		}
	}

	@Override
	protected void drawFrame(Canvas canvas) {
		if (textSize == 0) {
			prepareText();
		}

		// centered by computed line width
		paint.setTextAlign(Align.LEFT);

		// compute initial y
		float messageHeight = paint.getFontSpacing() * messageToDraw.length;
		// because text is drawn from text baseline y coordinate (absolute value of
		// ascent) we must add abs(ascent) to y coordinate.
		float y = (height - messageHeight) / 2f + Math.abs(paint.ascent());

		for (String m : messageToDraw) {
			paint.getTextBounds(m, 0, m.length(), bounds);

			// compute x by line width
			float x = (width - bounds.width()) / 2f;

			canvas.drawText(m, x, y, paint);
			y += paint.getFontSpacing();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		super.surfaceChanged(holder, format, width, height);

		this.width = width;
		this.height = height;
		textSize = 0;
	}

	protected void prepareText() {
		Log.i("screen size", width + "x" + height);

		// initial size
		paint.setTextSize(INITIAL_FONT_SIZE);

		// compute average ratio (height / width) of a single character
		paint.getTextBounds(message, 0, message.length(), bounds);
		float fontRatio = (bounds.height() / (bounds.width() / (float) message.length()));

		// compute ratio (height / width) of the target area for this text
		float targetRatio = height / width;

		// wrap message
		String messageWrapped = TextWrapper.performWrap(message, targetRatio, fontRatio);

		messageToDraw = messageWrapped.split("\n");

		// compute font size
		float heightRatio = (height / (paint.getFontSpacing() * messageToDraw.length)) * 0.8f;

		int maxWidth = 0;
		for (String m : messageToDraw) {
			paint.getTextBounds(m, 0, m.length(), bounds);
			maxWidth = Math.max(maxWidth, bounds.width());
		}

		float widthRatio = (width / maxWidth) * 0.95f;

		// set font size
		textSize = INITIAL_FONT_SIZE * Math.min(heightRatio, widthRatio);
		paint.setTextSize(textSize);
	 
	}
}
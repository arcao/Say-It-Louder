package com.arcao.sayitlouder.configuration;

import android.graphics.Color;

public class BasicConfiguration {
	private boolean touchHighlightAllowed = true;
	private boolean shakeHighlightAllowed = true;
	private int foregroundColor = Color.WHITE;
	private int backgroundColor = Color.BLACK;
	private int highlightMode = 0;
	private int shakeThresholdForce = 600;

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

	public void setShakeThresholdForce(int shakeThresholdForce) {
		this.shakeThresholdForce = shakeThresholdForce;
	}

	public int getShakeThresholdForce() {
		return shakeThresholdForce;
	}

	public void setHighlightMode(int highlightMode) {
		this.highlightMode = highlightMode;
	}

	public int getHighlightMode() {
		return highlightMode;
	}

	public void setBackgroundColor(int color) {
		backgroundColor = color;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}
}

package com.arcao.sayitlouder.shake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShakeDetector implements SensorEventListener {
	private static final int DEFAULT_THRESHOLD_FORCE = 700;

	private final SensorManager sensorService;
	private Sensor sensor;
	private long lastUpdate = -1;

	private int thresholdForce = DEFAULT_THRESHOLD_FORCE;
	private float last_x, last_y, last_z;

	private final List<ShakeListener> listeners = new CopyOnWriteArrayList<>();

	public ShakeDetector(Context parent) {
		sensorService = (SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorService.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			sensor = sensors.get(0);
		}
	}

	public void start() {
		if (sensor != null) {
			sensorService.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void stop() {
		sensorService.unregisterListener(this);
	}

	public void setThresholdForce(int thresholdForce) {
		this.thresholdForce = thresholdForce;
	}

	public int getThresholdForce() {
		return thresholdForce;
	}

	public void addListener(ShakeListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ShakeListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void onAccuracyChanged(Sensor s, int value) {
		// do nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || event.values.length < 3)
			return;

		long currentTime = System.currentTimeMillis();

		if ((currentTime - lastUpdate) > 100) {
			long diffTime = (currentTime - lastUpdate);
			lastUpdate = currentTime;

			float current_x = event.values[0];
			float current_y = event.values[1];
			float current_z = event.values[2];

			float currentForce = Math.abs(current_x + current_y + current_z - last_x - last_y - last_z) / diffTime * 10000;

			if (currentForce > thresholdForce) {
				// publish shake event
				fireOnShake();
			}
			last_x = current_x;
			last_y = current_y;
			last_z = current_z;
		}
	}

	protected void fireOnShake() {
		for (ShakeListener listener : listeners) {
			listener.onShake();
		}
	}
}

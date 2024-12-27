package com.gdsoftworks.app;

public interface Sound {
	/**
	 * Add decibel argument later
	 * @param volume
	 * @return
	 */
	public Sound play(double volume);
	public void dispose();
}

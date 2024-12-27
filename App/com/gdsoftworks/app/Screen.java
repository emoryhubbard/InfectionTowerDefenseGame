package com.gdsoftworks.app;

public abstract class Screen {
	protected final App app;
	public Screen(App app) {this.app=app;}
	public abstract void update(double deltaTime);
	public abstract void present();
	public abstract void pause();
	public abstract void resume();
	public abstract void dispose();
}

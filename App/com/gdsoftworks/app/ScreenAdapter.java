package com.gdsoftworks.app;

public abstract class ScreenAdapter extends Screen {
	public ScreenAdapter(App app) {super(app);}
	public void update(double deltaTime){} public void present(){}
	public void pause(){} public void resume(){}
	public void dispose(){}
}

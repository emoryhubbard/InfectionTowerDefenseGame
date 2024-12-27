package com.gdsoftworks.app;

public interface App {
	public UserInput getUserInput(); public IO getIO();
	
	public Graphics getGraphics(); public Audio getAudio();
	
	public Screen getStartScreen(); public Screen getCurrentScreen();
	
	public void setScreen(Screen screen);
}

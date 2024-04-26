package com.themarbles.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;


// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setForegroundFPS(60);
		config.setTitle("The Marbles");
		config.setResizable(false);
		config.setWindowedMode(1270,690);
		//config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

		new Lwjgl3Application(new EntryPoint(), config);
	}
}
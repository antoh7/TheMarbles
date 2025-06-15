package ru.kbuearpov.themarbles;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;


// run with -XstartOnFirstThread JVM arg on macOS
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setForegroundFPS(60);
		config.setTitle("The Marbles");
		config.setResizable(false);
		config.setWindowedMode(1200, 720);

		new Lwjgl3Application(new EntryPoint(), config);
	}
}

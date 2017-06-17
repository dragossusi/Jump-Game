package com.rachierudragos.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rachierudragos.game.MyGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = MyGame.WIDTH*3/4;
		config.height = MyGame.HEIGHT*3/4;
		config.title = MyGame.TITLE;
		new LwjglApplication(new MyGame(), config);
	}
}

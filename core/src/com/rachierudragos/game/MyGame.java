package com.rachierudragos.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rachierudragos.game.States.GameStateManager;
import com.rachierudragos.game.States.MenuState;

public class MyGame extends ApplicationAdapter {
    public static final int WIDTH = 480;
    public static final int HEIGHT = 800;
    public static final String TITLE = "Gioace";
    private Preferences preferences;
    private GameStateManager gsm;
    private SpriteBatch batch;

    @Override

    public void create() {
        batch = new SpriteBatch();
        gsm = new GameStateManager();
        gsm.push(new MenuState(gsm));
        Gdx.gl.glClearColor(1, 1, 0, 1);
        preferences = Gdx.app.getPreferences("highscore");
        preferences.putBoolean("nou", false).flush();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render(batch);
    }
}

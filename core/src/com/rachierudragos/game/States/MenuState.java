package com.rachierudragos.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rachierudragos.game.MyGame;

/**
 * Created by Dragos on 19.05.2016.
 */
public class MenuState extends State {
    private Texture background;
    private Texture playbtn;
    private int scor;
    Preferences preferences;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        background = new Texture("bg.png");
        playbtn = new Texture("play.png");
        preferences = Gdx.app.getPreferences("highscore");
        scor = preferences.getInteger("scor", 0);
    }

    @Override
    public void handleInput() {
        if (Gdx.input.justTouched()) {
            gsm.set(new PlayState(gsm));
            dispose();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        cam.setToOrtho(false, MyGame.WIDTH, MyGame.HEIGHT);
        cam.update();
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0, 0, MyGame.WIDTH, MyGame.HEIGHT);
        sb.draw(playbtn, MyGame.WIDTH / 2 - playbtn.getWidth() / 2, MyGame.HEIGHT / 2 - 150);
        if (scor != 0) {
            int aux=scor;
            int xScor=230;
            while(aux!=0) {
                xScor-=10;
                aux/=10;
            }
            BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"));
            font.draw(sb, String.valueOf(scor),xScor,600);
        }
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        playbtn.dispose();
    }
}

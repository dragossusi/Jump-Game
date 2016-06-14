package com.rachierudragos.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.rachierudragos.game.MyGame;

import java.util.HashMap;

/**
 * Created by Dragos on 10.06.2016.
 */
public class SettingsState extends State {
    public static final String[] numeMingi = {"ball", "blue", "green", "doge", "jake", "nazi", "rinnegan", "sasuke", "bleach", "fma", "derp", "bf", "butthead", "sir", "lenny", "yara", "yara2", "crazy", "mad", "pokerface", "romania", "siria"};
    private static final int POZITIE_MINGI = 325;
    private String name;
    private Texture background;
    private Texture nume;
    private Texture check;
    private Rectangle collideNume;
    private Rectangle collideMingi;
    private Circle collideCheck;
    private Preferences preferences;
    private HashMap<String, Texture> mingi;
    private HashMap<String, Float> pozitii;
    private int activ;

    protected SettingsState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, MyGame.WIDTH, MyGame.HEIGHT);
        preferences = Gdx.app.getPreferences("highscore");
        name = preferences.getString("nume", "Georgel");
        Gdx.input.setCatchBackKey(true);
        //texturi
        background = new Texture("bg.png");
        nume = new Texture("nume.png");
        check = new Texture("check.png");
        //collide texturi
        collideNume = new Rectangle(MyGame.WIDTH / 2 - nume.getWidth() / 2, 100, 114, 40);
        collideMingi = new Rectangle(0, POZITIE_MINGI, MyGame.WIDTH, POZITIE_MINGI + 75);
        collideCheck = new Circle();
        collideCheck.set(MyGame.WIDTH - 40, MyGame.HEIGHT - 40, 24);
        //mingile si pozitiile
        mingi = new HashMap<String, Texture>();
        pozitii = new HashMap<String, Float>();
        activ = find(numeMingi, preferences.getString("skin", "rsz_ball.png"));
        for (int i = 0; i < numeMingi.length; ++i) {
            mingi.put(numeMingi[i],
                    new Texture("rsz_" + numeMingi[i] + ".png"));
            pozitii.put(numeMingi[i], (float) (MyGame.WIDTH / 2 - 75 / 2 + 150 * (i - activ)));
        }
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            float clickX = Gdx.input.getX();
            float clickY = Gdx.input.getY();
            Vector3 input = new Vector3(clickX, clickY, 0);
            cam.unproject(input);
            if (collideNume.contains(input.x, input.y)) {
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        name = text;
                        Gdx.app.log("nume ", text);
                    }

                    @Override
                    public void canceled() {

                    }
                }, "Numele jucatorului:", "", name);
            } else if (collideMingi.contains(input.x, input.y)) {
                if (input.x > MyGame.WIDTH / 2 + 150 - 75 / 2
                        && pozitii.get(numeMingi[numeMingi.length - 1]) != MyGame.WIDTH / 2 - 75 / 2) {
                    //schimba spre stanga
                    for (int i = 0; i < numeMingi.length; ++i) {
                        pozitii.put(numeMingi[i], pozitii.get(numeMingi[i]) - 150);
                    }
                    ++activ;
                } else if (input.x < MyGame.WIDTH / 2 - 150 + 75 / 2
                        && pozitii.get(numeMingi[0]) != MyGame.WIDTH / 2 - 75 / 2) {
                    //schimba spre dreapta
                    for (int i = 0; i < numeMingi.length; ++i) {
                        pozitii.put(numeMingi[i], pozitii.get(numeMingi[i]) + 150);
                    }
                    --activ;
                }
            } else if (collideCheck.contains(input.x, input.y)) {
                preferences.putString("skin", "rsz_" + numeMingi[activ] + ".png").flush();
                preferences.putString("nume", name).flush();
                gsm.set(new MenuState(gsm));
                dispose();
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            gsm.set(new MenuState(gsm));
            dispose();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0, 0, MyGame.WIDTH, MyGame.HEIGHT);
        sb.draw(check, MyGame.WIDTH - 64, MyGame.HEIGHT - 64, 48, 48);
        sb.draw(nume, MyGame.WIDTH / 2 - nume.getWidth() / 2, 100);
        for (int i = 0; i < numeMingi.length; ++i) {
            sb.draw(mingi.get(numeMingi[i]), pozitii.get(numeMingi[i]), POZITIE_MINGI);
        }
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        nume.dispose();
        for (int i = 0; i < numeMingi.length; ++i) {
            mingi.get(numeMingi[i]).dispose();
        }
    }

    public int find(String[] array, String value) {
        for (int i = 0; i < array.length; i++)
            if (("rsz_" + array[i] + ".png").equals(value))
                return i;
        return -1;
    }

}

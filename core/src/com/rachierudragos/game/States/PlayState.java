package com.rachierudragos.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.rachierudragos.game.MyGame;
import com.rachierudragos.game.sprites.Ball;
import com.rachierudragos.game.sprites.Platforma;

/**
 * Created by Dragos on 19.05.2016.
 */
public class PlayState extends State {
    private static final int numarPlatforme = 7;
    private Ball ball;
    private Texture bg;
    private Texture ballTexture;
    private Texture Plat;
    private Texture cloudPlat;
    private Texture mdPlat;
    private Array<Platforma> platforme;
    private boolean poate = true;
    private Preferences preferences;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private float lastOne;

    protected PlayState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, MyGame.WIDTH, MyGame.HEIGHT);
        preferences = Gdx.app.getPreferences("highscore");
        bg = new Texture("oras.jpg");
        Plat = new Texture("plat.png");
        cloudPlat = new Texture("cloudplat.png");
        mdPlat = new Texture("platmovedes.png");
        ballTexture = new Texture(preferences.getString("skin", "rsz_ball.png"));
        platforme = new Array<Platforma>();
        for (int i = 1; i <= numarPlatforme; ++i) {
            platforme.add(new Platforma(i * 120));
        }
        ball = new Ball((int) platforme.get(0).getPozitie().x, 140);
        preferences.putBoolean("nou", false).flush();
        font = new BitmapFont(Gdx.files.internal("font.fnt"));
        glyphLayout = new GlyphLayout(font, String.valueOf(0));
        font.setColor(Color.CYAN);
        lastOne = 120;
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched() && poate == true) {
            ball.jump();
            poate = false;
            ball.setStopped(false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            gsm.set(new MenuState(gsm));
            dispose();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        ball.update(dt);
        if (ball.getPozitie().y > cam.position.y + 200)
            cam.position.y = ball.getPozitie().y - 200;
        for (Platforma plat : platforme) {
            if (cam.position.y - cam.viewportHeight / 2 > plat.getPozitie().y + 20) {
                plat.reposition(plat.getPozitie().y + 120 * numarPlatforme);
            }
            plat.update(dt);
            if (plat.collides(ball) && ball.getViteza().y < 0 && ball.getPozitie().y > plat.getPozitie().y) {
                if (plat.isDestroyed() == false) {
                    ball.jump();
                    if (plat.getType() != Platforma.MOVING)
                        plat.setDestroyed(true);
                    if (plat.getPozitie().y > lastOne) {
                        lastOne = plat.getPozitie().y;
                        glyphLayout.setText(font, String.valueOf((int) lastOne / 120 - 1));
                    }
                }
            }
        }
        if (ball.getPozitie().y < cam.position.y - 800) {
            int scor = preferences.getInteger("scor", 0);
            int rez = Math.max((int) lastOne / 120 - 1, scor);
            if (rez != scor) {
                preferences.putBoolean("nou", true);
            }
            preferences.putInteger("scor", rez);
            preferences.flush();
            gsm.set(new MenuState(gsm));
            dispose();
        }
        cam.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, 0, cam.position.y - cam.viewportHeight / 2, 480, 800);
        sb.draw(ballTexture, ball.getPozitie().x, ball.getPozitie().y);
        for (Platforma plat : platforme) {
            if (plat.isDestroyed() == false) {
                switch (plat.getType()) {
                    case Platforma.MOVING:
                        sb.draw(Plat, plat.getPozitie().x, plat.getPozitie().y, 100, 20);
                        break;
                    case Platforma.DESTROY:
                        sb.draw(cloudPlat, plat.getPozitie().x, plat.getPozitie().y, 100, 20);
                        break;
                    case Platforma.MOVEDESTROY:
                        sb.draw(mdPlat, plat.getPozitie().x, plat.getPozitie().y, 100, 20);
                        break;
                }
            }
        }
        font.draw(sb, String.valueOf((int) lastOne / 120 - 1), MyGame.WIDTH / 2 - glyphLayout.width / 2, cam.position.y + 350);
        sb.end();
        /*collides
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.circle(ball.getAaa().x, ball.getAaa().y, 75 / 2);
        shapeRenderer.setColor(Color.RED);
        for (Platforma plat : platforme) {
            shapeRenderer.rect(plat.getRectangle().x, plat.getRectangle().y, 100, 20);
        }
        shapeRenderer.end();
        */
    }

    @Override
    public void dispose() {
        bg.dispose();
        ballTexture.dispose();
        Plat.dispose();
        cloudPlat.dispose();
        mdPlat.dispose();
        font.dispose();
    }
}

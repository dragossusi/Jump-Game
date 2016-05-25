package com.rachierudragos.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.rachierudragos.game.MyGame;
import com.rachierudragos.game.sprites.Ball;
import com.rachierudragos.game.sprites.Platforma;

/**
 * Created by Dragos on 23.05.2016.
 */
public class DreamPlayState extends State {
    private static final int numarPlatforme = 7;
    private Ball ball;
    private Texture bg;
    private Array<Platforma> platforme;
    private boolean poate = true;
    private Preferences preferences;
    private int pauza = 3;
    //3-2-1 timp  pana incepe, 4 pierdut, 5 terminat
    private int scor;
    private Timer.Task asd;

    public DreamPlayState(GameStateManager gsm, boolean first) {
        super(gsm);
        ball = new Ball(50, 200);
        ball.setStopped(true);
        cam.setToOrtho(false, MyGame.WIDTH, MyGame.HEIGHT);
        bg = new Texture("oras.jpg");
        platforme = new Array<Platforma>();
        for (int i = 1; i <= numarPlatforme; ++i) {
            platforme.add(new Platforma(i * 120));
        }
        preferences = Gdx.app.getPreferences("highscore");
        preferences.putBoolean("nou", false).flush();
        asd = new Timer.Task() {
            @Override
            public void run() {
                --pauza;
                Gdx.app.log("pauza: ", "" + pauza);
            }
        };
        if (first == true)
            asdfg();
    }

    private void asdfg() {
        Timer.schedule(asd, 1, 1, 2);
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched() && poate == true) {
            ball.jump();
            poate = false;
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
            if (plat.collides(ball) && ball.getViteza().y < 0 && ball.getPozitie().y > plat.getPozitie().y)
                ball.jump();
        }
        if (ball.getPozitie().y < cam.position.y - 800) {
            scor = preferences.getInteger("scor", 0);
            int rez = Math.max((int) Math.floor(cam.position.y) - 400, scor);
            if (rez != scor) {
                preferences.putBoolean("nou", true);
            }
            preferences.putInteger("scor", rez);
            preferences.flush();
            //pierdu
            pauza = 4;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    pauza = 5;
                    gsm.set(new DreamPlayState(gsm, false));
                    dispose();
                }
            }, 1);
        }
        cam.update();
    }


    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, 0, cam.position.y - cam.viewportHeight / 2, 480, 800);
        sb.draw(ball.getBall(), ball.getPozitie().x, ball.getPozitie().y);
        for (Platforma plat : platforme) {
            sb.draw(plat.getPlatforma(), plat.getPozitie().x, plat.getPozitie().y, 100, 20);
        }
        if (pauza == 4) {
            BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"));
            font.setColor(Color.WHITE);
            int aux = scor;
            int xScor = 230;
            while (aux != 0) {
                xScor -= 12;
                aux /= 10;
            }
            font.draw(sb, String.valueOf(scor), xScor, 600);
            boolean nou = preferences.getBoolean("nou", false);
            if (nou)
                font.draw(sb, "New highscore!", 80, 700);

        } else if (pauza == 0) {
            ball.setStopped(false);
        } else if (pauza > 0 && pauza < 4) {
            BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"));
            font.setColor(Color.WHITE);
            font.draw(sb, String.valueOf(pauza), 230, 600);
        }
        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        ball.getBall().dispose();
        for (Platforma plat : platforme) {
            plat.getPlatforma().dispose();
        }
    }
}

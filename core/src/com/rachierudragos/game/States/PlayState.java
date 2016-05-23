package com.rachierudragos.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private Array<Platforma> platforme;
    private boolean poate = true;
    private ShapeRenderer shapeRenderer;

    protected PlayState(GameStateManager gsm) {
        super(gsm);
        ball = new Ball(50, 200);
        cam.setToOrtho(false, MyGame.WIDTH, MyGame.HEIGHT);
        bg = new Texture("oras.jpg");
        platforme = new Array<Platforma>();
        for (int i = 1; i <= numarPlatforme; ++i) {
            platforme.add(new Platforma(i * 120));
        }
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
            if (plat.collides(ball) && ball.getViteza().y < 0 && ball.getPozitie().y < plat.getPozitie().y)
                ball.jump();
        }
        if (ball.getPozitie().y < cam.position.y - 800) {
            Preferences preferences = Gdx.app.getPreferences("highscore");
            int scor = preferences.getInteger("scor",0);
            preferences.putInteger("scor", Math.max((int)Math.floor(cam.position.y) - 400,scor));
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
        sb.draw(ball.getBall(), ball.getPozitie().x, ball.getPozitie().y);
        for (Platforma plat : platforme) {
            sb.draw(plat.getPlatforma(), plat.getPozitie().x, plat.getPozitie().y, 100, 20);
        }
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
        ball.getBall().dispose();
        for (Platforma plat : platforme) {
            plat.getPlatforma().dispose();
        }
    }
}

package com.rachierudragos.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.rachierudragos.game.MyGame;
import com.rachierudragos.game.sprites.Ball;
import com.rachierudragos.game.sprites.DualBall;
import com.rachierudragos.game.sprites.Platforma;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Dragos on 19.05.2016.
 */
public class DualPlayState extends State {
    private static final int numarPlatforme = 7;
    String id;
    boolean conectat = false;
    DualBall dualBall;
    Texture dualBallTexture;
    private Ball ball;
    private Texture bg;
    private Array<Platforma> platforme;
    private boolean poate = true;
    private Preferences preferences;
    private Socket socket;

    protected DualPlayState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, MyGame.WIDTH, MyGame.HEIGHT);
        bg = new Texture("oras.jpg");
        platforme = new Array<Platforma>();
        for (int i = 1; i <= numarPlatforme; ++i) {
            platforme.add(new Platforma(i * 120));
        }
        ball = new Ball((int) platforme.get(0).getPozitie().x, 140);
        dualBallTexture = new Texture("rsz_ball.png");
        preferences = Gdx.app.getPreferences("highscore");
        preferences.putBoolean("nou", false).flush();
        connectSocket();
        configSocketEvents();
    }

    public void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", " Connected");
                dualBall = new DualBall(dualBallTexture);
            }
        }).on("socketID", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    id = data.getString("id");
                    Gdx.app.log("SocketIO", " My ID = " + id);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", String.valueOf(e));
                }
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    conectat = true;
                    id = data.getString("id");
                    Gdx.app.log("SocketIO", "New Player Connect: " + id);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting New PlayerID");
                }
            }
        });
    }

    private void connectSocket() {
        try {
            socket = IO.socket("http://localhost:8080");
            socket.connect();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched() && poate == true) {
            ball.jump();
            poate = false;
            ball.setStopped(false);
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
            int scor = preferences.getInteger("scor", 0);
            int rez = Math.max((int) Math.floor(cam.position.y) - 400, scor);
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
        if (dualBall != null) {
            Gdx.app.log("o ", "desenez " + ball.getPozitie());
            //da nu vrea s-o deseneze boolanjiul
            dualBall.draw(sb);
        }
        sb.draw(bg, 0, cam.position.y - cam.viewportHeight / 2, 480, 800);
        if (conectat == true)
            sb.draw(ball.getBall(), ball.getPozitie().x, ball.getPozitie().y);
        else {

        }
        for (Platforma plat : platforme) {
            sb.draw(plat.getPlatforma(), plat.getPozitie().x, plat.getPozitie().y, 100, 20);
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
        dualBallTexture.dispose();
    }
}

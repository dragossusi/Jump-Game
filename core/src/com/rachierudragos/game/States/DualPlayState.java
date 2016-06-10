package com.rachierudragos.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.rachierudragos.game.MyGame;
import com.rachierudragos.game.sprites.Ball;
import com.rachierudragos.game.sprites.Platforma;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Dragos on 19.05.2016.
 */
public class DualPlayState extends State {
    private static final int numarPlatforme = 7;
    private static final float UPDATE_TIME = 1 / 120f;
    private float timer;
    private String id;
    private HashMap<String, Ball> mingi;
    private HashMap<String, String> nume;
    private Ball ball;
    private Array<Platforma> platforme;
    private Texture bg;
    private Texture ballTexture;
    private Texture dualball;
    private boolean poate = true;
    private Preferences preferences;
    private Socket socket;
    private BitmapFont font;
    private BitmapFont font2;
    private GlyphLayout glyphLayout;
    private GlyphLayout glyphLayout2;
    private float lastOne;
    //private ShapeRenderer shapeRenderer;

    protected DualPlayState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, MyGame.WIDTH, MyGame.HEIGHT);
        cam.update();
        bg = new Texture("oras.jpg");
        ballTexture = new Texture("rsz_ball.png");
        dualball = new Texture("dualball.png");
        platforme = new Array<Platforma>();
        //platforme.add(new Platforma(120, 200));
        for (int i = 1; i <= numarPlatforme; ++i) {
            platforme.add(new Platforma(i * 120));
        }
        preferences = Gdx.app.getPreferences("highscore");
        //preferences.putBoolean("nou", false).flush();
        Gdx.input.setCatchBackKey(true);
        mingi = new HashMap<String, Ball>();
        nume = new HashMap<String, String>();
        font = new BitmapFont(Gdx.files.internal("fontsmaller.fnt"));
        font.setColor(Color.WHITE);
        font2 = new BitmapFont(Gdx.files.internal("font.fnt"));
        font2.setColor(Color.CYAN);
        glyphLayout = new GlyphLayout();
        glyphLayout2 = glyphLayout;
        lastOne = 120;
        glyphLayout2.setText(font, String.valueOf(0));
        connectSocket();
        configSocketEvents();
        //shapeRenderer = new ShapeRenderer();
    }

    public void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", " Connected");
                ball = new Ball((int) platforme.get(0).getPozitie().x, 140);
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
                    String playerId = data.getString("id");
                    String name = data.getString("name");
                    Gdx.app.log("SocketIO", "New Player Connect: " + id);
                    mingi.put(playerId, new Ball(ball.getPozitie().x, ball.getPozitie().y));
                    nume.put(playerId, name);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting New PlayerID");
                }
            }
        }).on("playerDisconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String playerId = data.getString("id");
                    mingi.remove(playerId);
                    nume.remove(playerId);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting New PlayerID");
                }
            }
        }).on("playerMoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String playerId = data.getString("id");
                    Double x = data.getDouble("x");
                    Double y = data.getDouble("y");
                    if (mingi.get(playerId) != null) {
                        mingi.get(playerId).setPozitie(x.floatValue(), y.floatValue());
                    }
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting New PlayerID");
                }
            }
        }).on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray objects = (JSONArray) args[0];
                try {
                    for (int i = 0; i < objects.length(); ++i) {
                        Vector2 position = new Vector2();
                        String playerId = objects.getJSONObject(i).getString("id");
                        position.x = ((Double) objects.getJSONObject(i).getDouble("x")).floatValue();
                        position.y = ((Double) objects.getJSONObject(i).getDouble("y")).floatValue();
                        //if (playerId != id) {
                        mingi.put(playerId, new Ball(position.y, position.x));
                        nume.put(playerId, objects.getJSONObject(i).getString("nume"));
                        //}
                        Gdx.app.log("pozitie", position.x + "  " + position.y);
                    }
                } catch (JSONException e) {
                    Gdx.app.log("GetPlayers error", String.valueOf(e));
                }
            }
        });
    }

    public void updateServer(float dt) {
        timer += dt;
        if (timer >= UPDATE_TIME) {
            JSONObject data = new JSONObject();
            try {
                data.put("x", ball.getPozitie().x);
                data.put("y", ball.getPozitie().y);
                socket.emit("playerMoved", data);
            } catch (JSONException e) {
                Gdx.app.log("SocketIO", "Error sending update data");
            }
        }
    }

    private void connectSocket() {
        try {
            socket = IO.socket("http://habarnuam-64071.onmodulus.net:80");
            //socket = IO.socket("http://localhost:8080");
            socket.connect();
            JSONObject data = new JSONObject();
            data.put("nume", preferences.getString("nume", ""));
            socket.emit("setName", data);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched() && poate == true && ball != null) {
            ball.jump();
            poate = false;
            ball.setStopped(false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            gsm.set(new MenuState(gsm));
            socket.disconnect();
            dispose();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        if (ball != null) {
            updateServer(dt);
            ball.update(dt);
            if (ball.getPozitie().y > cam.position.y + 200)
                cam.position.y = ball.getPozitie().y - 200;
            for (Platforma plat : platforme) {
                if (cam.position.y - cam.viewportHeight / 2 > plat.getPozitie().y + 20) {
                    plat.reposition(plat.getPozitie().y + 120 * numarPlatforme);
                }
                plat.update(dt);
                if (plat.collides(ball) && ball.getViteza().y < 0 && ball.getPozitie().y > plat.getPozitie().y - 5) {
                    ball.jump();
                    if (plat.getPozitie().y > lastOne) {
                        lastOne = plat.getPozitie().y;
                        glyphLayout2.setText(font, String.valueOf(lastOne / 120 - 1));
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
                socket.disconnect();
                dispose();
            }
        }
        cam.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, 0, cam.position.y - cam.viewportHeight / 2, 480, 800);

        //mingile

        for (Map.Entry<String, Ball> entry : mingi.entrySet()) {
            sb.draw(dualball, entry.getValue().getPozitie().x, entry.getValue().getPozitie().y);
            glyphLayout.setText(font, nume.get(entry.getKey()));
            font.draw(sb,
                    nume.get(entry.getKey()),
                    entry.getValue().getPozitie().x - glyphLayout.width / 2 + 75 / 2,
                    entry.getValue().getPozitie().y + 25);
        }
        if (ball != null)
            sb.draw(ballTexture, ball.getPozitie().x, ball.getPozitie().y);
        for (Platforma plat : platforme) {
            sb.draw(plat.getPlatforma(), plat.getPozitie().x, plat.getPozitie().y, 100, 20);
        }
        font2.draw(sb, String.valueOf((int) lastOne / 120 - 1), MyGame.WIDTH / 2 - glyphLayout.width / 2, cam.position.y + 350);
        sb.end();
        /*
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.CYAN);
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
        font.dispose();
        font2.dispose();
        for (Platforma plat : platforme) {
            plat.getPlatforma().dispose();
        }
    }
}

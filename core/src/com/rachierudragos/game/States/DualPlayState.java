package com.rachierudragos.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
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
import com.rachierudragos.game.sprites.SpongeBob;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Dragos on 19.05.2016.
 */
public class DualPlayState extends State {
    private static final int numarPlatforme = 7;
    private static final float UPDATE_TIME = (float) 1 / 10;
    private float timer;
    private String id;
    private HashMap<String, Ball> mingi;
    private HashMap<String, String> nume;
    private HashMap<String, String> skinuri;
    private HashMap<String, Texture> texturi;
    private Ball ball;
    private SpongeBob spongeBob;
    private Array<Platforma> platforme;
    private Texture bg;
    private Texture Plat;
    private Texture cloudPlat;
    private Texture mdPlat;
    private Texture ballTexture;
    private Texture spongeTexture;
    private Texture spongeMTexture;
    private Sound sound;
    private boolean poate = true;
    private Preferences preferences;
    private Random rand;
    private Socket socket;
    private BitmapFont font;
    private BitmapFont font2;
    private GlyphLayout glyphLayout;
    private GlyphLayout glyphLayout2;
    private float lastOne;
    private float nextSponge = 10 * 120;
    //private ShapeRenderer shapeRenderer;

    protected DualPlayState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, MyGame.WIDTH, MyGame.HEIGHT);
        cam.update();
        preferences = Gdx.app.getPreferences("highscore");
        rand = new Random();
        mingi = new HashMap<String, Ball>();
        nume = new HashMap<String, String>();
        skinuri = new HashMap<String, String>();
        texturi = new HashMap<String, Texture>();
        bg = new Texture("oras.jpg");
        spongeTexture = new Texture("rsz_spongebob.png");
        spongeMTexture = new Texture("rsz_spongebobm.png");
        Plat = new Texture("plat.png");
        cloudPlat = new Texture("cloudplat.png");
        mdPlat = new Texture("platmovedes.png");
        sound = Gdx.audio.newSound(Gdx.files.internal("jump.mp3"));
        ballTexture = new Texture(preferences.getString("skin", "rsz_ball.png"));
        platforme = new Array<Platforma>();
        for (int i = 1; i <= numarPlatforme; ++i) {
            platforme.add(new Platforma(i * 120));
        }
        //preferences.putBoolean("nou", false).flush();
        for (int i = 0; i < SettingsState.numeMingi.length; ++i) {
            texturi.put("rsz_" + SettingsState.numeMingi[i] + ".png", new Texture("rsz_" + SettingsState.numeMingi[i] + ".png"));
        }
        Gdx.input.setCatchBackKey(true);
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
                    String skin = data.getString("skin");
                    Gdx.app.log("SocketIO", "New Player Connect: " + id);
                    mingi.put(playerId, new Ball(ball.getPozitie().x, ball.getPozitie().y));
                    nume.put(playerId, name);
                    if (find(SettingsState.numeMingi, skin) != -1)
                        skinuri.put(playerId, skin);
                    else
                        skinuri.put(playerId, "rsz_ball.png");
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
                        mingi.put(playerId, new Ball(position.y, position.x));
                        nume.put(playerId, objects.getJSONObject(i).getString("nume"));
                        skinuri.put(playerId, objects.getJSONObject(i).getString("skin"));
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
            data.put("skin", preferences.getString("skin", "rsz_ball.png"));
            socket.emit("setName", data);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched() && poate == true && ball != null) {
            ball.jump();
            sound.play();
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
        //creare spongebob nou
        if (spongeBob != null) {
            spongeBob.update(dt);
            if (spongeBob.getPozitie().y + 75 < cam.position.y - 400) {
                spongeBob = null;
                nextSponge += 1000 + rand.nextInt(2000);
            }
        } else if (nextSponge < cam.position.y + 1000) {
            spongeBob = new SpongeBob(nextSponge);
        }
        if (ball != null) {
            updateServer(dt);
            ball.update(dt);
            //update platforme
            for (Platforma plat : platforme) {
                if (cam.position.y - cam.viewportHeight / 2 > plat.getPozitie().y + 20) {
                    plat.reposition(plat.getPozitie().y + 120 * numarPlatforme);
                }
                plat.update(dt);
            }
            //update camera
            if (ball.getPozitie().y > cam.position.y + 100)
                cam.position.y = ball.getPozitie().y - 100;
            //in caz ca viteza e negativa verifica coliziuni
            if (ball.getViteza().y < 0) {
                for (Platforma plat : platforme)
                    //mingea loveste o platforma
                    if (!poate)
                        if (ball.getPozitie().y > plat.getPozitie().y - 5 && plat.collides(ball)) {
                            if (plat.isDestroyed() == false) {
                                ball.jump();
                                sound.play();
                                if (plat.getType() != Platforma.MOVING) {
                                    plat.setDestroyed();
                                }
                                if (plat.getPozitie().y > lastOne) {
                                    lastOne = plat.getPozitie().y;
                                    glyphLayout2.setText(font, String.valueOf(lastOne / 120 - 1));
                                }
                        }
                    }
            }
            if (spongeBob != null)
                //mingea e deasupra la spongebob
                if (ball.getPozitie().y > spongeBob.getCollide().getY() + 65 && ball.getViteza().y < 0) {
                    if (spongeBob.collides(ball)) {
                        ball.jump();
                        sound.play();
                        spongeBob = null;
                        nextSponge += 1000 + rand.nextInt(2000);
                    }
                }
                //mingea e la nivelul lui
                else if (ball.getPozitie().y + 70 > spongeBob.getCollide().getY()) {
                    if (spongeBob.collides(ball)) {
                        poate = true;
                    }
                }
            if (ball.getPozitie().y < cam.position.y - 800) {
                pierdu();
            }
        }
        cam.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, 0, cam.position.y - cam.viewportHeight / 2, 480, 800);

        for (Map.Entry<String, Ball> entry : mingi.entrySet()) {
            sb.draw(texturi.get(skinuri.get(entry.getKey())),
                    entry.getValue().getPozitie().x,
                    entry.getValue().getPozitie().y);
            glyphLayout.setText(font, nume.get(entry.getKey()));
            font.draw(sb,
                    nume.get(entry.getKey()),
                    entry.getValue().getPozitie().x - glyphLayout.width / 2 + 75 / 2,
                    entry.getValue().getPozitie().y + 25);
        }
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
        if (spongeBob != null) {
            if (spongeBob.isMirrored())
                sb.draw(spongeMTexture, spongeBob.getPozitie().x, spongeBob.getPozitie().y, 127, 75);
            else
                sb.draw(spongeTexture, spongeBob.getPozitie().x, spongeBob.getPozitie().y, 127, 75);
        }
        if (ball != null)
            sb.draw(ballTexture, ball.getPozitie().x, ball.getPozitie().y);
        font2.draw(sb, String.valueOf((int) lastOne / 120 - 1), MyGame.WIDTH / 2 - glyphLayout.width / 2, cam.position.y + 350);
        sb.end();
        /*
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(spongeBob.getCollide().getX(), spongeBob.getCollide().y, 127, 75);
        shapeRenderer.end();
        */
    }

    @Override
    public void dispose() {
        bg.dispose();
        ballTexture.dispose();
        font.dispose();
        font2.dispose();
        Plat.dispose();
        cloudPlat.dispose();
        mdPlat.dispose();
        for (int i = 0; i < SettingsState.numeMingi.length; ++i) {
            texturi.get("rsz_" + SettingsState.numeMingi[i] + ".png").dispose();
        }
        sound.dispose();
    }

    public int find(String[] array, String value) {
        for (int i = 0; i < array.length; i++)
            if (("rsz_" + array[i] + ".png").equals(value))
                return i;
        return -1;
    }

    private void pierdu() {
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

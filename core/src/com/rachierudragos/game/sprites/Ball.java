package com.rachierudragos.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Dragos on 19.05.2016.
 */
public class Ball {
    private static final int GRAVITY = -30;
    private Vector3 pozitie;
    private Vector3 viteza;
    private Circle aaa;
    private Texture ball;
    private boolean stopped;

    public Ball(float x, float y) {
        pozitie = new Vector3(x, y, 0);
        viteza = new Vector3(0, 0, 0);
        ball = new Texture("rsz_ball.png");
        aaa = new Circle();
        aaa.set(pozitie.x, pozitie.y, 75 / 2);
        stopped = true;
    }

    public Vector3 getViteza() {
        return viteza;
    }

    public Vector3 getPozitie() {
        return pozitie;
    }

    public Texture getBall() {
        return ball;
    }

    public void update(float dt) {
        if (stopped == false) {
            if (pozitie.y > 0)
                viteza.add(0, GRAVITY, 0);
            viteza.scl(dt);
            pozitie.add(0, viteza.y, 0);
            float acceleration = Gdx.input.getAccelerometerX();
            if (Math.abs(acceleration) > 0.3f) {
                pozitie.x -= acceleration * 100 * dt;
            }
            if (pozitie.x > 480)
                pozitie.x -= 480;
            else if (pozitie.x < 0)
                pozitie.x += 480;
            viteza.scl(1 / dt);
        }
        aaa.setPosition(pozitie.x + 75 / 2, pozitie.y + 75 / 2);
    }

    public void jump() {
        viteza.y = 800;
    }

    public void setPozitie(float x, float y) {
        this.pozitie.x = x;
        this.pozitie.y = y;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public Circle getAaa() {
        return aaa;
    }
}

package com.rachierudragos.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private boolean stopped;

    public Ball(float x, float y) {
        pozitie = new Vector3(x, y, 0);
        viteza = new Vector3(0, 0, 0);
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

    public void update(float dt) {
        if (stopped == false) {
            if (pozitie.y > 0)
                viteza.add(0, GRAVITY, 0);
            viteza.scl(dt);
            pozitie.add(0, viteza.y, 0);
            switch (Gdx.app.getType()) {
                case Android:
                    float acceleration = Gdx.input.getAccelerometerX();
                    if (Math.abs(acceleration) > 0.3f) {
                        pozitie.x -= acceleration * 110 * dt;
                        checkPosition();
                    }
                    break;
                case Desktop:
                    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                        pozitie.x -= 400 * dt;
                        checkPosition();
                    } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                        pozitie.x += 400 * dt;
                        checkPosition();
                    }
            }

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

    private void checkPosition() {
        if (pozitie.x > 480)
            pozitie.x -= 555;
        else if (pozitie.x < -75)
            pozitie.x += 555;
    }
}

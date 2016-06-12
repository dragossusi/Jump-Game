package com.rachierudragos.game.sprites;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Created by Dragos on 19.05.2016.
 */
public class Platforma {
    public static final int MOVING = 1, DESTROY = 2, MOVEDESTROY = 3;
    private Vector2 pozitie;
    private float viteza;
    private Random rand;
    private Rectangle collision;
    private boolean stopped;
    private boolean destroyed;
    private int type;

    public Platforma(float y) {
        rand = new Random();
        int xx = rand.nextInt(380);
        pozitie = new Vector2(xx, y);
        collision = new Rectangle(xx, y, 120, 20);
        type = MOVING;
        stopped = true;
        destroyed = false;
    }

    public Vector2 getPozitie() {
        return pozitie;
    }

    public Rectangle getRectangle() {
        return collision;
    }

    public void update(float dt) {
        if (stopped == false) {
            pozitie.x += viteza * dt;
            if (pozitie.x > 380) {
                pozitie.x = 380;
                viteza = -viteza;
            } else if (pozitie.x < 0) {
                pozitie.x = 0;
                viteza = -viteza;
            }
        }
        collision.setPosition(pozitie.x, pozitie.y);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public void reposition(float y) {
        int xx = rand.nextInt(380);
        pozitie.set(xx, y);
        collision.setPosition(xx, y);
        if (y > 25 * 120 && y <= 50 * 120)
            setType(MOVING);
        else if (y > 50 * 120 && y <= 75 * 120) {
            setType(DESTROY);
        } else if (y > 75 * 120 && y <= 100 * 120) {
            setType(MOVEDESTROY);
        } else if (y > 100 * 120) {
            int x = rand.nextInt(100);
            if (x < 20) setType(MOVING);
            else if (x < 50) setType(DESTROY);
            else setType(MOVEDESTROY);
        }
    }

    public int getType() {
        return type;
    }

    private void setType(int tip) {
        switch (tip) {
            case MOVING:
                destroyed = false;
                stopped = false;
                type = MOVING;
                if (rand.nextBoolean())
                    viteza = rand.nextFloat() * 80;
                else
                    viteza = -rand.nextFloat() * 80;
                break;

            case DESTROY:
                stopped = true;
                destroyed = false;
                type = DESTROY;
                break;

            case MOVEDESTROY:
                destroyed = false;
                stopped = false;
                type = MOVEDESTROY;
                if (rand.nextBoolean())
                    viteza = rand.nextFloat() * 80 + 20;
                else
                    viteza = -rand.nextFloat() * 80 - 20;
                break;
        }
    }

    public boolean collides(Ball ball) {
        return (Intersector.overlaps(ball.getAaa(), collision));
    }
}

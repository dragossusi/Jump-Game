package com.rachierudragos.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Created by Dragos on 19.05.2016.
 */
public class Platforma {
    private Texture platforma;
    private Vector2 pozitie;
    private float viteza;
    private Random rand;
    private Rectangle collision;
    private boolean stopped;

    public Platforma(float y) {
        platforma = new Texture("platforma.png");
        rand = new Random();
        int xx = rand.nextInt(380);
        pozitie = new Vector2(xx, y);
        collision = new Rectangle(xx, y, 120, 20);
        stopped = true;
    }

    public Texture getPlatforma() {
        return platforma;
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

    public void reposition(float y) {
        int xx = rand.nextInt(380);
        pozitie.set(xx, y);
        collision.setPosition(xx, y);
        if (y > 800) {
            stopped = false;
            if (rand.nextBoolean())
                viteza = rand.nextFloat() * 80;
            else
                viteza = -rand.nextFloat() * 80;
        }
    }

    public boolean collides(Ball ball) {
        return (Intersector.overlaps(ball.getAaa(), collision));
    }
}

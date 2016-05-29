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
    private Random rand;
    private Rectangle collision;

    public Platforma(float y) {
        platforma = new Texture("platforma.png");
        rand = new Random();
        int xx = rand.nextInt(380);
        pozitie = new Vector2(xx, y);
        collision = new Rectangle(xx, y, 120, 20);

    }

    public Platforma(float y, float x) {
        platforma = new Texture("platforma.png");
        pozitie = new Vector2(x, y);
        collision = new Rectangle(x, y, 120, 20);
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

    public void reposition(float y) {
        int xx = rand.nextInt(380);
        pozitie.set(xx, y);
        collision.setPosition(xx, y);
    }

    public boolean collides(Ball ball) {
        return (Intersector.overlaps(ball.getAaa(), collision));
    }
}

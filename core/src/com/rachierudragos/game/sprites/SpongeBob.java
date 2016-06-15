package com.rachierudragos.game.sprites;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

/**
 * Created by Dragos on 14.06.2016.
 */
public class SpongeBob {
    private Vector3 pozitie;
    private Rectangle collide;
    private boolean stopped;
    private Random rand;
    private int timer;
    private boolean mirror;

    public SpongeBob(float y) {
        rand = new Random();
        int abc = rand.nextInt(1);

        pozitie = new Vector3(abc * 399 + 1, y, 0);
        if (abc == 1)
            mirror = false;
        else
            mirror = true;
        collide = new Rectangle(pozitie.x, pozitie.y, 127, 75);
        stopped = false;
        timer = 240;
    }

    public Rectangle getCollide() {
        return collide;
    }

    public Vector3 getPozitie() {
        return pozitie;
    }

    public boolean isMirrored() {
        return mirror;
    }

    public void update(float dt) {
        if (timer <= 0) {
            if (stopped == false) {
                stopped = true;
                timer = 60;
            } else {
                timer = 240;
                stopped = false;
                mirror = !mirror;
            }
        } else {
            timer -= dt;
            if (stopped == false) {
                if (mirror == true)
                    pozitie.x += dt * 80;
                else
                    pozitie.x -= dt * 80;
            }
        }
        collide.setPosition(pozitie.x, pozitie.y);
    }

    public boolean collides(Ball ball) {
        return (Intersector.overlaps(ball.getAaa(), collide));
    }
}

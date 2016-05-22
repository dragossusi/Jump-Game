package com.rachierudragos.game;

/**
 * Created by Dragos on 22.05.2016.
 */
import android.annotation.TargetApi;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidDaydream;

@TargetApi(17)
public class Daydream extends AndroidDaydream {
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        ApplicationListener app = new GameStandby();
        initialize(app, cfg);
    }
}
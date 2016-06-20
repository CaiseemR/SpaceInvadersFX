package com.tonyjs.spaceinvadersfx;

import javafx.scene.media.AudioClip;

/**
 * Created by tonysaavedra on 6/20/16.
 */
public class SoundEffect {
    private AudioClip soundEffect;

    public SoundEffect(String filePath) {
        soundEffect = new AudioClip(getClass().getResource(filePath).toExternalForm());
    }

    public void playClip() {
        soundEffect.play();
    }
}

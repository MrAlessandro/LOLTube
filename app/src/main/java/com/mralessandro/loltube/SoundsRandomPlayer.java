package com.mralessandro.loltube;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundsRandomPlayer {
    private final List<MediaPlayer> players;
    private int playerIndex;
    private MediaPlayer inPlay;

    protected SoundsRandomPlayer(Context context, Enableable enableable) {
        this.inPlay = null;
        this.players = new ArrayList<>();
        this.playerIndex = 0;

        this.players.add(MediaPlayer.create(context, R.raw.noise1));
        this.players.add(MediaPlayer.create(context, R.raw.noise2));
        this.players.add(MediaPlayer.create(context, R.raw.noise3));
        this.players.add(MediaPlayer.create(context, R.raw.noise4));

        for (MediaPlayer player : this.players) {
            player.setOnCompletionListener(mp -> {
                this.inPlay.seekTo(0);
                this.inPlay = null;
                enableable.enable();
            });
        }

    }

    protected void playRandom() throws IOException {
        this.inPlay = this.players.get(this.playerIndex);
        this.inPlay.start();
        this.playerIndex = (this.playerIndex + 1) % this.players.size();
    }

    protected void destroy() {
        for (MediaPlayer player : this.players) {
            player.stop();
            player.release();
        }
        this.players.clear();
    }
}

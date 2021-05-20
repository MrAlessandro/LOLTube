package com.mralessandro.loltube;

import java.util.Arrays;

public class SamplesBuffer {
    private static final int DIMENSION = 32;
    private final float[] buffer = new float[SamplesBuffer.DIMENSION];
    private int position;

    private float sum = 0;

    public SamplesBuffer(){
        Arrays.fill(this.buffer, (float) 0.0);
        this.position = 0;
    }

    protected void sample(float sample) {
        // Update sum
        this.sum -= this.buffer[position];
        this.buffer[position] = (float) (Math.round(sample * 100.0) / 100.0);
        this.sum += sample;

        if (position == SamplesBuffer.DIMENSION)
            System.out.println("Message");

        this.position += 1;
        this.position %= SamplesBuffer.DIMENSION;
    }


    protected float getAverage() {
        return (float) this.sum / SamplesBuffer.DIMENSION;
    }
}

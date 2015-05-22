package com.example.lvniqi.multimeter.Audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by lvniqi on 2015-05-16.
 */
public class AudioSender implements Runnable {
    protected AudioTrack audioTrack;
    private double frequency = 480;
    protected Thread thread;
    protected int size = 0;
    protected static byte[] sinData;
    public AudioSender(){
        //构造正弦函数
        if(sinData == null) {
            sinData = new byte[4096];
            for (int i = 0; i < 4096; i++) {
                sinData[i] = (byte) Math.round(Math.sin(2.0 * Math.PI / 4096.0 * i) * 127 + 128);
            }
        }
    }
    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    // Start
    public void start() {
        thread = new Thread(this, "AudioReceiver");
        thread.start();
    }

    public void stop() {
        Thread t = thread;
        thread = null;

        // Wait for the thread to exit

        while (t != null && t.isAlive())
            Thread.yield();
    }

    public void run() {
        processAudio();
    }

    protected void processAudio() {
        byte buffer[];
        int rate =
                AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
        int minSize =
                AudioTrack.getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_8BIT);

        // Find a suitable buffer size
        final int n = (int) (rate / frequency);
        size = (minSize / n + 1) * n;
        Log.i("minSize", "" + minSize);
        Log.i("size", "" + size);
        Log.i("rate", "" + rate);
        Log.i("frequency", "" + frequency);
        // Create the AudioSender track
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, rate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                size, AudioTrack.MODE_STREAM);
        // Check audiotrack
        if (audioTrack == null) {
            return;
        }

        // Check state
        int state = audioTrack.getState();
        if (state != AudioTrack.STATE_INITIALIZED) {
            audioTrack.release();
            return;
        }

        audioTrack.play();

        // Create the buffer
        buffer = new byte[2*size];

        // Initialise the generator variables
        //现在数据在正弦函数中的位置
        float pos = 0;
        while (thread != null) {
            Loop(rate, buffer, pos);
        }
        audioTrack.stop();
        audioTrack.release();
    }
    void Loop(int rate,byte [] buffer,float pos){
        float t_n = (float)(rate / frequency);
        //步进
        float x = 4096 / t_n;
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = sinData[(int)pos];
            pos = (pos + x) % 4096;
        }
        audioTrack.write(buffer, 0, buffer.length);
    }
}

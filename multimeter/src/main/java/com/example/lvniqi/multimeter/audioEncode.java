package com.example.lvniqi.multimeter;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by lvniqi on 2015-05-16.
 */
public class audioEncode implements Runnable {
    private AudioTrack audioTrack;
    private double frequency = 480;
    private Thread thread;

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getFrequency() {
        return frequency;
    }

    // Start
    protected void start() {
        thread = new Thread(this, "Audio");
        thread.start();
    }
    protected void stop(){
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
        //构造正弦函数
        byte[] sinData = new byte[4096];
        for(int i=0;i<4096;i++){
            sinData[i] = (byte)Math.round(Math.sin(2.0 * Math.PI/4096.0*i) * 127+128);
        }
        byte buffer[];
        int rate =
                AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
        int minSize =
                AudioTrack.getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_8BIT);

        // Find a suitable buffer size
        int size = 0;
        final int n = (int)(rate/frequency);
        size = (minSize/n+1)*n;
        Log.i("minSize",""+minSize);
        Log.i("size",""+size);
        Log.i("rate",""+rate);
        Log.i("frequency",""+frequency);
        // Create the audio track
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
        buffer = new byte[size];

        // Initialise the generator variables
        //现在数据在正弦函数中的位置
        int pos = 0;
        while (thread != null) {
            int t_n = (int)(rate/frequency);
            //步进
            int x = 4096/t_n;
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = sinData[pos];
                pos = (pos+x)%4096;
            }
            audioTrack.write(buffer, 0, buffer.length);
        }
        audioTrack.stop();
        audioTrack.release();
    }
}

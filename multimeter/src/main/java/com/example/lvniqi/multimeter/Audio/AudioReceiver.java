package com.example.lvniqi.multimeter.Audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

import com.example.lvniqi.multimeter.MainActivity;

/**
 * Created by lvniqi on 2015-05-21.
 */
public class AudioReceiver implements Runnable {

    protected static final int FRAMES = 4096;
    protected int input;
    protected int sample;
    // Data
    protected Thread thread;
    protected short data[];
    protected AudioRecord audioRecord;
    protected short buffer[];

    // Constructor
    public AudioReceiver() {
        buffer = new short[FRAMES];
    }

    // Start AudioSender
    public void start() {
        // Start the thread

        thread = new Thread(this, "AudioReceiver");
        thread.start();
    }

    // Run
    @Override
    public void run() {
        processAudio();
    }

    // Stop
    public void stop() {
        Thread t = thread;
        thread = null;

        // Wait for the thread to exit

        while (t != null && t.isAlive())
            Thread.yield();
    }

    // Process AudioReceiver

    protected void processAudio() {
        // Assume the output sample will work on the input as
        // there isn't an AudioRecord.getNativeInputSampleRate()

        sample =
                AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
        Log.i("sample", sample + "");
        // Get buffer size
        int size =
                AudioRecord.getMinBufferSize(sample,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
        // Give up if it doesn't work
        if (size == AudioRecord.ERROR_BAD_VALUE ||
                size == AudioRecord.ERROR ||
                size <= 0) {
            thread = null;
            return;
        }

        // Create the AudioRecord object
        audioRecord =
                new AudioRecord(input, sample,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        size);

        // Check audiorecord
        if (audioRecord == null) {
            thread = null;
            return;
        }

        // Check state
        int state = audioRecord.getState();

        if (state != AudioRecord.STATE_INITIALIZED) {

            audioRecord.release();
            thread = null;
            return;
        }

        // Start recording
        audioRecord.startRecording();
        // Continue until the thread is stopped
        while (thread != null) {
            Loop();
        }

        // Stop and release the AudioSender recorder
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
        }
    }

    protected void Loop() {
        // Read a buffer of data
        int size = audioRecord.read(buffer, 0, FRAMES);
        data = new short[size];
        // Stop the thread if no data
        if (size == 0) {
            thread = null;
        }
        System.arraycopy(buffer, 0, data, 0, size);
        short[] temp = new short[100];
        int pos_1 = 0, pos_2 = 0;
        pos_1 = find_posedge(data, 0);
        if (pos_1 != -1) {
            for (int i = pos_1, j = 0; j < 100 && i < data.length; j++, i++) {
                temp[j] = data[i];
            }
            //if(isTypical(data,pos_1,pos_1+5))
            {
                new MainActivity.GraphCardTask().execute(temp);
            }
        }
    }

    protected int find_posedge(short[] data, int start) {
        int pos;
        for (int i = start; i < data.length - 1; i++) {
            if (data[i] <= 0 && data[i + 1] > 0) {
                pos = i;
                return pos;
            }
        }
        return -1;
    }

    protected int find_posedge_reverse(short[] data, int end) {
        int pos;
        for (int i = end - 1; i > 0; i--) {
            if (data[i] <= 0 && data[i + 1] > 0) {
                pos = i;
                return pos;
            }
        }
        return -1;
    }

    protected boolean isTypical(short[] data, int start, int end) {
        boolean result = false;
        if (end - start > 50) {
            return false;
        }
        for (int max = 0, i = start; i < end; i++) {
            if (data[i] > max) {
                max = data[i];
            }
            if (max > 1000) {
                result = true;
                break;
            }
        }
        return result;
    }
}

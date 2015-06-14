package com.example.lvniqi.multimeter.Audio;

import android.content.Context;
import android.util.Log;

import com.example.lvniqi.multimeter.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lvniqi on 2015-06-14.
 */
public class Mcu_Updater implements Runnable {
    protected Thread thread;
    private AudioReceiver audioReceiver = new McuReceiver();
    private AudioSender audioSender = new AudioEncoder();
    private ArrayList<Byte[]> binArray = new ArrayList(50);

    public Mcu_Updater(Context context) {
        if (MainActivity.audioSender != null) {
            MainActivity.audioSender.stop();
            MainActivity.audioSender = null;
        }
        if (MainActivity.audioReceiver != null) {
            MainActivity.audioReceiver.stop();
            MainActivity.audioReceiver = null;
        }
        File binFilesDir = context.getDir("binFiles", Context.MODE_PRIVATE);
        binFilesDir.length();
        String path = binFilesDir.getAbsolutePath();
        Log.i("binFilesDir", "path:" + path);
        File file = new File(path + "/1.bin");
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] t = new byte[1024 + 4];
            int length = 0;
            int result = 0;
            while (true) {
                result = in.read(t, 0, 1024);
                if (result != -1) {
                    length += result;
                    Byte[] t2 = new Byte[1024 + 4];
                    for (int x = 0; x < result; x++) {
                        t2[x] = t[x];
                    }
                    for (int x = result; x < 1024; x++) {
                        t2[x] = (byte) 0xff;
                    }
                    for (int x = 1024; x < 1024 + 4; x++) {
                        t2[x] = (byte) 0;
                    }
                    binArray.add(t2);
                } else {
                    break;
                }
            }
            Log.i("length:", length + "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Stop
    public void stop() {
        audioReceiver.stop();
        audioSender.stop();
        Thread t = thread;
        thread = null;
        // Wait for the thread to exit
        while (t != null && t.isAlive())
            Thread.yield();
    }

    // Start AudioSender
    public void start() {
        // Start the thread
        thread = new Thread(this, "McuReceiver");
        thread.start();
    }

    @Override
    public void run() {
        updateFireWare();
    }

    private void updateFireWare() {
        audioReceiver.start();
        audioSender.start();
        if (binArray.size() > 0) {
            ((AudioEncoder) audioSender).addDatas(binArray.get(0));
        }
        while (thread != null) {
            try {
                thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class McuReceiver extends AudioDecoder {
        ArrayList<Byte> data = new ArrayList<>(500);
        //数据锁
        private ReentrantLock lock = new ReentrantLock();

        @Override
        protected void dataProcress(byte[] t) {
            lock.lock();
            if (t.length < 500) {
                for (byte x : t) {
                    data.add(x);
                }
            }
            lock.unlock();
        }

        int getByte() {
            lock.lock();
            if (data.size() > 0) {
                byte t = data.get(0);
                data.remove(0);
                lock.unlock();
                return 0xff & t;
            }
            lock.unlock();
            return -1;
        }
    }
}

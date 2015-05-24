package com.example.lvniqi.multimeter.Audio;

import android.util.Log;

import com.example.lvniqi.multimeter.MainActivity;

import java.util.ArrayList;

/**
 * Created by lvniqi on 2015-05-21.
 */
public class AudioDecoder extends AudioReceiver {
    static int count_1, count_2;
    private ArrayList<Integer> lastSave = new ArrayList<Integer>(FRAMES);
    private int saveLen = 0;
    private String tostring = new String();
    @Override
    protected void Loop() {
        // Read a buffer of data
        int size = audioRecord.read(buffer, 0, FRAMES);
        data = new short[size + saveLen];
        // Stop the thread if no data
        if (size == 0) {
            thread = null;
        }
        for (int i = 0; i < saveLen; i++) {
            data[i] = (short) (int) lastSave.get(i);
        }
        System.arraycopy(buffer, 0, data, saveLen, size);
        ArrayList<Integer> temp = getDatasFrimPcm(data);

        if (temp != null) {
            /*for (Integer x : temp) {
                if (x == 255)
                    count_2++;
                else {
                    count_1++;
                    Log.i("ERROR", x + "");
                }
            }*/

            byte[] t = new byte[temp.size()];
            for (int i = 0; i < temp.size(); i++) {
                t[i] = (byte) (int) temp.get(i);
                Log.i("ERROR", temp.get(i) + "");
            }
            tostring += new String(t);
        }
        //new MainActivity.DecoderCardTask().execute("count_1:" + count_1 + "count_2:" + count_2);
        new MainActivity.DecoderCardTask().execute(tostring);
    }

    /**
     * 返回数据开始值
     * -1为未找到
     *
     * @param data
     * @param start
     * @return
     */
    int prepare(short[] data, int start) {
        int pos_1 = 0, pos_2 = 0;
        int count = 0;
        pos_1 = find_posedge(data, start);
        while (true) {
            pos_2 = find_posedge(data, pos_1 + 1);
            if (pos_1 == -1 || pos_2 == -1) {
                return -1;
            }
            if (isTypical(data, pos_1, pos_2)
                    && (pos_2 - pos_1 == 4 ||
                    pos_2 - pos_1 == 3)) {
                return pos_2;
            }
            pos_1 = pos_2;
        }
    }

    int getDataFromPcm(short[] data, int start) {
        int pos_1 = 0, pos_2 = 0, pos_3 = 0;
        pos_1 = find_posedge(data, start);
        if (pos_1 == -1)
            return -1;
        pos_2 = find_posedge(data, pos_1 + 1);
        if (pos_2 == -1)
            return -1;
        pos_3 = find_posedge(data, pos_2 + 1);
        if (pos_3 == -1)
            return -1;
        if (pos_2 - pos_1 < 6 || pos_3 - pos_2 < 6)
            return -1;
        return ((pos_2 - pos_1 - 6) << 4) + (pos_3 - pos_2 - 6);
    }

    ArrayList<Integer> getDatasFrimPcm(short[] data) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        int start, last_end = -1;
        start = prepare(data, 0);
        if (start == -1) {
            saveLen = 0;
            return null;
        }
        while (true) {
            int temp = getDataFromPcm(data, start);
            if (temp != -1) {
                result.add(temp);
                last_end = start + 12 + temp / 16 + temp % 16;
                start = prepare(data, start);
                if (start == -1) {
                    break;
                }
            } else {
                break;
            }


        }
        //int savepos = find_posedge_reverse(data,data.length-1);
        int savepos;
        if (last_end != -1) {
            savepos = last_end;
        } else {
            savepos = data.length;
        }
        lastSave.clear();
        saveLen = data.length - savepos;
        if (savepos != -1) {
            for (int i = savepos; i < data.length; i++) {
                lastSave.add((int) data[i]);
            }
        }

        return result;
    }
}

package com.example.lvniqi.multimeter.Audio;

import android.util.Log;

import com.example.lvniqi.multimeter.MainActivity;

import java.util.ArrayList;

import static com.example.lvniqi.multimeter.Audio.AudioEncoder.END_DIV;
import static com.example.lvniqi.multimeter.Audio.AudioEncoder.START_DIV;

/**
 * Created by lvniqi on 2015-05-21.
 */
public class AudioDecoder extends AudioReceiver {
    static int count_1, count_2;
    private ArrayList<Short> lastSave = new ArrayList<Short>(FRAMES);
    private int saveLen = 0;
    private boolean isStart = false;
    private String tostring = new String();
    private ArrayList<Byte> savedArrayData = null;
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
        savedArrayData = getDatasFrimPcm(data, savedArrayData);
        if (!isStart) {
            if (savedArrayData != null) {
            /*for (Integer x : temp) {
                if (x == 255)
                    count_2++;
                else {
                    count_1++;
                    Log.i("ERROR", x + "");
                }
            }*/

                byte[] t = new byte[savedArrayData.size()];
                for (int i = 0; i < savedArrayData.size(); i++) {
                    t[i] = savedArrayData.get(i);
                    Log.i("ERROR", savedArrayData.get(i) + "");
                }
                tostring = new String(t);
            }
            savedArrayData = null;
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
        pos_1 = find_posedge(data, start);
        while (true) {
            pos_2 = find_posedge(data, pos_1 + 1);
            if (pos_1 < 0 || pos_2 < 0) {
                return -1;
            }
            if (isTypical(data, pos_1, pos_2)
                    && (pos_2 - pos_1 == 4 ||
                    pos_2 - pos_1 == 5)) {
                return pos_2;
            }
            pos_1 = pos_2;
        }
    }

    int getDataFromPcm(short[] data, int start, boolean isStart) {
        int pos_1, pos_2, pos_3;

        float pos_1_real, pos_2_real, pos_3_real;
        if (isStart) {
            pos_1 = start;
            pos_1_real = start;
        } else {
            pos_1 = find_posedge(data, start);
            if (pos_1 < 0)
                return -1;
            else
                pos_1_real = pos_1 + (-data[pos_1]) / (data[pos_1 + 1] - data[pos_1]);
        }
        pos_2 = find_posedge(data, pos_1 + 1);
        if (pos_2 < 0)
            return -1;
        else
            pos_2_real = pos_2 + (-data[pos_2]) / (data[pos_2 + 1] - data[pos_2]);
        pos_3 = find_posedge(data, pos_2 + 1);
        if (pos_3 < 0)
            return -1;
        else
            pos_3_real = pos_3 + (-data[pos_3]) / (data[pos_3 + 1] - data[pos_3]);
        if (pos_2 - pos_1 < START_DIV || pos_3 - pos_2 < START_DIV)
            return -1;
        if (pos_2 - pos_1 > START_DIV + 16 || pos_3 - pos_2 > START_DIV + 16)
            return -1;
        return (((Math.round(pos_2_real - pos_1_real) - START_DIV) << 4) +
                (Math.round(pos_3_real - pos_2_real) - START_DIV));
    }

    ArrayList<Byte> getDatasFrimPcm(short[] data, ArrayList<Byte> last) {
        ArrayList<Byte> result;
        if (last != null) {
            result = last;
        } else {
            result = new ArrayList<Byte>();
        }
        int start, last_end = -1;
        if (!isStart) {
            start = prepare(data, 0);
            if (start < 0) {
                saveLen = 0;
                return null;
            } else {
                isStart = true;
            }
        } else {
            start = find_posedge(data, 0);
        }
        while (true) {
            int temp = getDataFromPcm(data, start, false);
            if (temp >= 0) {
                result.add((byte) temp);
                last_end = start + START_DIV * 2 + temp / 16 + temp % 16;
                start = last_end;
                //start = prepare(data, start);
                if (start < 0) {
                    break;
                } else {
                    last_end = start;
                }
            } else {
                break;
            }

        }
        //int saveOos = find_posedge_reverse(data,data.length-1);
        int savePos;
        if (last_end >= 0 && data.length - last_end < 15 * 2 + START_DIV * 2 + END_DIV) {
            savePos = last_end;
        } else {
            savePos = data.length;
            isStart = false;
        }
        lastSave.clear();
        saveLen = data.length - savePos;
        if (savePos >= 0) {
            for (int i = savePos; i < data.length; i++) {
                lastSave.add(data[i]);
            }
        }

        return result;
    }
}

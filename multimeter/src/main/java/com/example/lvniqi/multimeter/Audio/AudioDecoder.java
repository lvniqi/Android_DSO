package com.example.lvniqi.multimeter.Audio;

import com.example.lvniqi.multimeter.MainActivity;

import java.util.ArrayList;

import static com.example.lvniqi.multimeter.Audio.AudioEncoder.DIV_RANGE;
import static com.example.lvniqi.multimeter.Audio.AudioEncoder.DIV_STEP;
import static com.example.lvniqi.multimeter.Audio.AudioEncoder.DIV_TOLERANCE;
import static com.example.lvniqi.multimeter.Audio.AudioEncoder.END_DIV;
import static com.example.lvniqi.multimeter.Audio.AudioEncoder.PREPARE_DIV;
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
                byte[] t = new byte[savedArrayData.size()];
                for (int i = 0; i < savedArrayData.size(); i++) {
                    t[i] = savedArrayData.get(i);
                }
                dataProcress(t);
            }
            savedArrayData = null;
        }
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
        int pos_1, pos_2;
        pos_1 = find_posedge(data, start);
        while (true) {
            pos_2 = find_posedge(data, pos_1 + 1);
            if (pos_1 < 0 || pos_2 < 0) {
                return -1;
            }
            if (isTypical(data, pos_1, pos_2)
                    && (pos_2 - pos_1 == PREPARE_DIV - 1 ||
                    pos_2 - pos_1 == PREPARE_DIV ||
                    pos_2 - pos_1 == PREPARE_DIV + 1)) {
                return pos_2;
            }
            pos_1 = pos_2;
        }
    }

    int getDataFromPcm(short[] data, int start, boolean isStart) {
        int[] pos = new int[DIV_RANGE + 1];
        float[] pos_real = new float[DIV_RANGE + 1];
        if (isStart) {
            pos[0] = start;
            pos_real[0] = start;
        } else {
            pos[0] = find_posedge(data, start);
            if (pos[0] < 0)
                return -1;
            else
                pos_real[0] = pos[0] + (-data[pos[0]]) / (data[pos[0] + 1] - data[pos[0]]);
        }
        for (int i = 1; i < DIV_RANGE + 1; i++) {
            pos[i] = find_posedge(data, pos[i - 1] + 1);
            if (pos[i] < 0)
                return -1;
            else
                pos_real[i] = pos[i] + (-data[pos[i]]) / (data[pos[i] + 1] - data[pos[i]]);
            if (pos[i] - pos[i - 1] < START_DIV - DIV_TOLERANCE)
                return -1;
            if (pos[i] - pos[i - 1] > START_DIV + (DIV_RANGE - 1) * (DIV_STEP) + DIV_TOLERANCE)
                return -2;
        }
        int temp = 0;
        for (int i = DIV_RANGE; i > 0; i--) {
            temp *= 4;
            temp += (Math.round(pos_real[i] - pos_real[i - 1] - START_DIV) + 1) / DIV_STEP;
        }
        /*if(temp ==100) {
            short[] temp_show = new short[150];
            for (int i = pos[0]-25>0?pos[0]-25:0, j = 0; j < 150 && i < data.length; j++, i++) {
                temp_show[j] = data[i];
            }
            //if(isTypical(data,pos_1,pos_1+5))
            {
                new MainActivity.GraphCardTask().execute(temp_show);
            }
        }*/
        return temp;
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
                int temp_temp = temp;
                last_end = start;
                for (int i = 0; i < DIV_RANGE; i++) {
                    last_end += START_DIV + ((temp_temp % DIV_RANGE) * DIV_STEP) - 1;
                    temp_temp /= DIV_RANGE;
                }
                last_end = find_posedge(data, last_end);
                start = last_end;
            } else {
                if (temp == -2) {
                    isStart = false;
                }
                break;
            }

        }
        //int saveOos = find_posedge_reverse(data,data.length-1);
        int savePos;
        if (last_end >= 0 && data.length - last_end <
                (DIV_RANGE) * ((DIV_RANGE - 1) * DIV_STEP + START_DIV) + END_DIV) {
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

    protected void dataProcress(byte[] t) {
        if (t != null) {
            tostring = new String(t);
            new MainActivity.DecoderCardTask().execute(tostring);
        }
    }
}
package com.example.lvniqi.multimeter.Audio;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lvniqi on 2015-05-21.
 */

public class AudioEncoder extends AudioSender {
    static final int PREPARE_DIV = 4;
    static final int START_DIV = 7;
    static final int DIV_RANGE = 4;
    static final int DIV_STEP = 3;
    static final int DIV_TOLERANCE = (DIV_STEP - 1) / 2;
    static final int END_DIV = 20;
    public boolean isFinish;
    protected int buffer_index = 0;
    //数据锁
    private ReentrantLock lock = new ReentrantLock();
    private ArrayList<BaseData> datas = new ArrayList<BaseData>();

    /**
     * 先使用6~21分频
     * 共16个段
     *
     * @param data
     * @return
     */
    byte[] setPcm(int data, boolean isPrepare, boolean isEnd) {

        int[] data_len = new int[DIV_RANGE];
        int sum = 0;
        {
            int data_temp = data;
            for (int i = 0; i < DIV_RANGE; i++) {
                int len = START_DIV + (data_temp % 4) * DIV_STEP;
                sum += len;
                data_len[i] = len;
                data_temp /= 4;
            }
        }
        byte[] result;
        int i = 0;
        if (isPrepare) {
            if (isEnd) {
                result = new byte[PREPARE_DIV + sum + END_DIV];
            } else {
                result = new byte[PREPARE_DIV + sum];
            }
            byte[] prepare = prepare();
            for (int count = 0; count < PREPARE_DIV; count++, i++) {
                result[i] = prepare[count];
            }
        } else if (isEnd) {
            result = new byte[sum + END_DIV];
        } else {
            result = new byte[sum];
        }
        for (int j = 0; j < DIV_RANGE; j++) {
            byte[] sinData = getDiv(data_len[j]);
            for (int count = 0; count < data_len[j]; count++, i++) {
                result[i] = sinData[count];
            }
        }
        if (isEnd) {
            byte[] end = getDiv(END_DIV);
            for (int count = 0; count < END_DIV; count++, i++) {
                result[i] = end[count];
            }
        }
        return result;
    }

    /**
     * 准备阶段
     * 如果开始发一帧需要这样做
     * 第一个为分频4后的结果(12k)
     *
     * @return
     */
    byte[] prepare() {
        byte[] result = new byte[PREPARE_DIV];
        byte[] in = getDiv(PREPARE_DIV);
        for (int i = 0; i < PREPARE_DIV; i++) {
            result[i] = in[i];
        }
        return result;
    }

    /**
     * 输入分频数
     * 返回正弦表
     */
    byte[] getDiv(int div) {
        byte[] result = new byte[div];
        for (int i = 0; i < div; i++) {
            result[i] = sinData[(4096 * i / div) % 4096];
        }
        return result;
    }

    @Override
    void Loop(int rate, byte[] buffer, float pos) {
        lock.lock();
        int dsize = datas.size();
        lock.unlock();
        if (dsize != 0) {
            boolean isFirst = true;
            boolean isEnd = false;
            lock.lock();
            BaseData data = datas.get(0);
            if (data.isContinue()) {
                isFirst = false;
            }
            while (data.getSize() != 0 && buffer_index < size * 2 - 200) {
                if (data.getSize() == 1) {
                    isEnd = true;
                }
                byte[] buffer_temp = setPcm(data.get(), isFirst, isEnd);
                //byte[] buffer_temp = setPcm(data.get(), true,false);

                isFirst = false;
                for (int i = 0; i < buffer_temp.length; i++, buffer_index++) {
                    buffer[buffer_index] = buffer_temp[i];
                }
            }
            if (data.getSize() == 0) {
                datas.remove(0);
            }
            while (buffer_index < size) {
                buffer[buffer_index++] = 127;
            }
            lock.unlock();
        } else {
            while (buffer_index < size) {
                buffer[buffer_index++] = 127;
            }
        }
        audioTrack.write(buffer, 0, buffer_index);
        buffer_index = 0;
    }

    public void addData(int in) {
        lock.lock();
        datas.add(new BaseData(in));
        lock.unlock();
    }

    public void addDatas(byte[] a) {
        lock.lock();
        datas.add(new BaseData(a));
        lock.unlock();
    }

    public void addDatas(Byte[] a) {
        lock.lock();
        datas.add(new BaseData(a));
        lock.unlock();
    }

    public void addDatas(String a) {
        lock.lock();
        datas.add(new BaseData(a));
        lock.unlock();
    }
}

class BaseData {
    private ArrayList<Byte> arrayList;
    private boolean isContinue = false;
    BaseData(byte in) {
        arrayList = new ArrayList<>();
        arrayList.add(in);
    }

    BaseData(int in) {
        arrayList = new ArrayList<>();
        arrayList.add((byte) ((0xff00 & in) >> 8));
        arrayList.add((byte) (0x00ff & in));
    }

    BaseData(byte[] in) {
        arrayList = new ArrayList<>();
        for (byte x : in) {
            arrayList.add(x);
        }
    }

    BaseData(Byte[] in) {
        arrayList = new ArrayList<>();
        for (Byte x : in) {
            arrayList.add(x);
        }
    }
    BaseData(String in) {
        arrayList = new ArrayList<>();
        byte[] in_t = in.getBytes();
        for (byte x : in_t) {
            arrayList.add(x);
        }
    }

    public boolean isContinue() {
        return isContinue;
    }

    int getSize() {
        return arrayList.size();
    }

    int get() {
        int result = 0xff & arrayList.get(0);
        arrayList.remove(0);
        return result;
    }
}
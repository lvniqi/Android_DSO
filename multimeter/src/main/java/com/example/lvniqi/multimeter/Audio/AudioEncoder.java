package com.example.lvniqi.multimeter.Audio;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lvniqi on 2015-05-21.
 */

public class AudioEncoder extends AudioSender {
    static final int PREPARE_DIV = 5;
    static final int START_DIV = 6;
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
    byte[] setPcm(int data, boolean isPrepare) {
        int data_low = data % 16;
        int len_low = data_low + START_DIV;
        int data_high = data / 16;
        int len_high = data_high + START_DIV;
        byte[] result;
        int i = 0;
        if (isPrepare) {
            result = new byte[PREPARE_DIV + len_low + len_high];
            byte[] prepare = prepare();
            for (int count = 0; count < PREPARE_DIV; count++, i++) {
                result[i] = prepare[count];
            }
        } else {
            result = new byte[len_low + len_high];
        }
        byte[] high = getDiv(len_high);
        for (int count = 0; count < len_high; count++, i++) {
            result[i] = high[count];
        }
        byte[] low = getDiv(len_low);
        for (int count = 0; count < len_low; count++, i++) {
            result[i] = low[count];
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
            lock.lock();
            BaseData data = datas.get(0);
            while (data.getSize() != 0 && buffer_index < size * 2 - 200) {
                //byte[] buffer_temp = setPcm(data.get(), isFirst);
                byte[] buffer_temp = setPcm(data.get(), true);
                isFirst = false;
                for (int i = 0; i < buffer_temp.length; i++, buffer_index++) {
                    buffer[buffer_index] = buffer_temp[i];
                }
            }
            datas.remove(0);
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

    public void addDatas(String a) {
        lock.lock();
        datas.add(new BaseData(a));
        lock.unlock();
    }
}

class BaseData {
    private ArrayList<Byte> arrayList;

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

    BaseData(String in) {
        arrayList = new ArrayList<>();
        byte[] in_t = in.getBytes();
        for (byte x : in_t) {
            arrayList.add(x);
        }
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
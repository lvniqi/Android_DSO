package com.example.lvniqi.multimeter.Audio;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by lvniqi on 2015-05-21.
 */

public class AudioEncoder extends AudioSender {
    static int PREPARE_DIV = 4;
    static int START_DIV = 6;
    //数据锁
    private ReentrantLock lock = new ReentrantLock();
    private ArrayList<Integer> datas = new ArrayList<Integer>();
    protected int buffer_index = 0;
    public boolean isFinish;
    /**
     * 先使用8~23分频
     * 共16个段
     * @param data
     * @return
     */
    byte [] setPcm(int data){
        int data_low = data%16;
        int len_low = data_low +START_DIV;
        int data_high = data/16;
        int len_high = data_high +START_DIV;
        byte [] result = new byte[PREPARE_DIV*2+len_low+len_high];
        int i=0;
        byte [] prepare = prepare();
        for(int count = 0;count<PREPARE_DIV*2;count++,i++){
            result[i] = prepare[count];
        }
        byte [] high = getDiv(len_high);
        for(int count = 0;count<len_high;count++,i++){
            result[i] = high[count];
        }
        byte [] low = getDiv(len_low);
        for(int count = 0;count<len_low;count++,i++){
            result[i] = low[count];
        }
        return result;
    }

    /**
     * 准备阶段
     * 如果开始发一帧需要这样做
     *
     * 第一个为分频5后的结果(9.6K)
     * 第二个为分频5后的结果(9.6k)
     * @return
     */
    byte [] prepare(){
        byte[] result  = new byte[PREPARE_DIV*2];
        byte[] in = getDiv(PREPARE_DIV);
        for(int i=0;i<PREPARE_DIV*2;i++){
            result[i] = in[i%PREPARE_DIV];
        }
        return  result;
    }
    /**
     * 输入分频数
     * 返回正弦表
     */
    byte [] getDiv(int div){
        byte[] result = new byte[div];
        for(int i=0;i<div;i++){
            result[i] = sinData[(int)(4096.0/div*(i+0.5))%4096];
        }
        return result;
    }

    @Override
    void Loop(int rate, byte[] buffer, float pos) {
            lock.lock();
            if(datas.size() != 0) {
                while(size> buffer_index) {
                    byte[] buffer_temp = setPcm(datas.get(0));
                    for(int i=0;i<buffer_temp.length;i++,buffer_index++){
                        buffer[buffer_index] = buffer_temp[i];
                    }
                }
                //datas.remove(0);
                audioTrack.write(buffer, 0,buffer_index);
                buffer_index = 0;
            }
            lock.unlock();
    }
    public void adddatas(int in){
        lock.lock();
        if(datas.size() <20) {
            datas.add(in);
        }
        lock.unlock();
    }
}

package com.example.lvniqi.multimeter.Audio;

import com.example.lvniqi.multimeter.MainActivity;

/**
 * Created by lvniqi on 2015-06-08.
 */
public class MessageDecoder extends AudioDecoder {
    @Override
    protected void dataProcress(byte[] t) {
        if (t.length == 2) {
            int t2 = ((short) t[1]) & 0xff;
            t2 <<= 8;
            t2 += ((short) t[0]) & 0xff;
            Float[] t3 = new Float[2];
            t3[0] = ((float) t2 * 3.3f / 4096);
            t3[1] = (float) 100;
            new MainActivity.MeasureCardsTask().execute(t3);
        }
    }
}

package demos.surfaceview_demo0;

import java.util.ArrayList;

/**
 * Created by lvniqi on 2014/11/22.
 */
public class ByteArrayFunction {
    private static final String libName = "byte2int"; // the module name of the library, without .so System . loadLibrary ( libName );

    static {
        System.loadLibrary(libName);
    }

    static public native int[] byte2int(byte[] data_in);
    static public float[] BytesToFloat(byte[] data_in) {
        float[] data_out = new float[data_in.length];
        for (int i = 0; i < data_in.length; i++) {
            data_out[i] = (short) data_in[i] & 0xff;
        }
        return data_out;
    }

    static public Integer[] BytesToInt(byte[] data_in) {
        Integer[] data_out = new Integer[data_in.length];
        for (int i = 0; i < data_in.length; i++) {
            data_out[i] = (short) data_in[i] & 0xff;
        }
        return data_out;
    }

    static public ArrayList<Integer> BytesToArrayInt(byte[] data_in, int len) {
        ArrayList<Integer> data_out = new ArrayList<Integer>(len);
        for (byte x : data_in) {
            data_out.add((short) x & 0xff);
        }
        return data_out;
    }

    static public void BytesToArrayInt(ArrayList<Integer> data, byte[] data_in) {
        data.clear();
        for (byte x : data_in) {
            data.add((short) x & 0xff);
        }
    }
}
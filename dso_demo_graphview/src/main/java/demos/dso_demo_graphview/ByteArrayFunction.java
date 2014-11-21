package demos.dso_demo_graphview;

/**
 * Created by lvniqi on 2014/11/22.
 */
public class ByteArrayFunction {
    static public float[] BytesToFloat(byte[] data_in) {
        float[] data_out = new float[data_in.length];
        for (int i = 0; i < data_in.length; i++) {
            data_out[i] = data_in[i];
        }
        return data_out;
    }
}

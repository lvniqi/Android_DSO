package demos.surfaceview_demo0;

/**
 * Created by lvniqi on 2014/11/10.
 */

import android.os.Bundle;
import android.os.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/**
 * Created by lvniqi on 2014/11/9.
 */
public class UdpService implements Runnable {
    private static final int MSG_SUCCESS = 0;//获取图片成功的标识
    public Boolean IsThreadDisable = false;//指示监听线程是否终止
    InetAddress mInetAddress;
    Integer port = null;
    DatagramSocket datagramSocket;
    DatagramPacket datagramPacket;
    // 接收的字节大小，客户端发送的数据不能超过这个大小
    byte[] message = new byte[5000];

    public UdpService(Integer port2) {
        port = port2;
    }

    public void StartListen() {
        // UDP服务器监听的端口
        try {
            // 建立Socket连接
            if (datagramSocket == null) {
                datagramSocket = new DatagramSocket(port);
                datagramPacket = new DatagramPacket(message,
                        message.length);
                //datagramSocket.setBroadcast(true);
                datagramSocket.setBroadcast(true);
            }
            try {
                while (!IsThreadDisable) {
                    // 准备接收数据
                    //Log.d("UDP Demo", "准备接受");
                    datagramSocket.receive(datagramPacket);
                    byte[] temp = new byte[datagramPacket.getLength() - 1];
                    byte channel_flag = datagramPacket.getData()[datagramPacket.getOffset()];
                    System.arraycopy(datagramPacket.getData(), datagramPacket.getOffset() + 1, temp, 0, datagramPacket.getLength() - 1);
                    Message m = new Message();
                    if (((int) channel_flag & 0x80) != 0) {
                        m.what = DefinedMessages.ADD_NEW_DATA_CH1;
                    } else {
                        m.what = DefinedMessages.ADD_NEW_DATA_CH2;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("str", temp);
                    m.setData(bundle);
                    MainActivity.graphView.getUpdate_thread().getmHandler().sendMessage(m);
                    //MainActivity.mlock.release();
                }
            } catch (IOException e) {//IOException
                e.printStackTrace();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        StartListen();
    }
}


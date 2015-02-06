package demos.surfaceview_demo0;

/**
 * Created by lvniqi on 2014/11/10.
 */

import android.os.Bundle;
import android.os.Message;
import android.text.format.Time;

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

    public UdpService(Integer port2) {
        port = port2;
    }

    public void StartListen() {
        // UDP服务器监听的端口

        // 接收的字节大小，客户端发送的数据不能超过这个大小
        byte[] message = new byte[5000];
        try {
            // 建立Socket连接
            DatagramSocket datagramSocket = new DatagramSocket(port);
            //datagramSocket.setBroadcast(true);
            DatagramPacket datagramPacket = new DatagramPacket(message,
                    message.length);
            byte[] a = new byte[4096];
            datagramSocket.setBroadcast(true);
            Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
            t.setToNow(); // 取得系统时间。
            try {
                while (!IsThreadDisable) {
                    // 准备接收数据
                    //Log.d("UDP Demo", "准备接受");
                    datagramSocket.receive(datagramPacket);
                    byte[] temp = new byte[datagramPacket.getLength()];
                    System.arraycopy(datagramPacket.getData(), datagramPacket.getOffset(), temp, 0, datagramPacket.getLength());
                    Message m = new Message();
                    m.what = DefinedMessages.ADD_NEW_DATA;
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


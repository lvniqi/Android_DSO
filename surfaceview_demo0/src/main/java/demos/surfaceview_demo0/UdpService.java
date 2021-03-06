package demos.surfaceview_demo0;

/**
 * Created by lvniqi on 2014/11/10.
 */

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;


/**
 * Created by lvniqi on 2014/11/9.
 */
public class UdpService implements Runnable {
    private static final int MSG_SUCCESS = 0;//获取图片成功的标识
    private final String TAG = "UdpService";
    // 接收的字节大小，客户端发送的数据不能超过这个大小
    byte[] message = new byte[5000];
    private InetAddress mInetAddress;
    private Integer port = null;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private ArrayList<SeriesChannel> channelList;

    public UdpService(Integer port) {
        this.port = port;
    }

    public void StartListen() {
        // UDP服务器监听的端口
        try {
            // 建立Socket连接
            datagramSocket = new DatagramSocket(port);
            //datagramSocket.setReuseAddress(true);
            //datagramSocket.bind(new InetSocketAddress(port));
            //datagramSocket.setBroadcast(true);
            //datagramSocket.setBroadcast(true);
            datagramPacket = new DatagramPacket(message,
                    message.length);
            //Message m = MainActivity.graphView.getUpdate_thread().getmHandler().obtainMessage();
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // 准备接收数据
                    //Log.d("UDP Demo", "准备接受");
                    datagramSocket.receive(datagramPacket);
                    byte[] temp = new byte[datagramPacket.getLength() - 1];
                    byte channel_flag = datagramPacket.getData()[datagramPacket.getOffset()];
                    System.arraycopy(datagramPacket.getData(), datagramPacket.getOffset() + 1, temp, 0, datagramPacket.getLength() - 1);
                    /*channelList = MainActivity.graphView.getUpdate_thread().getChannelList();
                    if(channelList != null && channelList.size() >0) {
                        MainActivity.graphView.getUpdate_thread().getLockData().lock();
                        if (((int) channel_flag & 0x80) != 0) {
                            //通道0
                            channelList.get(0).setData(ByteArrayFunction.BytesToArrayInt(temp, 5000));
                        }
                        else{
                            //通道1
                            channelList.get(1).setData(ByteArrayFunction.BytesToArrayInt(temp, 5000));
                        }
                        MainActivity.graphView.getUpdate_thread().getLockData().unlock();
                    }*/
                    //Message m = new Message();
                    //long startTime=System.nanoTime(); //获取开始时间\
                    //long endTime=System.nanoTime(); //获取结束时间
                    //Log.i("程序运行时间： ",(endTime-startTime)+"ns");
                    if (((int) channel_flag & 0x80) != 0) {
                        //m.what = DefinedMessages.ADD_NEW_DATA_CH1;
                        MainActivity.graphView.getUpdate_thread().getChannelList().get(0).setData(temp);
                    } else {
                        //m.what = DefinedMessages.ADD_NEW_DATA_CH2;
                        MainActivity.graphView.getUpdate_thread().getChannelList().get(1).setData(temp);
                    }
                    new MainActivity.textMessageTask().execute(temp.toString());
                    //MainActivity.status.setVisibility(View.VISIBLE);
                    Thread.sleep(5);
                    //Bundle bundle = new Bundle();
                    //bundle.putByteArray("str", temp);
                    //m.setData(bundle);
                    //m.sendToTarget();
                    //MainActivity.graphView.getUpdate_thread().getmHandler().removeMessages(m.what);
                    //MainActivity.graphView.getUpdate_thread().getmHandler().sendMessage(m);
                    //MainActivity.mlock.release();
                }
            } catch (IOException e) {
                Log.i(TAG, "IOException");
                e.printStackTrace();
            } catch (InterruptedException e) {
                Log.i(TAG, "InterruptedException");
                e.printStackTrace();
            }
        } catch (SocketException e) {
            Log.i(TAG, "SocketException");
            e.printStackTrace();
        } finally {
            if (datagramSocket != null && (!datagramSocket.isClosed())) {
                datagramSocket.close();
            }
        }

    }

    @Override
    public void run() {
        StartListen();
    }

    public void close() {
        if (!datagramSocket.isClosed()) {
            datagramSocket.close();
        }
    }
}


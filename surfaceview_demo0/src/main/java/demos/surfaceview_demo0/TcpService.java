package demos.surfaceview_demo0;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by lvniqi on 2015-02-15.
 */
public class TcpService extends Thread implements Serializable {
    public static final String TAG = "TcpService";
    private transient ServerSocket serverSocket;
    private transient Socket socket;
    private int port;
    private boolean outTime = false;
    private boolean isCheck = false;
    TcpService(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        serverRun();
    }

    public void close() {
        try {
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isOutTime() {
        return outTime;
    }

    public boolean isCheck() {
        return isCheck;
    }

    /**
     * TCP服务器运行程序
     */
    void serverRun() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(3000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //while (!Thread.currentThread().isInterrupted()){
            // 等待客户端连接
            try {
                socket = serverSocket.accept();
                isCheck = true;
                //CommunicationThread commThread = new CommunicationThread(socket);
                //new Thread(commThread).start();
            } catch (SocketTimeoutException e) {
                Log.i(TAG, "TIMEOUT");
                outTime = true;
                isCheck = true;
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        // }
        /*try {
            serverSocket.close();
        } catch (IOException e) {
            Log.i(TAG, "Socket Close Error");
            e.printStackTrace();
        }*/
    }
};

class CommunicationThread implements Runnable {
    private Socket clientSocket;
    private BufferedReader input;
    public CommunicationThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String read = input.readLine();
                if (read != null) {
                    Log.i(TcpService.TAG, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
package demos.surfaceview_demo0;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lvniqi on 2015-02-15.
 */
public class TcpService implements Runnable {
    final String TAG = "TcpService";
    private ServerSocket serverSocket;
    private Socket socket;
    private int port;

    TcpService(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        serverRun();
    }

    public void close() {

    }

    /**
     * TCP服务器运行程序
     */
    void serverRun() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(2000);
        } catch (IOException e) {
            Log.i("TAG", e.toString() + "");
            e.printStackTrace();
            return;
        }
        while (!Thread.currentThread().isInterrupted()) {
            // 等待客户端连接
            try {
                socket = serverSocket.accept();
                CommunicationThread commThread = new CommunicationThread(socket);
                new Thread(commThread).start();
            } catch (IOException e) {
                Log.i("TAG", "IOException");
                e.printStackTrace();
            }
        }
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
                Log.i("TAG", read);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
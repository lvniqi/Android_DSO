package com.example.lvniqi.tcp_test0;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends Activity {
    Runnable tcpClient = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            clientRun();
        }
    };
    Runnable tcpServer = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            serverRun();
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(tcpServer).start();
        /*Button button = (Button) this.findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(tcpClient).start();
            }
        });*/
    }

    void serverRun() {
        try {
            Boolean endFlag = false;
            ServerSocket ss = new ServerSocket(55555);
            while (!endFlag) {
                // 等待客户端连接
                Socket s = ss.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                //注意第二个参数据为true将会自动flush，否则需要需要手动操作output.flush()
                PrintWriter output = new PrintWriter(s.getOutputStream(), true);
                String message = input.readLine();
                Log.d("Tcp Demo", "message from Client:" + message);
                output.println("message received!");
                //output.flush();
                if ("shutDown".equals(message)) {
                    endFlag = true;
                }
                output.close();
                s.close();
            }
            ss.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void clientRun() {
        try {
            System.out.println("Client：Connecting");
            //IP地址和端口号（对应服务端），我这的IP是本地路由器的IP地址
            Socket socket = new Socket("192.168.1.150", 12345);
            //发送给服务端的消息
            String message = "Message from Android phone";
            try {
                System.out.println("Client Sending: '" + message + "'");

                //第二个参数为True则为自动flush
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(
                                socket.getOutputStream())), true);
                out.println(message);
                //out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //关闭Socket
                socket.close();
                System.out.println("Client:Socket closed");
            }
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


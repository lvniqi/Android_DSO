package com.example.lvniqi.tcp_test0;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends Activity {
    Runnable downloadRun = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            updateListView();
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) this.findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(downloadRun).start();
            }
        });
    }

    void updateListView() {
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
//                      out.flush();
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


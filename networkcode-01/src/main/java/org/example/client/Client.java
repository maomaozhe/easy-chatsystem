package org.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "192.168.3.248";//服务器IP地址
    private static final int SERVER_PORT = 8080;//服务器端口

    public static void main(String[] args) throws IOException {
        //连接服务器 套接字
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        System.out.println("Connected to server");


        //设置输入输出流

        //键盘输入  InputStreamReader: 这是一个桥接类，
        //用于将字节流（System.in）转换为字符流，因为键盘输入本质上是字符。

        //BufferedReader 一次读取一行
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        //从网络套接字读取数据
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //向网络套接字写入数据
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        // TODO: 处理用户输入和服务器消息
        // 1. 获取用户昵称并发送给服务器  显示时间
        // 2. 创建新线程接收服务器消息
        // 3. 在主线程中发送用户输入到服务器

        System.out.print("请输入你的昵称： ");
        String nickname = userInput.readLine();
        out.println(nickname);

        //创建线程接受服务器消息
        new Thread(() -> {
            String message;
            try{
                while ((message = in.readLine()) != null){
                    System.out.println(message);
                }
            } catch (IOException e){
                System.out.println("与服务器连接断开");
            }
        }).start();

        //主线程发送输入到服务器
        String userMessage;
        while((userMessage = userInput.readLine()) != null) {
            out.println(userMessage);
            if("exit".equalsIgnoreCase(userMessage)) {
                break;
            }
        }
        socket.close();

    }
}

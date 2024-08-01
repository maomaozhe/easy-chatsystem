package org.example.server;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Server {
    private static final int PORT = 8080;
    //储存连接的客户端
    private static Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);//创建服务器套接字
        System.out.println("Server started on port: " + PORT);

        while(true){//不断接受新的客户端连接

            //接受客户端连接
            Socket clientSocker = serverSocket.accept();
            System.out.println("New client connected: " + clientSocker);

            //为每个客户端创建处理器
            ClientHandler clientHandler = new ClientHandler(clientSocker);
            clients.add(clientHandler);//加入到集合


            //启动新线程处理这个客户端
            new Thread(clientHandler).start();


    }

}

    static class ClientHandler implements Runnable{
        private Socket socket;
        private  BufferedReader in;
        private  PrintWriter out;
        private String nickname;

        public ClientHandler(Socket socket) throws IOException {
            this.socket =  socket;//

            //设置输入输出流
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

        }

        //完成抽象方法 run


        @Override
        public void run() {
            try{
                // TODO: 处理客户端连接和消息
                // 1. 获取客户端昵称
                // 2. 循环读取客户端消息
                // 3. 广播消息给所有客户端
                out.println("enter your name:");
                nickname = in.readLine();
                System.out.println(nickname + "已加入聊天室");
                broadcastMessage(nickname + "加入了聊天室");


                String message;
                while((message = in.readLine()) != null){
                    if("exit".equalsIgnoreCase(message)){
                        break;
                    }
                    //广播消息
                    broadcastMessage(formatMessage(nickname, message));
                }


            } catch (IOException e){
                System.out.println("error: " + e.getMessage());
            } finally {
                try{
                    socket.close();//关闭连接

                } catch (IOException e){
                    e.printStackTrace();
                }
                //移除这个客户端 同时通知
                clients.remove(this);
                broadcastMessage(nickname + "离开了聊天室");
                System.out.println(nickname + "断开连接");
            }
        }
        // TODO: 实现发送消息给所有客户端的方法

        private void broadcastMessage(String message){
            //server对每个client发送
            for(ClientHandler client : clients){
                client.out.println(message);
            }
        }

        //发送的
        //标准消息
        private String formatMessage(String sender, String content){
            LocalDateTime now = LocalDateTime.now();
            //时间
            String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            return String.format("[%s] %s: %s", time, sender, content);

        }
    }

    }




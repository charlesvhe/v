package charles.v.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        java.net.ServerSocket serverSocket = new ServerSocket(2048);

        while (true){
            Socket socket = serverSocket.accept();
            ThroughputServerThread tst = new ThroughputServerThread(socket);
            tst.start();
        }
    }
}

class ThroughputServerThread extends Thread{
    Socket socket;
    public ThroughputServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, 1024);
            byte[] data = new byte[1024];
            while (true){
                int dataSize = bis.read(data, 0, data.length);
                if(dataSize == -1){
                    return;
                }
                System.out.println(dataSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

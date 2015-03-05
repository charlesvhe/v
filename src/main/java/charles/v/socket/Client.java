package charles.v.socket;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        new ThroughputClientThread().start();   // 50%
//        new ThroughputClientThread().start();   // 75%
//        new ThroughputClientThread().start();   // 100%
//        new ThroughputClientThread().start();   // 100%
    }
}

class ThroughputClientThread extends Thread {
    @Override
    public void run() {
        try {
            java.net.Socket socket = new Socket("10.125.2.17", 2048);
            BufferedOutputStream bo = new BufferedOutputStream(socket.getOutputStream(), 1024);
            byte[] data = new byte[1024];
            while (true) {
                bo.write(data, 0, data.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

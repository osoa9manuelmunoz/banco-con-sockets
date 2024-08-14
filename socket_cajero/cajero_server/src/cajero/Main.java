package cajero;

import cajero.server.JavaServerSocket;
import cajero.server.Server;
import cajero.server.SocketProcess;
import java.net.ServerSocket;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Java Server Socket");

        JavaServerSocket javaServerSocket = new JavaServerSocket(1802, 100);
        ServerSocket serverSocket = javaServerSocket.get();
        if (serverSocket == null) {
            System.out.println("ServerSocket es nulo");
            return;
        }

        SocketProcess server = new Server(serverSocket);

        while (true) {
            if (server.bind()) {
                List<Object> dataRequest = server.listen(); // Usa List<Object> aquí
                server.processRequest(dataRequest);
                server.close();
            }
        }
    }
}
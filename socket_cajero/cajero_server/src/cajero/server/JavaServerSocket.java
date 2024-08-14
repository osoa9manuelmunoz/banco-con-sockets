package cajero.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaServerSocket {
    private int port;
    private int maxClients;

    public JavaServerSocket(int port, int maxClients) {
        this.port = port;
        this.maxClients = maxClients;
    }

    public ServerSocket get() {
        try {
            return new ServerSocket(this.port, this.maxClients);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage(), e);
            return null;
        }
    }
}
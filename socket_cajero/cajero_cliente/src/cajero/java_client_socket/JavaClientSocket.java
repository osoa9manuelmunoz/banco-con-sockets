package cajero.java_client_socket;

import cajero.client.Client;

public class JavaClientSocket {
    private Client client;

    public JavaClientSocket(String address, int port) {
        client = new Client(address, port);
        client.connect();
    }

    public Client getClient() {
        return client;
    }
}
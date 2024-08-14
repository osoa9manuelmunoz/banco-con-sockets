package cajero.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client implements SocketProcess {
    private Session session;
    private String host;
    private int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean connect() {
        try {
            Socket socket = new Socket(host, port);
            this.session = new Session(socket);
            System.out.println("\n[Client]: Conexión exitosa.");
            return true;
        } catch (IOException e) {
            System.err.println("\n[Client]: Error al conectar con el servidor.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Object> listen() {
        List<Object> dataList = new ArrayList<>();
        boolean next = true;
        while (next) {
            Object data = this.session.read();
            if (data != null) {
                dataList.add(data);
                next = false; // Sale del bucle después de recibir una respuesta
            }
        }
        return dataList;
    }

    @Override
    public boolean response(List<Object> data) {
        // Verifica que siempre se envíen cuatro elementos
        if (data.size() != 4) {
            System.err.println("[Client]: Error: Datos incompletos. Se esperaban 4 elementos.");
            return false;
        }
    
        for (Object d : data) {
            System.out.println("[Client] Enviando: " + d);
            if (!this.session.write(d)) {
                System.err.println("[Client]: Error al enviar datos.");
                return false;
            }
        }
    
        List<Object> response = listen();
        if (response == null || response.isEmpty()) {
            System.err.println("[Client]: No se recibió respuesta del servidor.");
            return false;
        }
    
        for (Object r : response) {
            System.out.println("[Client] Recibido: " + r);
        }
    
        return true;
    }

    @Override
    public boolean close() {
        return this.session.close();
    }
}
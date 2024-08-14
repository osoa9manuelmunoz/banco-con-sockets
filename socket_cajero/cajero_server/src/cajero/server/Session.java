package cajero.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Session {
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Session(Socket socket) throws IOException {
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public Object read() {
        try {
            Object data = this.objectInputStream.readObject();
            System.out.println("[Server]: Recibido: " + data);
            return data;
        } catch (ClassNotFoundException | IOException e) {
            System.err.println("[Server]: Error al leer datos.");
            e.printStackTrace();
            return null;
        }
    }

    public boolean write(Object data) {
        try {
            System.out.println("[Server]: Enviando: " + data);
            this.objectOutputStream.writeObject(data);
            this.objectOutputStream.flush();
            return true;
        } catch (IOException e) {
            System.err.println("[Server]: Error al enviar datos.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean close() {
        try {
            this.objectOutputStream.close();
            this.objectInputStream.close();
            return true;
        } catch (IOException e) {
            System.err.println("[Server]: Error al cerrar la conexión.");
            e.printStackTrace();
            return false;
        }
    }
}
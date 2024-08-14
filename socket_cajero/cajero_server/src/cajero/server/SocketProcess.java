package cajero.server;

import java.util.List;

public interface SocketProcess {
    boolean bind();
    List<Object> listen();
    boolean response(List<Object> data);
    boolean close();
    void processRequest(List<Object> requestData); // Asegúrate de que esto coincida
}
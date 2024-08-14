package cajero.client;

import java.util.List;

public interface SocketProcess {
    boolean connect();
    List<Object> listen();
    boolean response(List<Object> data);
    boolean close();
}
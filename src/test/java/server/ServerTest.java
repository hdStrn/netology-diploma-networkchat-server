package server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import server.Server;

public class ServerTest {

    Server server;

    @Test
    public void startServer_thenReturnTrue() {
        server = new Server();
        Assertions.assertTrue(server.startServer());
        server = null;
    }
}

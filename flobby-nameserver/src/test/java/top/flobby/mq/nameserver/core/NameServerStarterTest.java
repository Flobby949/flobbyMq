package top.flobby.mq.nameserver.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class NameServerStarterTest {

    @Test
    public void testStartServer() {
        NameServerStarter nameServerStarter = new NameServerStarter(8080);
        assertDoesNotThrow(nameServerStarter::startServer);
    }
}
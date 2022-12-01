package log;

import log.LogType;
import log.Logger;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoggerTest {

    private Logger logger;

    @Test
    public void givenMessage_whenLog_thenReturnTrue() {
        String msg = "Hello all";
        PrintWriter out = mock(PrintWriter.class);

        logger = Logger.getInstance();

        boolean actual = logger.log(msg, LogType.MESSAGE, false);

        assertTrue(actual);
    }
}

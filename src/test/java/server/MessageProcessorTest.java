package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.MessageProcessor;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageProcessorTest {

    private MessageProcessor messageProcessor;
    private Socket clientSocket;
    private BufferedReader income;
    private PrintWriter outcome;

    @BeforeEach
    public void mockDependencies() {
        clientSocket = mock(Socket.class);
        income = mock(BufferedReader.class);
        outcome = mock(PrintWriter.class);
    }

    @AfterEach
    public void resetDependencies() {
        clientSocket = null;
        income = null;
        outcome = null;
        messageProcessor = null;
    }

    @Test
    public void givenAvailableName_whenIsUsernameExists_thenReturnFalse() {
        // имитируем пустой список юзеров онлайн
        List<MessageProcessor> usersOnline = new CopyOnWriteArrayList<>();

        // создаем тестируемый объект
        messageProcessor = new MessageProcessor(clientSocket, usersOnline);
        messageProcessor.setIncome(income);
        messageProcessor.setOutcome(outcome);

        // тестируем метод
        assertFalse(messageProcessor.isUsernameExists("Oleg"));
    }

    @Test
    public void givenExistingName_whenIsUsernameExists_thenReturnTrue() {
        MessageProcessor user = new MessageProcessor(clientSocket, new CopyOnWriteArrayList<>());
        user.setUsername("Oleg");
        List<MessageProcessor> usersOnline = new CopyOnWriteArrayList<>(List.of(user));

        messageProcessor = new MessageProcessor(clientSocket, usersOnline);
        messageProcessor.setIncome(income);
        messageProcessor.setOutcome(outcome);

        assertTrue(messageProcessor.isUsernameExists("Oleg"));
    }

    @Test
    public void whenRegisterUser_thenReturnTrue() throws IOException {
        when(income.readLine()).thenReturn("Oleg");

        List<MessageProcessor> usersOnline = new CopyOnWriteArrayList<>();

        messageProcessor = new MessageProcessor(clientSocket, usersOnline);
        messageProcessor.setIncome(income);
        messageProcessor.setOutcome(outcome);

        assertTrue(messageProcessor.register());
    }

    @Test
    public void givenIncomeMessageEqualsExit_whenReadAndSendMessage_thenReturnFalse() throws IOException {
        when(income.readLine()).thenReturn("/exit");

        List<MessageProcessor> usersOnline = new CopyOnWriteArrayList<>();

        messageProcessor = new MessageProcessor(clientSocket, usersOnline);
        messageProcessor.setIncome(income);
        messageProcessor.setOutcome(outcome);
        messageProcessor.setUsername("Oleg");

        assertFalse(messageProcessor.readAndSendMessage());
    }

    @Test
    public void givenIncomeMessageNotEqualsExit_whenReadAndSendMessage_thenReturnTrue() throws IOException {
        when(income.readLine()).thenReturn("Hello");

        List<MessageProcessor> usersOnline = new CopyOnWriteArrayList<>();

        messageProcessor = new MessageProcessor(clientSocket, usersOnline);
        messageProcessor.setIncome(income);
        messageProcessor.setOutcome(outcome);
        messageProcessor.setUsername("Oleg");

        assertTrue(messageProcessor.readAndSendMessage());
    }

    @Test
    public void givenTwoMoreUsersInChat_whenPrintMsgInConsoleAndSendToAll_thenSendMessageTwoTimes() {
        // имитируем еще 2-ух юзеров в чате
        MessageProcessor user1 = new MessageProcessor(clientSocket, new CopyOnWriteArrayList<>());
        user1.setOutcome(outcome);
        user1.setUsername("Elena");
        MessageProcessor user2 = new MessageProcessor(clientSocket, new CopyOnWriteArrayList<>());
        user2.setOutcome(outcome);
        user2.setUsername("Ivan");
        List<MessageProcessor> usersOnline = new CopyOnWriteArrayList<>(List.of(user1, user2));

        messageProcessor = new MessageProcessor(clientSocket, usersOnline);
        messageProcessor.setIncome(income);
        messageProcessor.setOutcome(outcome);
        messageProcessor.setUsername("Oleg");

        messageProcessor.printMsgInConsoleAndSendToAll("Hello all");

        verify(outcome, times(2)).println("Hello all");
    }
}

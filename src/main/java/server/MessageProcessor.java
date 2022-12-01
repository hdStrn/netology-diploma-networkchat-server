package server;

import log.LogType;
import log.Logger;
import settings.MessageSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import static log.TimeFormat.getCurrentTime;

public class MessageProcessor implements Runnable {

    private final Socket clientSocket;
    private PrintWriter outcome;
    private BufferedReader income;
    private String username;
    private List<MessageProcessor> usersOnline;
    private final Logger logger = Logger.getInstance();

    public MessageProcessor(Socket clientSocket, List<MessageProcessor> usersOnline) {
        this.clientSocket = clientSocket;
        this.usersOnline = usersOnline;
    }

    public PrintWriter getOutcome() {
        return outcome;
    }

    public void setOutcome(PrintWriter outcome) {
        this.outcome = outcome;
    }

    public void setIncome(BufferedReader income) {
        this.income = income;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void run() {
        try {
            outcome = new PrintWriter(clientSocket.getOutputStream(), true);
            income = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            register();
            while (readAndSendMessage());
        } catch (IOException e) {
            usersOnline.remove(this);
            String info = formatEvent("* " + username + " has disconnected *");
            printMsgInConsoleAndSendToAll(info);
            logger.log(info, LogType.INFO, true);
        } finally {
            outcome.close();
            try {
                income.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean register() {
        outcome.println(MessageSettings.getProperty("user.name"));
        String name;
        try {
            do {
                name = income.readLine();
            } while (isUsernameExists(name));
            username = name;
            String info = formatEvent("* " + username + " has joined the chat *");
            printMsgInConsoleAndSendToAll(info);
            logger.log(info, LogType.INFO, true);
            outcome.println(MessageSettings.getProperty("user.welcome"));
            usersOnline.add(this);
            return true;
        } catch (IOException e) {
            System.out.println(MessageSettings.getProperty("user.disconnect"));
            return false;
        }
    }

    public boolean isUsernameExists(String name) {
        for (MessageProcessor user : usersOnline) {
            if (user.getUsername().equals(name)) {
                outcome.println(MessageSettings.getProperty("user.name.exists"));
                return true;
            }
        }
        return false;
    }

    public boolean readAndSendMessage() throws IOException {
        String message = income.readLine();
        if ("/exit".equalsIgnoreCase(message)) {
            String info = formatEvent("* " + username + " leaving the chat *");
            printMsgInConsoleAndSendToAll(info);
            usersOnline.remove(this);
            logger.log(info, LogType.INFO, true);
            return false;
        } else if (message != null) {
            message = formatMessage(message);
            printMsgInConsoleAndSendToAll(message);
            logger.log(message, LogType.MESSAGE, true);
        }
        return true;
    }

    public void printMsgInConsoleAndSendToAll(String msg) {
        System.out.println(msg);
        for (MessageProcessor user : usersOnline) {
            if (!user.getUsername().equals(username)) {
                user.getOutcome().println(msg);
            }
        }
    }

    public String formatEvent(String msg) {
        return getCurrentTime() + msg;
    }

    public String formatMessage(String msg) {
        return getCurrentTime() + username + " says: " + msg;
    }
}

package server;

import log.LogType;
import log.Logger;
import settings.MessageSettings;
import settings.ServerSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private ServerSocket serverSocket;
    private Logger logger = Logger.getInstance();
    private List<MessageProcessor> usersOnline = new CopyOnWriteArrayList<>();

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public boolean startServer() {
        int port = Integer.parseInt(ServerSettings.getProperty("port"));
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.log(MessageSettings.getProperty("server.cannot.start"), LogType.INFO, false);
            return false;
        }
        String start = MessageSettings.getProperty("server.start");
        System.out.println(start);
        logger.log(start, LogType.INFO, false);
        return true;
    }

    public void listenForConnection() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept(); // сервер ждет подключения

                // при подключении создаем новый поток, который будет обрабатывать сообщения от клиента
                new Thread(new MessageProcessor(clientSocket, usersOnline)).start();

            } catch (IOException e) {
                logger.log(MessageSettings.getProperty("server.troubles"), LogType.ERROR, false);
                break;
            }
        }
    }
}

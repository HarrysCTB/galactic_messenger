package galactic_messenger.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import galactic_messenger.client.ClientHandler;

import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int INVALID_PORT_NUMBER = -1;
    private static boolean isServerRunning = true;
    private static boolean initialMessageDisplayed = false;
    private static Set<String> connectedClients = new HashSet<>();
    private static Map<String, ClientHandler> clientHandlers = new HashMap<>();

    public static void main(String[] args) {
        validateArguments(args);

        int portNumber = getPortNumber(args[0]);

        createServerSocket(portNumber);
    }

    private static void validateArguments(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar galactic_messenger_server.jar [port number]");
            System.exit(1);
        }
    }

    private static int getPortNumber(String portArg) {
        try {
            return Integer.parseInt(portArg);
        } catch (NumberFormatException e) {
            System.err.println("Error: Port number must be an integer.");
            System.exit(1);
            return INVALID_PORT_NUMBER;
        }
    }

    private static void createServerSocket(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            printServerStartedMessage(serverSocket);
            startShutdownThread();

            while (isServerRunning) {
                try {
                    serverSocket.setSoTimeout(1000);
                    displayWaitingForClientConnection();

                    Socket clientSocket = serverSocket.accept();
                    printClientConnectedMessage(clientSocket);

                    ClientHandler clientHandler = new ClientHandler(clientSocket, connectedClients, clientHandlers);
                    clientHandler.start();
                } catch (java.net.SocketTimeoutException e) {
                    // Ignore
                }
            }
        } catch (IOException e) {
            handleServerSocketException(port, e);
        }
    }

    private static void startShutdownThread() {
        Thread shutdownThread = new Thread(() -> waitForShutdownCommand());
        shutdownThread.start();
    }

    private static void displayWaitingForClientConnection() {
        if (!initialMessageDisplayed) {
            System.out.println("Waiting for client connection...");
            initialMessageDisplayed = true;
        }
    }

    private static void printServerStartedMessage(ServerSocket serverSocket) throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        String ipAddress = address.getHostAddress();
        System.out.println("Server started on " + ipAddress + ":" + serverSocket.getLocalPort());
    }

    private static void printClientConnectedMessage(Socket clientSocket) {
        System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
        initialMessageDisplayed = false;
    }

    private static void handleServerSocketException(int port, IOException e) {
        System.err.println("Error: Could not listen on port " + port);
        System.exit(1);
    }

    private static void waitForShutdownCommand() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (isServerRunning) {
                String command = reader.readLine();
                if ("shutdown".equalsIgnoreCase(command)) {
                    System.out.println("Shutting down the server...");
                    isServerRunning = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
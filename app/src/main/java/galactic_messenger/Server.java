package galactic_messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Server class for the galactic messenger.
 * This class creates a server that waits for client connections.
 */
public class Server {
    private static final int INVALID_PORT_NUMBER = -1;
    private static boolean isServerRunning = true;
    private static boolean initialMessageDisplayed = false;

    /**
     * Main method to start the server.
     *
     * @param args - Command-line arguments. Expects a single argument: the port
     *             number.
     */
    public static void main(String[] args) {
        validateArguments(args);

        int portNumber = getPortNumber(args[0]);

        createServerSocket(portNumber);
    }

    /**
     * Validates the command-line arguments.
     *
     * @param args - Command-line arguments
     */
    private static void validateArguments(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar galactic_messenger_server.jar [port number]");
            System.exit(1);
        }
    }

    /**
     * Parses the port number from the command-line argument.
     *
     * @param portArg - Command-line argument containing the port number
     * @return The port number as an integer
     */
    private static int getPortNumber(String portArg) {
        try {
            return Integer.parseInt(portArg);
        } catch (NumberFormatException e) {
            System.err.println("Error: Port number must be an integer.");
            System.exit(1);
            return INVALID_PORT_NUMBER;
        }
    }

    /**
     * Creates a server socket and listens for client connections.
     *
     * @param port - Port number to listen on
     */
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
                } catch (java.net.SocketTimeoutException e) {
                    // Ignore
                }
            }
        } catch (IOException e) {
            handleServerSocketException(port, e);
        }
    }

    /**
     * Starts the thread for handling the "shutdown" command.
     */
    private static void startShutdownThread() {
        Thread shutdownThread = new Thread(() -> waitForShutdownCommand());
        shutdownThread.start();
    }

    /**
     * Displays the "Waiting for client connection..." message if it hasn't been
     * displayed yet.
     */
    private static void displayWaitingForClientConnection() {
        if (!initialMessageDisplayed) {
            System.out.println("Waiting for client connection...");
            initialMessageDisplayed = true;
        }
    }

    /**
     * Prints a message indicating that the server has started.
     *
     * @param serverSocket - The server socket that was created
     * @throws UnknownHostException - If the local host address is unknown
     */
    private static void printServerStartedMessage(ServerSocket serverSocket) throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        String ipAddress = address.getHostAddress();
        System.out.println("Server started on " + ipAddress + ":" + serverSocket.getLocalPort());
    }

    /**
     * Prints a message indicating that a client has connected.
     *
     * @param clientSocket - The socket representing the client connection
     */
    private static void printClientConnectedMessage(Socket clientSocket) {
        System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
        initialMessageDisplayed = false;
    }

    /**
     * Handles exceptions that occur when creating the server socket.
     *
     * @param port - The port number that the server was trying to listen on
     * @param e    - The exception that was thrown
     */
    private static void handleServerSocketException(int port, IOException e) {
        System.err.println("Error: Could not listen on port " + port);
        System.exit(1);
    }

    /**
     * Waits for the "shutdown" command to be entered and stops the server.
     */
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

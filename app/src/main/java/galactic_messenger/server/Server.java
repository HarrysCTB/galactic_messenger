package galactic_messenger.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import galactic_messenger.utils.ValidationUtils;
import galactic_messenger.utils.Color;

public class Server {
    private static final Map<String, ClientHandler> clientHandlers = new ConcurrentHashMap<>();

    public static void addClientHandler(String username, ClientHandler clientHandler) {
        clientHandlers.put(username, clientHandler);
    }

    public static void removeClientHandler(String username) {
        clientHandlers.remove(username);
    }

    public static ClientHandler getClientHandler(String username) {
        return clientHandlers.get(username);
    }
    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length != 1) {
            System.err.println(Color.colorize("Usage: java -jar galactic_messenger_server.jar [port]", Color.RED));
            System.exit(1);
        }

        int port = ValidationUtils.validateAndParsePort(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            displayServerInfo(serverSocket);

            // Utilisez Executors pour gérer les threads des clients
            var threadPool = Executors.newCachedThreadPool();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(
                        Color.colorize("New client connected: " + clientSocket.getInetAddress().getHostAddress(),
                                Color.GREEN));
                // Créez un thread pour gérer la connexion du client
                threadPool.execute(new ClientHandler(clientSocket, new Database()));
            }
        } catch (IOException e) {
            System.err.println(Color.colorize("Error: " + e.getMessage(), Color.RED));
            e.printStackTrace(System.err);
        }
    }

    private static void displayServerInfo(ServerSocket serverSocket) throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        String ipAddress = address.getHostAddress();
        System.out.println(
                Color.colorize("Server available at " + ipAddress + ":" + serverSocket.getLocalPort(), Color.BLUE));
    }
}

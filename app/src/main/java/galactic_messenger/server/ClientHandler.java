package galactic_messenger.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Database database;

    public ClientHandler(Socket clientSocket, Database database) {
        this.clientSocket = clientSocket;
        this.database = database;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String response = handleCommand(inputLine);
                if (response != null) {
                    out.println(response);
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleCommand(String command) {
        String[] parts = command.split(" ", 3);
        String action = parts[0].toLowerCase();

        switch (action) {
            case "/register":
                if (parts.length == 3) {
                    String username = parts[1];
                    String hashedPassword = parts[2];
                    if (database.registerUser(username, hashedPassword)) {
                        return "Registration successful.";
                    } else {
                        return "Registration failed.";
                    }
                } else {
                    return "Invalid command format.";
                }
            case "/login":
                if (parts.length == 3) {
                    String username = parts[1];
                    String hashedPassword = parts[2];
                    if (database.authenticateUser(username, hashedPassword)) {
                        return "Login successful.";
                    } else {
                        return "Login failed.";
                    }
                } else {
                    return "Invalid command format.";
                }
            default:
                return "Unknown command: " + action;
        }
    }
}

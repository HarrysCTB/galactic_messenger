package galactic_messenger.client;

import javax.swing.*;

import galactic_messenger.utils.StyledMessage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.text.StyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.BadLocationException;


public class Client implements ServerResponseListener {

    private JFrame frame;
    private JTextField commandInput;
    private JTextPane chatPane;
    private PrintWriter out;
    private BufferedReader in;
    private ServerHandler serverHandler;
    private CommandHandler commandHandler;
    private Socket socket;
    private ActionListener initialCommandListener;
    private ActionListener chatCommandListener;

    @Override
    public void updateChat(StyledMessage styledMessage) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = chatPane.getStyledDocument();
                Style style = chatPane.addStyle("ColorStyle", null);
                StyleConstants.setForeground(style, styledMessage.getColor());
                doc.insertString(doc.getLength(), styledMessage.getText(), style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    public Client(String ipAddress, int serverPort) throws IOException {
        this.socket = new Socket(ipAddress, serverPort);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        createGUI();

        serverHandler = new ServerHandler(chatPane, this);
        commandHandler = new CommandHandler(this);

        listenToServer();

        new Thread(() -> {
            try {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    serverHandler.handleServerResponse(serverResponse);
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> updateChat(new StyledMessage("Error: " + e.getMessage(), Color.WHITE)));
            }
        }).start();
    }

    public void exitApplication() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SwingUtilities.invokeLater(() -> {
                frame.dispose();
                System.exit(0);
            });
        }
    }

    private void listenToServer() {
        new Thread(() -> {
            try {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    final String response = serverResponse;
                    SwingUtilities.invokeLater(() -> {
                        serverHandler.handleServerResponse(response);
                    });
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    updateChat(new StyledMessage("Error: " + e.getMessage(), Color.RED));
                });
            }
        }).start();
    }


    private void createGUI() {
        frame = new JFrame("Galactic Messenger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setBackground(Color.BLACK);
        chatPane.setForeground(Color.WHITE);
        chatPane.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(chatPane);

        commandInput = new JTextField();
        initialCommandListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = commandInput.getText().trim();
                commandInput.setText("");
                String response = commandHandler.handleCommand(command);
                if (response != null) {
                    out.println(response);
                }
            }
        };
        commandInput.addActionListener(initialCommandListener);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(commandInput, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public void startChatSession() {
        commandInput.removeActionListener(initialCommandListener);
        chatCommandListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = commandInput.getText().trim();
                commandInput.setText("");
                processChatCommand(command);
            }
        };
        commandInput.addActionListener(chatCommandListener);
        chatPane.setEditable(true);
    }

    public void endChatSession() {
        if (chatCommandListener != null) {
            commandInput.removeActionListener(chatCommandListener);
        }
        commandInput.addActionListener(initialCommandListener);
        chatPane.setEditable(false);
    }

    private void processChatCommand(String command) {
        String[] parts = command.split(" ", 2);
        String action = parts[0];

        switch (action.toLowerCase()) {
            case "/logout":
                out.println(action);
                endChatSession();
                break;
            case "/online_users":
                out.println(command);
                break;
            case "/private_chat":
                if (parts.length == 2) {
                    String targetUser = parts[1];
                    out.println("/private_chat " + targetUser);
                }
                break;
            case "/accept":
                if (parts.length == 2) {
                    String requestingUser = parts[1];
                    out.println("/accept " + requestingUser);
                }
                break;
            case "/decline":
                if (parts.length == 2) {
                    String requestingUser = parts[1];
                    out.println("/decline " + requestingUser);
                }
                break;
            default:
                displayErrorMessage("Unknown command: " + command);
                break;
        }
    }


    private void displayErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = chatPane.getStyledDocument();
                Style style = chatPane.addStyle("ErrorStyle", null);
                StyleConstants.setForeground(style, Color.RED);
                doc.insertString(doc.getLength(), message + "\n", style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (args.length != 2) {
                JOptionPane.showMessageDialog(null,
                        "Usage: java -jar galactic_messenger_client.jar [adresse ip serveur] [numéro de port]",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            String ipAddress = args[0];
            int serverPort = Integer.parseInt(args[1]);

            try {
                new Client(ipAddress, serverPort);
            } catch (UnknownHostException e) {
                JOptionPane.showMessageDialog(null, "Unknown host: " + ipAddress, "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error connecting to server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}

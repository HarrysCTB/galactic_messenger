package galactic_messenger.client;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class ServerHandler {

    private JTextPane chatPane; // Remplacer JTextArea par JTextPane
    private ServerResponseListener responseListener;

    // Constructeur qui accepte JTextPane
    public ServerHandler(JTextPane chatPane, ServerResponseListener responseListener) {
        this.chatPane = chatPane;
        this.responseListener = responseListener;
    }

        public void handleServerResponse(String response) {
            System.out.println(response);
            if (response != null) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        StyledDocument doc = chatPane.getStyledDocument();
                        Style style = chatPane.addStyle("ServerStyle", null);
                        if (response.startsWith("Online Users:")) {
                            // Traitement spécial pour la liste des utilisateurs en ligne
                            StyleConstants.setForeground(style, Color.GREEN);
                            doc.insertString(doc.getLength(), response + "\n", style);
                        } else if (response.startsWith("Registration successful")) {
                            // Inscription réussie
                            StyleConstants.setForeground(style, Color.GREEN);
                            doc.insertString(doc.getLength(), "Registration successful. You can now log in.\n", style);
                        } else if (response.startsWith("Registration failed")) {
                            // Échec de l'inscription
                            StyleConstants.setForeground(style, Color.RED);
                            doc.insertString(doc.getLength(), "Registration failed: " + response.substring("Registration failed".length()) + "\n", style);
                        } else if (response.startsWith("Login successful")) {
                            // Connexion réussie
                            StyleConstants.setForeground(style, Color.GREEN);
                            doc.insertString(doc.getLength(), "Login successful. You can now use the chat.\n", style);
                            ((Client)responseListener).startChatSession();
                        } else if (response.startsWith("Login failed")) {
                            // Échec de la connexion
                            StyleConstants.setForeground(style, Color.RED);
                            doc.insertString(doc.getLength(), "Login failed: " + response.substring("Login failed".length()) + "\n", style);
                        } else if (response.equals("Logged out")) {
                            // Affichage du message de déconnexion
                            StyleConstants.setForeground(style, Color.BLUE);
                            doc.insertString(doc.getLength(), "You have been logged out.\n", style);
                            ((Client) responseListener).endChatSession(); // Appel pour terminer la session de chat
                        } else if (response.startsWith("Private Chat Request sent to ")) {
                            // Afficher la notification de demande de chat privé
                            StyleConstants.setForeground(style, Color.ORANGE);
                            doc.insertString(doc.getLength(), response + "\n", style);
                        } else if (response.startsWith("Private Chat Accepted by")) {
                            // Afficher la confirmation d'acceptation du chat privé
                            StyleConstants.setForeground(style, Color.GREEN);
                            doc.insertString(doc.getLength(), response + "\n", style);
                        } else if (response.startsWith("Private Chat Declined by")) {
                            // Afficher la notification de refus du chat privé
                            StyleConstants.setForeground(style, Color.RED);
                            doc.insertString(doc.getLength(), response + "\n", style);
                        } else if (response.startsWith("Exited Private Chat")) {
                            // Afficher la notification de fin de chat privé
                            StyleConstants.setForeground(style, Color.BLUE);
                            doc.insertString(doc.getLength(), response + "\n", style);
                        } else if (response.startsWith("User ") && response.contains(" is no longer available.")) {
                            StyleConstants.setForeground(style, Color.RED);
                            doc.insertString(doc.getLength(), response + "\n", style);
                        } else if (response.startsWith("User ") && response.contains(" is not available for private chat.")) {
                            StyleConstants.setForeground(style, Color.RED);
                            doc.insertString(doc.getLength(), response + "\n", style);
                        } else if (response.startsWith("You have accepted ")) {
                            StyleConstants.setForeground(style, Color.GREEN);
                            doc.insertString(doc.getLength(), response + "\n", style);
                        } else if (response.startsWith("Requesting user ") && response.contains(" is no longer available.")) {
                            StyleConstants.setForeground(style, Color.RED);
                            doc.insertString(doc.getLength(), response + "\n", style);
                        } else if (response.startsWith("Private Chat Request: ")) {
                            StyleConstants.setForeground(style, Color.ORANGE);
                            doc.insertString(doc.getLength(), response + "\n", style);
                        } else if (response.startsWith("Private Chat Accepted: ")) {
                            StyleConstants.setForeground(style, Color.GREEN);
                            doc.insertString(doc.getLength(), response + "\n", style);
                        }
                        else {
                            // Autres réponses du serveur
                            StyleConstants.setForeground(style, Color.RED);
                            doc.insertString(doc.getLength(), "Unhandled response: " + response + "\n", style);
                        }
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
}
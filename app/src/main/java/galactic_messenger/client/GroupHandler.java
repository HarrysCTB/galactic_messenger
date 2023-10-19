package galactic_messenger.client;

import java.util.Set;
import java.io.PrintWriter;

public class GroupHandler {
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";

    public void handleGroupCommands(String clientInput, ClientHandler clientHandler) {
        String[] parts = clientInput.split(" ", 3); // split into at most 3 parts
        String command = parts[0];
        PrintWriter out = clientHandler.getOut();

        if ("/create_group".equalsIgnoreCase(command) && parts.length > 1) {
            String groupName = parts[1];
            if (ClientHandler.getMembersOfChatGroup(groupName) == null) {
                ClientHandler.addToChatGroup(groupName, clientHandler);
                out.println(
                        ANSI_GREEN + "Groupe " + ANSI_RESET + ANSI_RED + groupName + ANSI_RESET + ANSI_GREEN + " créé."
                                + ANSI_RESET);
            } else {
                out.println(ANSI_GREEN + "Ce groupe existe déjà." + ANSI_RESET);
            }
        } else if ("/join_group".equalsIgnoreCase(command) && parts.length > 1) {
            String groupName = parts[1];
            Set<ClientHandler> groupMembers = ClientHandler.getMembersOfChatGroup(groupName);
            if (groupMembers != null) {
                ClientHandler.addToChatGroup(groupName, clientHandler);
                out.println(ANSI_GREEN + "Vous avez rejoint le groupe " + ANSI_RESET + ANSI_RED + groupName + ANSI_RESET
                        + ".");
            } else {
                out.println("Ce groupe n'existe pas.");
            }
        } else if ("/exit_group".equalsIgnoreCase(command) && parts.length > 1) {
            String groupName = parts[1];
            Set<ClientHandler> groupMembers = ClientHandler.getMembersOfChatGroup(groupName);
            if (groupMembers != null) {
                ClientHandler.removeFromChatGroup(groupName, clientHandler);
                out.println(ANSI_GREEN + "Vous avez quitté le groupe " + ANSI_RESET + ANSI_RED + groupName + ANSI_RESET
                        + ".");
            } else {
                out.println(ANSI_GREEN + "Ce groupe n'existe pas." + ANSI_RESET);
            }
        } else if ("/msg_group".equalsIgnoreCase(command) && parts.length > 2) {
            String groupName = parts[1];
            String message = parts[2];
            Set<ClientHandler> groupMembers = ClientHandler.getMembersOfChatGroup(groupName);
            if (groupMembers != null) {
                for (ClientHandler member : groupMembers) {
                    if (member != clientHandler) { // Don't send the message back to the sender
                        member.getOut().println(
                                ANSI_YELLOW + "[" + groupName + "] " + ANSI_RESET + ANSI_RED
                                        + clientHandler.getUsername() + ANSI_RESET + ANSI_YELLOW + ": " + message
                                        + ANSI_RESET);
                    }
                }
            } else {
                out.println(ANSI_GREEN + "Ce groupe n'existe pas." + ANSI_RESET);
            }
        } else if ("/list_groups".equalsIgnoreCase(command)) {
            Set<String> allGroups = ClientHandler.getAllGroupNames();
            if (allGroups.isEmpty()) {
                out.println(ANSI_GREEN + "Il n'y a pas de groupes actuellement disponibles." + ANSI_RESET);
            } else {
                out.println(ANSI_GREEN + "Groupes disponibles : " + String.join(", ", allGroups) + ANSI_RESET);
            }
        } else if ("/create_secure_group".equalsIgnoreCase(command) && parts.length > 2) {
            String groupName = parts[1];
            String password = parts[2];
            if (ClientHandler.getMembersOfChatGroup(groupName) == null) {
                ClientHandler.addToChatGroup(groupName, clientHandler);
                ClientHandler.setGroupPassword(groupName, password);
                out.println(ANSI_GREEN + "Groupe sécurisé " + ANSI_RESET + ANSI_RED + groupName
                        + ANSI_RESET + ANSI_GREEN + " créé." + ANSI_RESET);
            } else {
                out.println(ANSI_RED + "Ce groupe existe déjà." + ANSI_RESET);
            }
        } else if ("/join_secure_group".equalsIgnoreCase(command) && parts.length > 2) {
            String groupName = parts[1];
            String password = parts[2];
            Set<ClientHandler> groupMembers = ClientHandler.getMembersOfChatGroup(groupName);
            if (groupMembers != null) {
                String correctPassword = ClientHandler.getGroupPassword(groupName);
                if (correctPassword.equals(password)) {
                    ClientHandler.addToChatGroup(groupName, clientHandler);
                    out.println(ANSI_GREEN + "Vous avez rejoint le groupe sécurisé "
                            + ANSI_RESET + ANSI_RED + groupName + ANSI_RESET + ".");
                } else {
                    out.println(ANSI_RED + "Mot de passe incorrect." + ANSI_RESET);
                }
            } else {
                out.println(ANSI_RED + "Ce groupe n'existe pas." + ANSI_RESET);
            }
        }
    }
}
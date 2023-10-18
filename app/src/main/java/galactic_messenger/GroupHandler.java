package galactic_messenger;

import java.util.Set;
import java.io.PrintWriter;

public class GroupHandler {

    public void handleGroupCommands(String clientInput, ClientHandler clientHandler) {
        String[] parts = clientInput.split(" ", 3); // split into at most 3 parts
        String command = parts[0];
        PrintWriter out = clientHandler.getOut();

        if ("/create_group".equalsIgnoreCase(command) && parts.length > 1) {
            String groupName = parts[1];
            if (ClientHandler.getMembersOfChatGroup(groupName) == null) {
                ClientHandler.addToChatGroup(groupName, clientHandler);
                out.println("Groupe " + groupName + " créé.");
            } else {
                out.println("Ce groupe existe déjà.");
            }
        } else if ("/join_group".equalsIgnoreCase(command) && parts.length > 1) {
            String groupName = parts[1];
            Set<ClientHandler> groupMembers = ClientHandler.getMembersOfChatGroup(groupName);
            if (groupMembers != null) {
                ClientHandler.addToChatGroup(groupName, clientHandler);
                out.println("Vous avez rejoint le groupe " + groupName + ".");
            } else {
                out.println("Ce groupe n'existe pas.");
            }
        } else if ("/exit_group".equalsIgnoreCase(command) && parts.length > 1) {
            String groupName = parts[1];
            Set<ClientHandler> groupMembers = ClientHandler.getMembersOfChatGroup(groupName);
            if (groupMembers != null) {
                ClientHandler.removeFromChatGroup(groupName, clientHandler);
                out.println("Vous avez quitté le groupe " + groupName + ".");
            } else {
                out.println("Ce groupe n'existe pas.");
            }
        } else if ("/msg_group".equalsIgnoreCase(command) && parts.length > 2) {
            String groupName = parts[1];
            String message = parts[2];
            Set<ClientHandler> groupMembers = ClientHandler.getMembersOfChatGroup(groupName);
            if (groupMembers != null) {
                for (ClientHandler member : groupMembers) {
                    if (member != clientHandler) { // Don't send the message back to the sender
                        member.getOut().println("[" + groupName + "] " + clientHandler.getUsername() + ": " + message);
                    }
                }
            } else {
                out.println("Ce groupe n'existe pas.");
            }
        }
    }
}

package galactic_messenger.client;

public class ServerHandler {
    public void handleServerResponse(String response) {
        // Traitement de la réponse du serveur
        if (response != null) {
            // Exemple de traitement : afficher la réponse du serveur
            System.out.println("[Server]: " + response);

            // Ajoutez ici la logique pour gérer les réponses du serveur
            // Par exemple, pour afficher des messages de confirmation ou des messages
            // d'erreur
            if (response.startsWith("Registration successful")) {
                // Inscription réussie
                System.out.println("Registration successful. You can now log in.");
            } else if (response.startsWith("Registration failed")) {
                // Échec de l'inscription
                System.out.println("Registration failed: " + response.substring("Registration failed".length()));
            } else if (response.startsWith("Login successful")) {
                // Connexion réussie
                System.out.println("Login successful. You can now use the chat.");
                // Ajoutez ici la logique pour gérer la boucle du chat
                // Par exemple, la gestion des commandes /private_chat, /accept, /decline, etc.
            } else if (response.startsWith("Login failed")) {
                // Échec de la connexion
                System.out.println("Login failed: " + response.substring("Login failed".length()));
            } else {
                // Autres cas non gérés
                System.out.println("Server response: " + response);
            }
        }
    }
}
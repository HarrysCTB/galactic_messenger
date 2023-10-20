package galactic_messenger.utils;

public class Color {
    // Codes ANSI pour les couleurs du texte
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Méthode pour envelopper du texte avec une couleur
    public static String colorize(String text, String colorCode) {
        return colorCode + text + RESET;
    }
}
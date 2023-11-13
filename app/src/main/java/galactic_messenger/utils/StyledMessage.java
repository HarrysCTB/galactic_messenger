package galactic_messenger.utils;

import java.awt.Color;

public class StyledMessage {
    private final String text;
    private final Color color;

    public StyledMessage(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }
}


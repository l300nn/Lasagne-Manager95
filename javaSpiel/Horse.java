package javaSpiel;

import java.awt.Color;

public class Horse {
    private String name;
    private Color color;
    private double baseSpeed;
    private double variance;
    
    // Jockey Details
    private Color jockeyShirtColor;
    private Color jockeyHelmetColor;
    private Color jockeySkinColor;

    public Horse(String name, Color color, double baseSpeed, double variance) {
        this.name = name;
        this.color = color;
        this.baseSpeed = baseSpeed;
        this.variance = variance;
        
        generateJockeyAppearance();
    }
    
    private void generateJockeyAppearance() {
        java.util.Random rand = new java.util.Random();
        // Zufällige Shirt-Farbe (grelle Farben für Arcade-Look)
        Color[] shirtColors = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.WHITE, Color.YELLOW, new Color(128, 0, 128)};
        this.jockeyShirtColor = shirtColors[rand.nextInt(shirtColors.length)];
        
        // Helm kann die gleiche Farbe wie Shirt oder eine andere haben
        this.jockeyHelmetColor = (rand.nextBoolean()) ? this.jockeyShirtColor : shirtColors[rand.nextInt(shirtColors.length)];
        
        // Zufällige Hautfarbe
        Color[] skinColors = {
            new Color(255, 224, 189), // Hell
            new Color(241, 194, 125), // Mittel
            new Color(224, 172, 105), // Gebräunt
            new Color(141, 85, 36),   // Dunkelbraun
            new Color(70, 40, 20)     // Sehr dunkel
        };
        this.jockeySkinColor = skinColors[rand.nextInt(skinColors.length)];
    }

    // Getters
    public Color getJockeyShirtColor() { return jockeyShirtColor; }
    public Color getJockeyHelmetColor() { return jockeyHelmetColor; }
    public Color getJockeySkinColor() { return jockeySkinColor; }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public double getBaseSpeed() {
        return baseSpeed;
    }

    public double getVariance() {
        return variance;
    }

    // Setter für Anpassungen vor einem Rennen
    public void setBaseSpeed(double baseSpeed) {
        this.baseSpeed = baseSpeed;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }
}

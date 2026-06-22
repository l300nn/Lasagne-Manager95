package javaSpiel;

import java.awt.Color;

public class Horse {
    private String name;
    private Color color;
    private double baseSpeed;
    private double variance;

    public Horse(String name, Color color, double baseSpeed, double variance) {
        this.name = name;
        this.color = color;
        this.baseSpeed = baseSpeed;
        this.variance = variance;
    }

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

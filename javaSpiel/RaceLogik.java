package javaSpiel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RaceLogik {
    private List<Horse> horses;
    private List<Double> currentOdds;
    private Random random;

    // Ein Pool an witzigen/kontroversen GTA-Style Namen
    private List<String> horseNamePool = Arrays.asList(
        "Glue Factory", "Al Capony", "Hoof Hearted", "Usain Colt", 
        "Pony Soprano", "Tax Evasion", "My Little Crony", "OnlyFoals",
        "Divorce Settlement", "Sugar Daddy", "Dust Eater", "Neigh Sayer",
        "Trojan Horse", "Bad Investment", "Mane Attraction", "Neighbeline",
        "Jon Bon Pony", "Harry Trotter", "Sylvester Stallion", "Wife's Alibi",
        "Bitcoin Crash", "Midlife Crisis", "Fake News", "Pony Stark"
    );

    // Verschiedene Farben für die Pferde
    private List<Color> colorPool = Arrays.asList(
        Color.RED, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.ORANGE, 
        Color.CYAN, Color.PINK, Color.WHITE, new Color(128, 0, 128), new Color(0, 128, 0)
    );

    public RaceLogik() {
        this.horses = new ArrayList<>();
        this.currentOdds = new ArrayList<>();
        this.random = new Random();
    }

    public void prepareNewRace() {
        horses.clear();
        currentOdds.clear();

        // Mischen der Pools, damit jedes Rennen neue Pferde hat
        Collections.shuffle(horseNamePool);
        Collections.shuffle(colorPool);

        int numHorses = 5; // Wir starten mit 5 Pferden pro Rennen

        for (int i = 0; i < numHorses; i++) {
            String name = horseNamePool.get(i);
            Color color = colorPool.get(i);
            
            // ==========================================
            // SPEED NERF - Rennen verlangsamt
            // ==========================================
            // Basis-Geschwindigkeit: 0.15 bis 0.5 (früher 0.6 bis 1.5)
            // Dadurch dauert das Rennen viel länger und man kann die Kommentare lesen.
            double baseSpeed = 0.15 + (random.nextDouble() * 0.35);
            
            // Varianz (Unberechenbarkeit): 0.05 bis 0.35
            double variance = 0.05 + (random.nextDouble() * 0.3);
            
            Horse h = new Horse(name, color, baseSpeed, variance);
            horses.add(h);

            // ==========================================
            // Quoten-Berechnung
            // ==========================================
            // Je schneller das Pferd (höhere baseSpeed), desto niedriger sollte die Quote sein.
            // Angepasst an die verlangsamte Basis-Geschwindigkeit.
            double odds = (1.5 / h.getBaseSpeed()) + (random.nextDouble() * 1.0 - 0.5);
            
            // Mindestquote festlegen
            if(odds < 1.1) {
                odds = 1.1 + random.nextDouble() * 0.5;
            }
            
            // Runden auf zwei Nachkommastellen für die Anzeige
            odds = Math.round(odds * 100.0) / 100.0;
            currentOdds.add(odds);
        }
    }

    public double calculateTickProgress(Horse h) {
        // Fortschritt = Basis-Geschwindigkeit + Zufallswert basierend auf Varianz
        double step = h.getBaseSpeed() + (random.nextDouble() * h.getVariance());
        return step;
    }

    public List<Horse> getHorses() {
        return horses;
    }

    public List<Double> getCurrentOdds() {
        return currentOdds;
    }
}

package javaSpiel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RaceTrackPanel extends JPanel {
    private List<Horse> horses;
    private double[] progress;

    // Klasse für Werbeschilder
    private static class Billboard {
        int x, y, width, height;
        Color bg, fg;
        String text;
        String subText;
        int logoType;

        public Billboard(int x, int y, int w, int h, Color bg, Color fg, String t, String st, int logoType) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.bg = bg;
            this.fg = fg;
            this.text = t;
            this.subText = st;
            this.logoType = logoType;
        }
    }

    private static class Brand {
        String name, subText;
        Color bg, fg;
        int logoType;

        public Brand(String name, String subText, Color bg, Color fg, int logoType) {
            this.name = name;
            this.subText = subText;
            this.bg = bg;
            this.fg = fg;
            this.logoType = logoType;
        }
    }

    // Klasse für Staubpartikel
    private static class DustParticle {
        double x, y;
        double life;
        Color color;
        double size;
        double vx, vy;

        public DustParticle(double x, double y) {
            this.x = x;
            this.y = y;
            this.life = 1.0;
            this.size = 2 + Math.random() * 4;
            this.color = new Color(180, 140, 100);
            this.vx = -1 - Math.random() * 2;
            this.vy = -0.5 + Math.random();
        }

        public void update() {
            x += vx;
            y += vy;
            life -= 0.04;
            size += 0.2;
        }
    }

    private static class MoneyParticle {
        double x, y;
        double speedY;
        double speedX;
        double rotation;
        double rotationSpeed;
        boolean isCoin; // Münze oder Schein

        public MoneyParticle(int width) {
            x = Math.random() * width;
            y = -20 - Math.random() * 50;
            speedY = 2 + Math.random() * 5;
            speedX = -1 + Math.random() * 2;
            rotation = Math.random() * Math.PI * 2;
            rotationSpeed = -0.1 + Math.random() * 0.2;
            isCoin = Math.random() > 0.4;
        }
    }

    private List<Billboard> billboards = new ArrayList<>();
    private List<DustParticle> dustParticles = new ArrayList<>();
    private List<MoneyParticle> moneyParticles = new ArrayList<>();
    private boolean moneyRainActive = false;
    private int lastWidth = 0;

    public void setHorses(List<Horse> horses, double[] progress) {
        this.horses = horses;
        this.progress = progress;
        this.dustParticles.clear(); // Staub beim Neustart entfernen
        this.moneyParticles.clear();
        this.moneyRainActive = false;
        repaint();
    }

    public void setMoneyRain(boolean active) {
        this.moneyRainActive = active;
        if (!active) {
            this.moneyParticles.clear();
        }
    }

    public void updateParticles() {
        // Partikel aktualisieren und alte entfernen
        Iterator<DustParticle> it = dustParticles.iterator();
        while (it.hasNext()) {
            DustParticle p = it.next();
            p.update();
            if (p.life <= 0)
                it.remove();
        }

        // Geld-Regen updaten
        if (moneyRainActive) {
            // Neue Geldscheine/Münzen erzeugen
            if (Math.random() < 0.6) { // Häufigkeit
                moneyParticles.add(new MoneyParticle(getWidth()));
            }
            Iterator<MoneyParticle> moneyIt = moneyParticles.iterator();
            while (moneyIt.hasNext()) {
                MoneyParticle mp = moneyIt.next();
                mp.y += mp.speedY;
                mp.x += mp.speedX;
                mp.rotation += mp.rotationSpeed;
                if (mp.y > getHeight() + 30) {
                    moneyIt.remove();
                }
            }
        }

        if (horses != null && progress != null) {
            int width = getWidth();
            int topOffset = 70;
            int trackHeight = getHeight() - topOffset;

            if (trackHeight > 0) {
                int startX = 50;
                int finishX = width - 80;
                int laneHeight = trackHeight / horses.size();

                for (int i = 0; i < horses.size(); i++) {
                    if (progress[i] > 0 && progress[i] < 100) {
                        // 50% Chance pro Tick, Staub zu erzeugen
                        if (Math.random() < 0.5) {
                            int horseX = startX + (int) ((progress[i] / 100.0) * (finishX - startX));
                            int y = topOffset + i * laneHeight;
                            int hoofY = y + laneHeight / 2 + 10; // Ungefähre Position der Hufe
                            dustParticles.add(new DustParticle(horseX + 5, hoofY));
                        }
                    }
                }
            }
        }
    }

    private void generateBillboards(int width) {
        billboards.clear();

        List<Brand> allBrands = new ArrayList<>();
        allBrands.add(new Brand("McDowell's", "I'm tolerating it", new Color(220, 20, 20), new Color(255, 204, 0), 1));
        allBrands.add(new Brand("Moonbucks", "Overpriced Coffee", new Color(0, 112, 74), Color.WHITE, 2));
        allBrands.add(new Brand("Koka-Kola", "Enjoy Diabetes", new Color(244, 0, 9), Color.WHITE, 3));
        allBrands.add(new Brand("Adios", "Just do it tomorrow", new Color(0, 81, 186), Color.WHITE, 4));
        allBrands.add(new Brand("Pear", "Think Identical", new Color(240, 240, 240), Color.BLACK, 5));
        allBrands.add(new Brand("Burger Tsar", "Have it our way", new Color(242, 169, 0), new Color(218, 41, 28), 6));
        allBrands.add(new Brand("Subpar", "Eat Freshly Frozen", new Color(0, 137, 56), new Color(255, 203, 10), 7));
        allBrands.add(new Brand("Dead Bull", "Gives you wings... maybe", new Color(30, 60, 110), Color.LIGHT_GRAY, 8));
        allBrands.add(new Brand("Relax", "Fake Luxury Watches", new Color(0, 96, 57), new Color(201, 160, 80), 9));
        allBrands.add(new Brand("KFD", "Kentucky Fried Dog", new Color(163, 13, 20), Color.WHITE, 10));
        allBrands.add(new Brand("Gargle", "We know everything", new Color(255, 255, 255), new Color(66, 133, 244), 11));
        allBrands.add(new Brand("Heinieken", "Probably Beer", new Color(0, 130, 32), Color.WHITE, 12));

        Collections.shuffle(allBrands, new Random(42));
        Random rnd = new Random(42);

        int currentX = 20;
        int brandIndex = 0;

        while (brandIndex < allBrands.size()) {
            Brand brand = allBrands.get(brandIndex);

            // Präzise Breite berechnen, um Textüberlappung zu vermeiden
            java.awt.image.BufferedImage tmpImg = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D gTmp = tmpImg.createGraphics();
            FontMetrics fmTitle = gTmp.getFontMetrics(new Font("Monospaced", Font.BOLD, 18));
            FontMetrics fmSub = gTmp.getFontMetrics(new Font("Monospaced", Font.BOLD, 10));
            int textW = Math.max(fmTitle.stringWidth(brand.name), fmSub.stringWidth(brand.subText));
            gTmp.dispose();
            
            int w = 40 + 20 + textW + 20; // Logo + Abstand + Textbreite + Randabstand

            // Prüfen, ob das Plakat noch komplett auf den Bildschirm passt
            if (currentX + w > width - 20) {
                break;
            }

            billboards.add(new Billboard(currentX, 5, w, 50, brand.bg, brand.fg, brand.name, brand.subText, brand.logoType));
            currentX += w + 20 + rnd.nextInt(50);
            brandIndex++;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int width = getWidth();
        int height = getHeight();

        if (width != lastWidth && width > 0) {
            generateBillboards(width);
            lastWidth = width;
        }

        // --- 1. HINTERGRUND & PLAKATWAND (OBEN) ---
        int topOffset = 70;

        g2d.setColor(new Color(20, 20, 30));
        g2d.fillRect(0, 0, width, topOffset);

        g2d.setColor(new Color(80, 80, 80));
        g2d.drawLine(0, topOffset - 10, width, topOffset - 10);
        g2d.drawLine(0, topOffset - 5, width, topOffset - 5);
        for (int x = 0; x < width; x += 15) {
            g2d.drawLine(x, topOffset - 10, x, topOffset - 5);
        }

        for (Billboard b : billboards) {
            g2d.setColor(new Color(60, 40, 20));
            g2d.fillRect(b.x + 10, b.y + b.height, 4, topOffset - b.y - b.height);
            g2d.fillRect(b.x + b.width - 15, b.y + b.height, 4, topOffset - b.y - b.height);

            g2d.setColor(b.bg);
            g2d.fillRect(b.x, b.y, b.width, b.height);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(b.x, b.y, b.width, b.height);
            g2d.drawRect(b.x + 2, b.y + 2, b.width - 4, b.height - 4);

            int logoW = 40;
            drawBrandLogo(g2d, b.x + 10, b.y + 5, b.logoType);

            g2d.setColor(b.fg);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 18)); // Etwas größer
            FontMetrics fm = g2d.getFontMetrics();
            int tX = b.x + logoW + 10 + (b.width - logoW - 10 - fm.stringWidth(b.text)) / 2;
            g2d.drawString(b.text, tX, b.y + 24);

            g2d.setFont(new Font("Monospaced", Font.BOLD, 10));
            fm = g2d.getFontMetrics();
            tX = b.x + logoW + 10 + (b.width - logoW - 10 - fm.stringWidth(b.subText)) / 2;
            g2d.drawString(b.subText, tX, b.y + 38);

            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.drawLine(b.x + 3, b.y + 3, b.width - 4, b.y + 3);
        }

        // --- 2. RENNSTRECKE ---
        int trackHeight = height - topOffset;
        if (horses == null || progress == null || trackHeight <= 0)
            return;

        int laneHeight = trackHeight / horses.size();

        // Gras zeichnen
        g2d.setColor(new Color(25, 60, 25));
        g2d.fillRect(0, topOffset, width, trackHeight);

        // STARTLINIE (Weiß, durch alle Bahnen)
        int startX = 50;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(startX + 25, topOffset, 4, trackHeight); // +25 damit die Schnauze beim Start an der Linie ist

        // Start-Text auf dem Rasen (Vertikal)
        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2d.drawString("S", startX + 10, topOffset + 20);
        g2d.drawString("T", startX + 10, topOffset + 35);
        g2d.drawString("A", startX + 10, topOffset + 50);
        g2d.drawString("R", startX + 10, topOffset + 65);
        g2d.drawString("T", startX + 10, topOffset + 80);

        // ZIELLINIE (Kariertes Muster)
        int finishX = width - 80;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(finishX, topOffset, 16, trackHeight);
        g2d.setColor(new Color(20, 20, 20));
        for (int i = topOffset; i < height; i += 16) {
            g2d.fillRect(finishX, i, 8, 8);
            g2d.fillRect(finishX + 8, i + 8, 8, 8);
        }

        // Bahnen zeichnen
        for (int i = 0; i < horses.size(); i++) {
            int y = topOffset + i * laneHeight;

            // Sandige Rennbahn
            g2d.setColor(new Color(90, 60, 40));
            g2d.fillRect(0, y + 5, width, laneHeight - 10);

            // Begrenzungslinien
            g2d.setColor(new Color(120, 80, 50));
            g2d.drawLine(0, y, width, y);

            // Nummer
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
            g2d.drawString(String.valueOf(i + 1), 15, y + laneHeight / 2 + 6);
        }

        // Staubpartikel zeichnen (Hinter den Pferden, vor der Bahn)
        for (DustParticle p : dustParticles) {
            int alpha = (int) (p.life * 180);
            if (alpha < 0)
                alpha = 0;
            if (alpha > 255)
                alpha = 255;
            g2d.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), alpha));
            int s = (int) p.size;
            g2d.fillOval((int) p.x, (int) p.y, s, s);
        }

        // --- HIER KOMMT DIE PFERDE-ZEICHNEN LOGIK (Auszug) ---
        // Die Pferde-Zeichenschleife beginnt gleich danach
        for (int i = 0; i < horses.size(); i++) {
            int y = topOffset + i * laneHeight;
            int horseX = startX + (int) ((progress[i] / 100.0) * (finishX - startX - 25)); // -25 wg. Pferdelänge

            // Wipp-Effekt und Schatten-Logik
            boolean legsOpen = (horseX % 20) < 10;
            int bobOffset = legsOpen ? 0 : 3; // Pferd springt hoch, wenn Beine zusammen sind

            // Dynamischer Schatten (wird kleiner/heller, wenn das Pferd hüpft)
            int shadowWidth = legsOpen ? 30 : 24;
            int shadowXOffset = legsOpen ? 0 : 3;
            int shadowAlpha = legsOpen ? 150 : 90; // Transparenz anpassen
            g2d.setColor(new Color(20, 15, 10, shadowAlpha));
            g2d.fillOval(horseX + shadowXOffset, y + laneHeight / 2 + 10, shadowWidth, 6);

            // Pferd mit Wipp-Effekt (y - bobOffset) zeichnen
            drawPixelHorse(g2d, horseX, y + laneHeight / 2 + 5 - bobOffset, horses.get(i));
        }

        // Geld-Regen zeichnen (ganz oben auf allem anderen)
        if (!moneyParticles.isEmpty()) {
            for (MoneyParticle mp : moneyParticles) {
                java.awt.geom.AffineTransform old = g2d.getTransform();
                g2d.translate(mp.x, mp.y);
                g2d.rotate(mp.rotation);
                
                if (mp.isCoin) {
                    g2d.setColor(new Color(255, 215, 0)); // Gold
                    g2d.fillOval(-8, -8, 16, 16);
                    g2d.setColor(new Color(218, 165, 32)); // Dunkleres Gold
                    g2d.drawOval(-8, -8, 16, 16);
                    g2d.setColor(new Color(180, 130, 10)); // Münz-Symbol
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
                    g2d.drawString("$", -3, 5);
                } else {
                    g2d.setColor(new Color(34, 139, 34)); // Grüner Schein
                    g2d.fillRect(-15, -8, 30, 16);
                    g2d.setColor(new Color(0, 100, 0)); // Dunkelgrüner Rand
                    g2d.drawRect(-15, -8, 30, 16);
                    g2d.setColor(new Color(200, 255, 200, 150)); // Helle Streifen
                    g2d.drawLine(-10, 0, 10, 0);
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(-4, -4, 8, 8); // Weißes Zentrum
                }
                
                g2d.setTransform(old);
            }
        }
    }

    private void drawBrandLogo(Graphics2D g, int x, int y, int type) {
        int cx = x + 20;
        int cy = y + 20;
        switch(type) {
            case 1: // McDowell's
                g.setColor(new Color(255, 204, 0));
                g.setStroke(new BasicStroke(4));
                g.drawLine(x + 5, y + 10, x + 12, y + 30);
                g.drawLine(x + 12, y + 30, cx, y + 15);
                g.drawLine(cx, y + 15, x + 28, y + 30);
                g.drawLine(x + 28, y + 30, x + 35, y + 10);
                g.setStroke(new BasicStroke(1));
                break;
            case 2: // Moonbucks
                g.setColor(Color.WHITE);
                g.fillOval(cx - 16, cy - 16, 32, 32);
                g.setColor(new Color(0, 112, 74));
                g.fillOval(cx - 12, cy - 12, 24, 24);
                g.setColor(Color.WHITE);
                g.fillRect(cx - 4, cy - 8, 8, 16);
                g.fillOval(cx - 8, cy - 12, 16, 16);
                break;
            case 3: // Koka-Kola
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(3));
                g.drawArc(x + 5, y + 10, 20, 20, 0, -180);
                g.drawArc(x + 15, y + 20, 20, 20, 0, 180);
                g.setStroke(new BasicStroke(1));
                break;
            case 4: // Adios (4 Stripes)
                g.setColor(Color.WHITE);
                for(int i = 0; i < 4; i++) {
                    int stripeH = 8 + i * 6; // Wachsende Streifen: 8, 14, 20, 26
                    int[] px = {x + i*9, x + 5 + i*9, x + 10 + i*9, x + 5 + i*9};
                    int[] py = {y + 35, y + 35, y + 35 - stripeH, y + 35 - stripeH};
                    g.fillPolygon(px, py, 4);
                }
                break;
            case 5: // Pear
                g.setColor(Color.BLACK);
                g.fillOval(cx - 10, cy - 12, 20, 24); 
                g.fillOval(cx - 6, cy - 16, 12, 12);  
                g.setColor(new Color(240, 240, 240));
                g.fillOval(cx + 4, cy - 5, 10, 10); 
                g.setColor(Color.BLACK);
                g.drawLine(cx, cy - 16, cx + 4, cy - 20); 
                break;
            case 6: // Burger Tsar
                g.setColor(new Color(242, 169, 0));
                g.fillArc(x + 5, y + 5, 30, 20, 0, 180); 
                g.fillArc(x + 5, y + 15, 30, 20, 0, -180); 
                g.setColor(new Color(218, 41, 28));
                g.setFont(new Font("Monospaced", Font.BOLD, 10));
                g.drawString("TSAR", x + 8, cy + 3);
                break;
            case 7: // Subpar (S mit Pfeilen)
                g.setFont(new Font("Monospaced", Font.BOLD, 30));
                g.setColor(Color.WHITE);
                g.drawString("S", x + 10, y + 32);
                g.fillPolygon(new int[]{x+12, x+6, x+12}, new int[]{cy-12, cy-8, cy-4}, 3); 
                g.setColor(new Color(255, 203, 10));
                g.fillPolygon(new int[]{x+28, x+34, x+28}, new int[]{cy+4, cy+8, cy+12}, 3); 
                break;
            case 8: // Dead Bull
                g.setColor(new Color(255, 204, 0));
                g.fillOval(cx - 10, cy - 10, 20, 20); 
                g.setColor(new Color(220, 20, 20));
                g.fillRect(cx - 15, cy - 5, 10, 10); 
                g.fillRect(cx + 5, cy - 5, 10, 10); 
                break;
            case 9: // Relax
                g.setColor(new Color(201, 160, 80));
                int[] cxp = {x + 5, x + 12, x + 20, x + 28, x + 35, x + 30, x + 10};
                int[] cyp = {y + 10, y + 20, y + 5, y + 20, y + 10, y + 28, y + 28};
                g.fillPolygon(cxp, cyp, 7);
                g.fillOval(cx - 8, cy + 10, 16, 4); 
                break;
            case 10: // KFD
                g.setColor(Color.WHITE);
                g.fillRect(x + 5, y + 5, 30, 30);
                g.setColor(new Color(163, 13, 20));
                g.fillRect(x + 5, y + 5, 10, 30);
                g.fillRect(x + 25, y + 5, 10, 30);
                g.setColor(Color.BLACK);
                g.fillOval(cx - 4, cy - 5, 8, 8); 
                g.drawLine(cx - 6, cy + 3, cx + 6, cy + 3); 
                g.drawLine(cx, cy + 3, cx, cy + 8); 
                break;
            case 11: // Gargle
                g.setFont(new Font("Monospaced", Font.BOLD, 30));
                g.setColor(new Color(66, 133, 244)); 
                g.drawString("G", x + 10, y + 30);
                g.setColor(new Color(234, 67, 53)); 
                g.fillRect(x + 20, y + 18, 6, 6);
                break;
            case 12: // Heinieken
                g.setColor(new Color(255, 20, 20));
                int[] sx = {cx, cx + 4, cx + 12, cx + 5, cx + 8, cx, cx - 8, cx - 5, cx - 12, cx - 4};
                int[] sy = {cy - 12, cy - 4, cy - 4, cy + 2, cy + 10, cy + 5, cy + 10, cy + 2, cy - 4, cy - 4};
                g.fillPolygon(sx, sy, 10);
                break;
        }
    }

    private void drawPixelHorse(Graphics2D g, int x, int y, Horse horse) {
        int size = 2; // Doppelte Pixeldichte (kleinere Blöcke) für mehr Details
        boolean legsOpen = (x % 30) < 15; // Geschmeidigere Animation

        Color baseColor = horse.getColor();
        Color highlight = baseColor.brighter().brighter();
        if (baseColor.getRed() < 50 && baseColor.getGreen() < 50 && baseColor.getBlue() < 50) {
            highlight = new Color(80, 80, 80);
        }
        Color shadow = baseColor.darker();
        
        Color shirt = horse.getJockeyShirtColor();
        Color helmet = horse.getJockeyHelmetColor();
        Color skin = horse.getJockeySkinColor();

        // --- 1. HINTERE BEINE ---
        g.setColor(new Color(15, 15, 15)); 
        if (legsOpen) {
            g.fillRect(x - 3*size, y + 1*size, 2*size, 3*size); // Oberschenkel
            g.fillRect(x - 5*size, y + 4*size, 2*size, 4*size); // Unterschenkel
            g.fillRect(x - 6*size, y + 7*size, 2*size, 1*size); // Huf
            
            g.fillRect(x + 9*size, y + 1*size, 2*size, 3*size); 
            g.fillRect(x + 11*size, y + 4*size, 2*size, 3*size);
            g.fillRect(x + 12*size, y + 7*size, 2*size, 1*size);
        } else {
            g.fillRect(x + 1*size, y + 1*size, 2*size, 4*size);
            g.fillRect(x + 0*size, y + 5*size, 2*size, 3*size);
            
            g.fillRect(x + 6*size, y + 1*size, 2*size, 3*size);
            g.fillRect(x + 5*size, y + 4*size, 2*size, 3*size);
        }

        // --- 2. SCHWEIF ---
        g.setColor(new Color(20, 20, 20));
        g.fillRect(x - 5*size, y - 5*size, 2*size, 6*size);
        g.fillRect(x - 6*size, y - 2*size, 2*size, 5*size);
        g.setColor(new Color(60, 60, 60)); // Highlight
        g.fillRect(x - 5*size, y - 4*size, 1*size, 4*size);

        // --- 3. KÖRPER ---
        g.setColor(baseColor);
        g.fillRect(x - 4*size, y - 6*size, 14*size, 6*size); // main body
        g.fillRect(x - 5*size, y - 5*size, 2*size, 4*size);  // rump
        g.fillRect(x + 10*size, y - 5*size, 2*size, 4*size); // chest
        
        g.setColor(shadow);
        g.fillRect(x - 4*size, y - 1*size, 14*size, 1*size); // bauch unten
        g.fillRect(x - 5*size, y - 1*size, 1*size, 1*size);
        g.fillRect(x + 10*size, y - 1*size, 1*size, 1*size);
        g.fillRect(x - 3*size, y - 4*size, 2*size, 3*size); // muskel-schatten
        g.fillRect(x + 8*size, y - 4*size, 2*size, 3*size);

        g.setColor(highlight);
        g.fillRect(x - 4*size, y - 6*size, 14*size, 1*size); // rücken oben
        g.fillRect(x - 5*size, y - 5*size, 1*size, 1*size);

        // --- 4. HALS & KOPF ---
        g.setColor(baseColor);
        g.fillRect(x + 8*size, y - 10*size, 4*size, 5*size);
        g.fillRect(x + 11*size, y - 8*size, 2*size, 4*size);
        g.fillRect(x + 9*size, y - 13*size, 5*size, 3*size); 
        g.fillRect(x + 14*size, y - 12*size, 3*size, 2*size);
        g.fillRect(x + 9*size, y - 15*size, 1*size, 2*size); // ohren
        g.fillRect(x + 11*size, y - 14*size, 1*size, 1*size);
        
        g.setColor(new Color(30, 30, 30)); // mähne
        g.fillRect(x + 7*size, y - 11*size, 2*size, 6*size);
        g.fillRect(x + 8*size, y - 13*size, 1*size, 2*size);

        g.setColor(shadow);
        g.fillRect(x + 8*size, y - 5*size, 4*size, 1*size);
        g.fillRect(x + 10*size, y - 10*size, 2*size, 1*size);
        g.fillRect(x + 14*size, y - 10*size, 2*size, 1*size);
        
        g.setColor(highlight);
        g.fillRect(x + 8*size, y - 10*size, 1*size, 3*size); 
        g.fillRect(x + 9*size, y - 13*size, 5*size, 1*size); 
        g.fillRect(x + 14*size, y - 12*size, 3*size, 1*size);

        // Auge & Zügel
        g.setColor(Color.WHITE);
        g.fillRect(x + 12*size, y - 12*size, 2*size, 1*size);
        g.setColor(Color.BLACK);
        g.fillRect(x + 13*size, y - 12*size, 1*size, 1*size);
        g.setColor(new Color(100, 50, 20));
        g.fillRect(x + 12*size, y - 10*size, 3*size, 1*size);
        g.fillRect(x + 14*size, y - 12*size, 1*size, 3*size);

        // --- 5. VORDERE BEINE ---
        g.setColor(new Color(40, 40, 40));
        if (legsOpen) {
            g.fillRect(x - 1*size, y + 0*size, 3*size, 4*size);
            g.fillRect(x - 3*size, y + 4*size, 2*size, 4*size);
            g.fillRect(x - 4*size, y + 7*size, 2*size, 1*size); 
            
            g.fillRect(x + 7*size, y + 0*size, 3*size, 4*size);
            g.fillRect(x + 9*size, y + 4*size, 2*size, 3*size);
            g.fillRect(x + 10*size, y + 7*size, 2*size, 1*size);
        } else {
            g.fillRect(x + 3*size, y + 0*size, 3*size, 4*size);
            g.fillRect(x + 2*size, y + 4*size, 2*size, 4*size);
            g.fillRect(x + 1*size, y + 7*size, 2*size, 1*size);
            
            g.fillRect(x + 4*size, y + 0*size, 3*size, 4*size);
            g.fillRect(x + 7*size, y + 4*size, 2*size, 3*size);
            g.fillRect(x + 8*size, y + 7*size, 2*size, 1*size);
        }
        g.setColor(new Color(80, 80, 80)); // Highlight
        if (legsOpen) {
            g.fillRect(x - 1*size, y + 0*size, 1*size, 3*size);
            g.fillRect(x + 7*size, y + 0*size, 1*size, 3*size);
        } else {
            g.fillRect(x + 3*size, y + 0*size, 1*size, 3*size);
            g.fillRect(x + 4*size, y + 0*size, 1*size, 3*size);
        }
        g.setColor(Color.BLACK); // Hufe
        if (legsOpen) {
            g.fillRect(x - 4*size, y + 7*size, 2*size, 1*size);
            g.fillRect(x + 10*size, y + 7*size, 2*size, 1*size);
        } else {
            g.fillRect(x + 1*size, y + 7*size, 2*size, 1*size);
            g.fillRect(x + 8*size, y + 7*size, 2*size, 1*size);
        }

        // --- 6. SATTEL ---
        g.setColor(new Color(139, 69, 19)); 
        g.fillRect(x - 1*size, y - 7*size, 5*size, 2*size);
        g.fillRect(x + 0*size, y - 5*size, 3*size, 2*size); 
        g.setColor(new Color(100, 40, 10)); 
        g.fillRect(x - 1*size, y - 5*size, 5*size, 1*size);

        // --- 7. JOCKEY ---
        g.setColor(Color.WHITE); // Hosen
        g.fillRect(x + 0*size, y - 6*size, 3*size, 3*size); 
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x + 0*size, y - 4*size, 3*size, 1*size); 
        
        g.setColor(Color.BLACK); // Stiefel
        g.fillRect(x + 1*size, y - 3*size, 2*size, 2*size);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x + 2*size, y - 3*size, 1*size, 2*size);

        g.setColor(shirt); // Shirt
        g.fillRect(x - 1*size, y - 11*size, 5*size, 5*size);
        g.setColor(highlightColor(shirt)); 
        g.fillRect(x - 1*size, y - 11*size, 5*size, 1*size);
        g.fillRect(x + 3*size, y - 10*size, 1*size, 4*size);
        g.setColor(shirt.darker()); 
        g.fillRect(x - 1*size, y - 7*size, 5*size, 1*size);
        
        g.setColor(shirt); // Ärmel
        g.fillRect(x + 2*size, y - 9*size, 4*size, 2*size); 
        g.setColor(shirt.darker());
        g.fillRect(x + 2*size, y - 7*size, 4*size, 1*size);
        
        g.setColor(skin); // Hände & Gesicht
        g.fillRect(x + 6*size, y - 9*size, 2*size, 2*size);
        g.fillRect(x + 1*size, y - 13*size, 3*size, 3*size);
        g.setColor(skin.darker()); 
        g.fillRect(x + 1*size, y - 11*size, 3*size, 1*size);
        
        g.setColor(helmet); // Helm
        g.fillRect(x + 0*size, y - 14*size, 4*size, 2*size);
        g.fillRect(x + 3*size, y - 14*size, 2*size, 1*size);
        g.setColor(highlightColor(helmet)); 
        g.fillRect(x + 0*size, y - 14*size, 3*size, 1*size);

        g.setColor(Color.DARK_GRAY); // Brille
        g.fillRect(x + 2*size, y - 13*size, 2*size, 1*size);
    }
    
    private Color highlightColor(Color c) {
        return new Color(
            Math.min(255, c.getRed() + 60),
            Math.min(255, c.getGreen() + 60),
            Math.min(255, c.getBlue() + 60)
        );
    }
}

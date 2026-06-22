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

        public Billboard(int x, int y, int w, int h, Color bg, Color fg, String t, String st) {
            this.x = x; this.y = y; this.width = w; this.height = h;
            this.bg = bg; this.fg = fg; this.text = t; this.subText = st;
        }
    }
    
    private static class Brand {
        String name, subText;
        Color bg, fg;
        public Brand(String name, String subText, Color bg, Color fg) {
            this.name = name; this.subText = subText; this.bg = bg; this.fg = fg;
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
    
    private List<Billboard> billboards = new ArrayList<>();
    private List<DustParticle> dustParticles = new ArrayList<>();
    private int lastWidth = 0;

    public void setHorses(List<Horse> horses, double[] progress) {
        this.horses = horses;
        this.progress = progress;
        this.dustParticles.clear(); // Staub beim Neustart entfernen
        repaint();
    }
    
    public void updateParticles() {
        // Partikel aktualisieren und alte entfernen
        Iterator<DustParticle> it = dustParticles.iterator();
        while (it.hasNext()) {
            DustParticle p = it.next();
            p.update();
            if (p.life <= 0) it.remove();
        }
        
        if (horses != null && progress != null) {
            int width = getWidth();
            int topOffset = 70;
            int trackHeight = getHeight() - topOffset;
            
            if(trackHeight > 0) {
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
        allBrands.add(new Brand("McDowell's", "I'm tolerating it", new Color(220, 20, 20), new Color(255, 204, 0))); 
        allBrands.add(new Brand("Moonbucks", "Overpriced Coffee", new Color(0, 112, 74), Color.WHITE)); 
        allBrands.add(new Brand("Koka-Kola", "Enjoy Diabetes", new Color(244, 0, 9), Color.WHITE)); 
        allBrands.add(new Brand("Adios", "Just do it tomorrow", new Color(0, 81, 186), Color.WHITE)); 
        allBrands.add(new Brand("Pear", "Think Identical", new Color(240, 240, 240), Color.BLACK)); 
        allBrands.add(new Brand("Burger Tsar", "Have it our way", new Color(242, 169, 0), new Color(218, 41, 28))); 
        allBrands.add(new Brand("Subpar", "Eat Freshly Frozen", new Color(0, 137, 56), new Color(255, 203, 10))); 
        allBrands.add(new Brand("Dead Bull", "Gives you wings... maybe", new Color(30, 60, 110), Color.LIGHT_GRAY)); 
        allBrands.add(new Brand("Relax", "Fake Luxury Watches", new Color(0, 96, 57), new Color(201, 160, 80))); 
        allBrands.add(new Brand("KFD", "Kentucky Fried Dog", new Color(163, 13, 20), Color.WHITE)); 
        allBrands.add(new Brand("Gargle", "We know everything", new Color(255, 255, 255), new Color(66, 133, 244))); 
        allBrands.add(new Brand("Heinieken", "Probably Beer", new Color(0, 130, 32), Color.WHITE)); 
        
        Collections.shuffle(allBrands, new Random(42)); 
        Random rnd = new Random(42); 
        
        int currentX = 20;
        int brandIndex = 0;
        
        while (brandIndex < allBrands.size()) {
            int w = 150 + rnd.nextInt(50); 
            
            // Prüfen, ob das Plakat noch komplett auf den Bildschirm passt
            if (currentX + w > width - 20) {
                break; 
            }
            
            Brand brand = allBrands.get(brandIndex);
            billboards.add(new Billboard(currentX, 10, w, 40, brand.bg, brand.fg, brand.name, brand.subText));
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
        for(int x = 0; x < width; x += 15) {
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
            
            g2d.setColor(b.fg);
            g2d.setFont(new Font("Impact", Font.ITALIC, 16));
            FontMetrics fm = g2d.getFontMetrics();
            int tX = b.x + (b.width - fm.stringWidth(b.text)) / 2;
            g2d.drawString(b.text, tX, b.y + 18);
            
            g2d.setFont(new Font("Courier New", Font.BOLD, 10));
            fm = g2d.getFontMetrics();
            tX = b.x + (b.width - fm.stringWidth(b.subText)) / 2;
            g2d.drawString(b.subText, tX, b.y + 32);
            
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.drawLine(b.x + 3, b.y + 3, b.width - 4, b.y + 3);
        }

        // --- 2. RENNSTRECKE ---
        int trackHeight = height - topOffset;
        if (horses == null || progress == null || trackHeight <= 0) return;
        
        int laneHeight = trackHeight / horses.size();

        // Gras zeichnen
        g2d.setColor(new Color(25, 60, 25)); 
        g2d.fillRect(0, topOffset, width, trackHeight);

        // STARTLINIE (Weiß, durch alle Bahnen)
        int startX = 50;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(startX + 25, topOffset, 4, trackHeight); // +25 damit die Schnauze beim Start an der Linie ist
        
        // Start-Text auf dem Rasen (Vertikal)
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
        for(int i = topOffset; i < height; i += 16) {
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
            g2d.setColor(new Color(200, 200, 200));
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2d.drawString(String.valueOf(i + 1), 15, y + laneHeight / 2 + 6);
        }
        
        // Staubpartikel zeichnen (Hinter den Pferden, vor der Bahn)
        for (DustParticle p : dustParticles) {
            int alpha = (int)(p.life * 180); 
            if (alpha < 0) alpha = 0;
            if (alpha > 255) alpha = 255;
            g2d.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), alpha));
            int s = (int)p.size;
            g2d.fillOval((int)p.x, (int)p.y, s, s);
        }

        // Pferde zeichnen
        for (int i = 0; i < horses.size(); i++) {
            int y = topOffset + i * laneHeight;
            int horseX = startX + (int) ((progress[i] / 100.0) * (finishX - startX - 25)); // -25 wg. Pferdelänge

            g2d.setColor(new Color(50, 30, 20)); 
            g2d.fillOval(horseX, y + laneHeight / 2 + 10, 30, 8);

            drawPixelHorse(g2d, horseX, y + laneHeight / 2 + 5, horses.get(i).getColor());
        }
    }

    private void drawPixelHorse(Graphics2D g, int x, int y, Color color) {
        int size = 4; 
        boolean legsOpen = (x % 20) < 10;
        
        g.setColor(color);
        g.fillRect(x, y - 2*size, 6*size, 3*size);
        g.fillRect(x + 5*size, y - 4*size, 2*size, 3*size);
        g.fillRect(x + 7*size, y - 3*size, 1*size, 1*size);
        
        g.setColor(new Color(30, 30, 30)); 
        if (legsOpen) {
            g.fillRect(x + 1*size, y + 1*size, 1*size, 2*size);
            g.fillRect(x + 5*size, y + 1*size, 1*size, 2*size);
        } else {
            g.fillRect(x + 2*size, y + 1*size, 1*size, 2*size);
            g.fillRect(x + 4*size, y + 1*size, 1*size, 2*size);
        }
        
        g.setColor(new Color(20, 20, 20));
        g.fillRect(x - 1*size, y - 1*size, 1*size, 2*size);
        
        g.setColor(Color.WHITE);
        g.fillRect(x + 6*size, y - 4*size, 1*size, 1*size);
        g.setColor(Color.BLACK);
        g.fillRect(x + 6*size, y - 4*size, 1, 1);
        
        g.setColor(new Color(0, 150, 255)); 
        g.fillRect(x + 2*size, y - 4*size, 2*size, 2*size);
        g.setColor(new Color(255, 200, 200)); 
        g.fillRect(x + 3*size, y - 5*size, 1*size, 1*size);
    }
}

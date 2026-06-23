package javaSpiel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameUI extends JFrame {
    private RaceLogik raceLogik;
    private BettingSystem bettingSystem;

    // UI Komponenten
    private JLabel balanceLabel;
    private JLabel breakingNewsLabel;
    private JPanel horsesPanel;
    private RaceTrackPanel raceTrackPanel;
    private JTextArea logArea;
    private JButton startButton;
    private JTextField betInputField;
    private JButton placeBetButton;

    private List<JPanel> horseRowPanels;
    private int selectedHorseIndex = -1;

    private Timer raceTimer;
    private Timer postRaceTimer;
    private double[] currentProgress;

    private Random random = new Random();
    private int commentCooldown = 0;

    // Farbkonzept - 90s DOS Arcade Theme
    private final Color COLOR_BG = Color.BLACK;
    private final Color COLOR_CARD_BG = new Color(0, 0, 170); // Classic DOS Blue
    private final Color COLOR_TEXT_PRIMARY = Color.WHITE;
    private final Color COLOR_TEXT_SECONDARY = Color.CYAN;
    private final Color COLOR_PRIMARY = Color.YELLOW;
    private final Color COLOR_ACCENT = Color.MAGENTA;
    private final Color COLOR_DANGER = Color.RED;
    private final Color COLOR_HOVER = new Color(0, 0, 255); // Bright Blue
    private final Color COLOR_SELECTED = new Color(170, 0, 170); // DOS Purple

    public GameUI() {
        raceLogik = new RaceLogik();
        bettingSystem = new BettingSystem(1000.0);

        setTitle("Vinewood Inside Track - Modern UI");
        setSize(1250, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(15, 15));

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
        prepareNewRace();
    }

    private void initUI() {
        // --- Top-Panel: Balance ---
        JPanel topPanel = createCardPanel("");
        topPanel.setLayout(new BorderLayout());

        balanceLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 70));
                int h = getHeight();
                int w = getWidth();
                for (int y = 0; y < h; y += 3) {
                    g2.drawLine(0, y, w, y);
                }
            }
        };
        balanceLabel.setOpaque(true);
        balanceLabel.setBackground(new Color(5, 15, 5));
        balanceLabel.setForeground(new Color(50, 255, 50));
        balanceLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        balanceLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 40, 40), 4),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));

        topPanel.add(balanceLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- Center-Panel ---
        JPanel mainCenterPanel = new JPanel(new BorderLayout(15, 15));
        mainCenterPanel.setBackground(COLOR_BG);

        // --- BREAKING NEWS BANNER ---
        JPanel newsPanel = new JPanel(new BorderLayout());
        newsPanel.setBackground(new Color(220, 10, 30));
        breakingNewsLabel = new JLabel("🎤 LIVE: WILLKOMMEN IN VINEWOOD!");
        breakingNewsLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        breakingNewsLabel.setForeground(Color.YELLOW);
        breakingNewsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        newsPanel.add(breakingNewsLabel, BorderLayout.CENTER);

        Border yellowMatte = BorderFactory.createMatteBorder(3, 0, 3, 0, new Color(255, 215, 0));
        Border emptyPadding = BorderFactory.createEmptyBorder(12, 15, 12, 15);
        newsPanel.setBorder(BorderFactory.createCompoundBorder(yellowMatte, emptyPadding));

        mainCenterPanel.add(newsPanel, BorderLayout.NORTH);

        // --- Split für Wettbüro und Strecke ---
        JPanel splitPanel = new JPanel(new BorderLayout(15, 0));
        splitPanel.setBackground(COLOR_BG);

        // Left: Wettbüro
        horsesPanel = createCardPanel("Wettbüro");
        horsesPanel.setPreferredSize(new Dimension(600, 0));

        // Right: Rennstrecke
        raceTrackPanel = new RaceTrackPanel();
        raceTrackPanel.setBackground(COLOR_CARD_BG);

        JPanel trackContainer = createCardPanel("Rennstrecke");
        trackContainer.add(raceTrackPanel, BorderLayout.CENTER);

        splitPanel.add(horsesPanel, BorderLayout.WEST);
        splitPanel.add(trackContainer, BorderLayout.CENTER);

        mainCenterPanel.add(splitPanel, BorderLayout.CENTER);
        add(mainCenterPanel, BorderLayout.CENTER);

        // --- Bottom-Panel: Log und Start ---
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 15));
        bottomPanel.setBackground(COLOR_BG);

        JPanel logCard = createCardPanel("System-Terminal");

        // Custom JTextArea für CRT Scanlines-Effekt
        logArea = new JTextArea(5, 50) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                // Dezent transparente Scanlines über den Text zeichnen
                g2.setColor(new Color(0, 0, 0, 70));
                int h = getHeight();
                int w = getWidth();
                for (int y = 0; y < h; y += 3) {
                    g2.drawLine(0, y, w, y);
                }
            }
        };
        logArea.setEditable(false);
        // Typisches CRT Phosphor-Grün auf extrem dunklem Hintergrund
        Color crtGreen = new Color(50, 255, 50);
        logArea.setBackground(new Color(5, 15, 5));
        logArea.setForeground(crtGreen);
        logArea.setCaretColor(crtGreen);
        logArea.setFont(new Font("Monospaced", Font.BOLD, 15));
        logArea.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JScrollPane scrollPane = new JScrollPane(logArea);
        // Bildschirm-Gehäuse/Rand (Monitor-Bezel)
        Border bezelInner = BorderFactory.createLoweredBevelBorder();
        Border bezelOuter = BorderFactory.createLineBorder(new Color(40, 40, 40), 5);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(bezelOuter, bezelInner));

        logCard.add(scrollPane, BorderLayout.CENTER);

        startButton = new JButton("START") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                boolean enabled = isEnabled();
                boolean pressed = getModel().isPressed();
                boolean hover = getModel().isRollover();

                Color bezel = new Color(20, 20, 20);
                Color bezelHighlight = new Color(60, 60, 60);
                
                Color baseColor = enabled ? (hover ? new Color(255, 30, 30) : new Color(220, 10, 10)) : new Color(80, 20, 20);
                Color glowColor = enabled ? (hover ? new Color(255, 120, 120) : new Color(255, 80, 80)) : new Color(100, 40, 40);

                // Hintergrund füllen, um Artefakte zu vermeiden
                g2.setColor(getParent().getBackground());
                g2.fillRect(0, 0, w, h);

                // Bezel (äußerer schwarzer/grauer Rand aus Kunststoff)
                g2.setColor(bezelHighlight);
                g2.fillRoundRect(10, 10, w - 20, h - 20, 30, 30);
                g2.setColor(bezel);
                g2.fillRoundRect(13, 13, w - 26, h - 26, 25, 25);

                int offset = pressed ? 6 : 0; // Beim Drücken geht der Knopf spürbar nach unten

                // Knopf-Schatten/Tiefe
                g2.setColor(new Color(100, 0, 0));
                g2.fillRoundRect(18, 18, w - 36, h - 36, 20, 20);

                // Knopf-Oberfläche
                GradientPaint gp = new GradientPaint(0, 18 + offset, glowColor, 0, h - 26 + offset, baseColor);
                g2.setPaint(gp);
                g2.fillRoundRect(18, 18 + offset, w - 36, h - 44, 20, 20);

                // Plastik/Glas-Glanz (obere Hälfte glänzt)
                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillRoundRect(22, 20 + offset, w - 44, (h - 44) / 3, 15, 15);

                // Blink-Effekt für den Arcade Automaten Look
                boolean blink = enabled && (System.currentTimeMillis() % 1000 < 500);
                
                g2.setFont(new Font("Monospaced", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int tx = (w - fm.stringWidth(text)) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent() + offset - 4;

                // Leuchtende Schrift
                if (enabled) {
                    if (blink) {
                        g2.setColor(new Color(255, 255, 180, 200)); // Helles gelbliches Leuchten
                        g2.drawString(text, tx - 1, ty - 1);
                        g2.drawString(text, tx + 1, ty + 1);
                        g2.setColor(Color.WHITE);
                    } else {
                        g2.setColor(new Color(255, 180, 180, 100)); // Schwaches rotes Leuchten
                        g2.drawString(text, tx - 1, ty - 1);
                        g2.drawString(text, tx + 1, ty + 1);
                        g2.setColor(new Color(255, 220, 220));
                    }
                } else {
                    g2.setColor(Color.GRAY);
                }
                
                g2.drawString(text, tx, ty);
                g2.dispose();
            }
        };
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setEnabled(false);
        startButton.setPreferredSize(new Dimension(250, 0));
        startButton.addActionListener(e -> startRace());

        // Timer
        raceTimer = new Timer(50, e -> updateRaceTick());
        postRaceTimer = new Timer(50, e -> {
            raceTrackPanel.updateParticles();
            raceTrackPanel.repaint();
        });

        // Repaint Timer für das Blinken des Buttons
        new javax.swing.Timer(100, e -> {
            if (startButton.isEnabled()) startButton.repaint();
        }).start();

        bottomPanel.add(logCard, BorderLayout.CENTER);
        bottomPanel.add(startButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Timer
        raceTimer = new Timer(50, e -> updateRaceTick());
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(COLOR_CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 3),
                BorderFactory.createEmptyBorder(12, 17, 12, 17)));

        if (!title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
            titleLabel.setForeground(Color.YELLOW);
            panel.add(titleLabel, BorderLayout.NORTH);
        }

        return panel;
    }

    private void styleArcadeButton(JButton btn, Color bgColor, Color fgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFont(new Font("Monospaced", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(bgColor.brighter());
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(bgColor);
                }
            }
        });
    }

    private void prepareNewRace() {
        if (postRaceTimer != null) {
            postRaceTimer.stop();
        }
        if (raceTrackPanel != null) {
            raceTrackPanel.setMoneyRain(false);
        }

        raceLogik.prepareNewRace();
        updateBalanceLabel();

        selectedHorseIndex = -1;
        horseRowPanels = new ArrayList<>();

        BorderLayout layout = (BorderLayout) horsesPanel.getLayout();
        Component centerComp = layout.getLayoutComponent(BorderLayout.CENTER);
        if (centerComp != null)
            horsesPanel.remove(centerComp);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_CARD_BG);

        currentProgress = new double[raceLogik.getHorses().size()];
        raceTrackPanel.setHorses(raceLogik.getHorses(), currentProgress);

        List<Horse> horses = raceLogik.getHorses();
        List<Double> odds = raceLogik.getCurrentOdds();

        // --- Interaktive Klickbare Reihen ---
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.setBackground(COLOR_CARD_BG);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_CARD_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        JLabel h1 = new JLabel("Pferd");
        h1.setFont(new Font("Monospaced", Font.BOLD, 16));
        h1.setForeground(COLOR_PRIMARY);
        headerPanel.add(h1, BorderLayout.WEST);

        JLabel h2 = new JLabel("Quote", SwingConstants.CENTER);
        h2.setFont(new Font("Monospaced", Font.BOLD, 16));
        h2.setForeground(COLOR_PRIMARY);
        h2.setPreferredSize(new Dimension(80, 30));
        headerPanel.add(h2, BorderLayout.EAST);

        tablePanel.add(headerPanel);

        // Pferde-Zeilen (Klickbar)
        for (int i = 0; i < horses.size(); i++) {
            Horse h = horses.get(i);
            final int currentIndex = i;

            JPanel rowPanel = new JPanel(new BorderLayout());
            rowPanel.setBackground(COLOR_CARD_BG);
            rowPanel.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 15));
            rowPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Name + Pixel-Portrait
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            namePanel.setOpaque(false);

            JLabel portraitLabel = new JLabel(createHorsePortrait(h, 24, 24));
            JLabel nameLabel = new JLabel((i + 1) + ". " + h.getName());
            nameLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
            nameLabel.setForeground(COLOR_TEXT_PRIMARY);

            namePanel.add(portraitLabel);
            namePanel.add(nameLabel);
            rowPanel.add(namePanel, BorderLayout.WEST);

            // Quote
            JLabel oddsLabel = new JLabel(String.valueOf(odds.get(i)), SwingConstants.CENTER);
            oddsLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
            oddsLabel.setForeground(COLOR_PRIMARY);
            oddsLabel.setPreferredSize(new Dimension(80, 30));
            rowPanel.add(oddsLabel, BorderLayout.EAST);

            // Hover & Click Logic
            rowPanel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    if (selectedHorseIndex != currentIndex && placeBetButton.isEnabled()) {
                        rowPanel.setBackground(COLOR_HOVER);
                    }
                }

                public void mouseExited(MouseEvent evt) {
                    if (selectedHorseIndex != currentIndex && placeBetButton.isEnabled()) {
                        rowPanel.setBackground(COLOR_CARD_BG);
                    }
                }

                public void mouseClicked(MouseEvent evt) {
                    if (placeBetButton.isEnabled()) {
                        selectedHorseIndex = currentIndex;
                        updateHorseSelection();
                    }
                }
            });

            horseRowPanels.add(rowPanel);
            tablePanel.add(rowPanel);
        }

        contentPanel.add(tablePanel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Gesamter Einsatz-Bereich als ein CRT-Terminal
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)) {
            @Override
            protected void paintChildren(Graphics g) {
                super.paintChildren(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 70));
                int h = getHeight();
                int w = getWidth();
                for (int y = 0; y < h; y += 3) {
                    g2.drawLine(0, y, w, y);
                }
            }
        };
        inputPanel.setBackground(new Color(5, 15, 5));
        Border inputBezelInner = BorderFactory.createLoweredBevelBorder();
        Border inputBezelOuter = BorderFactory.createLineBorder(new Color(40, 40, 40), 4);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(inputBezelOuter, inputBezelInner));

        Color crtGreen = new Color(50, 255, 50);

        JLabel betLabel = new JLabel("EINSATZ: $");
        betLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        betLabel.setForeground(crtGreen);
        inputPanel.add(betLabel);

        betInputField = new JTextField("10", 8);
        betInputField.setBackground(new Color(5, 15, 5));
        betInputField.setForeground(crtGreen);
        betInputField.setFont(new Font("Monospaced", Font.BOLD, 18));
        betInputField.setHorizontalAlignment(JTextField.CENTER);
        betInputField.setCaretColor(crtGreen);
        betInputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(crtGreen, 1),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        inputPanel.add(betInputField);

        placeBetButton = new JButton("[ WETTE PLATZIEREN ]");
        placeBetButton.setBackground(new Color(5, 15, 5));
        placeBetButton.setForeground(crtGreen);
        placeBetButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        placeBetButton.setFocusPainted(false);
        placeBetButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(crtGreen, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        placeBetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        placeBetButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (placeBetButton.isEnabled()) {
                    placeBetButton.setBackground(crtGreen);
                    placeBetButton.setForeground(new Color(5, 15, 5));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (placeBetButton.isEnabled()) {
                    placeBetButton.setBackground(new Color(5, 15, 5));
                    placeBetButton.setForeground(crtGreen);
                }
            }
        });
        placeBetButton.addActionListener(e -> placeBet());
        inputPanel.add(placeBetButton);

        contentPanel.add(inputPanel);
        horsesPanel.add(contentPanel, BorderLayout.CENTER);

        horsesPanel.revalidate();
        horsesPanel.repaint();

        for (ActionListener al : startButton.getActionListeners()) {
            startButton.removeActionListener(al);
        }
        startButton.addActionListener(e -> startRace());

        startButton.setText("Rennen starten");
        startButton.setEnabled(true);
        startButton.setBackground(COLOR_DANGER);

        breakingNewsLabel.setText("🎤 WARTEN AUF DEN START DES NÄCHSTEN RENNENS...");
    }

    private void updateHorseSelection() {
        for (int i = 0; i < horseRowPanels.size(); i++) {
            JPanel p = horseRowPanels.get(i);
            if (i == selectedHorseIndex) {
                p.setBackground(COLOR_SELECTED);
                p.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_PRIMARY, 1),
                        BorderFactory.createEmptyBorder(7, 4, 7, 14)));
            } else {
                p.setBackground(COLOR_CARD_BG);
                p.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 15));
            }
        }
    }

    // Kleines detailliertes Pixel-Portrait des Pferdes für das Wettbüro
    private Icon createHorsePortrait(Horse horse, int width, int height) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                // Hintergrund-Kästchen
                g2.setColor(new Color(20, 20, 20));
                g2.fillRoundRect(x, y, width, height, 4, 4);

                Color color = horse.getColor();

                // Pferdekopf (Profil) - Leicht zentriert
                g2.setColor(color);
                g2.fillRect(x + 5, y + 7, 8, 12); // Hals
                g2.fillRect(x + 8, y + 4, 7, 5);  // Stirn/Kopf oben
                g2.fillRect(x + 14, y + 6, 6, 5); // Schnauze
                
                g2.fillRect(x + 8, y + 1, 2, 4);  // Ohren

                // Highlight für 3D-Look
                Color highlight = color.brighter();
                if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50) {
                    highlight = new Color(80, 80, 80);
                }
                g2.setColor(highlight);
                g2.fillRect(x + 8, y + 4, 7, 1);  // Kopf Highlight
                g2.fillRect(x + 14, y + 6, 6, 1); // Schnauze Highlight
                
                // Mähne
                g2.setColor(new Color(30, 30, 30));
                g2.fillRect(x + 3, y + 4, 4, 15);

                // Auge
                g2.setColor(Color.WHITE);
                g2.fillRect(x + 12, y + 6, 2, 2);
                g2.setColor(Color.BLACK);
                g2.fillRect(x + 13, y + 6, 1, 1);

                // Halfter / Zügel
                g2.setColor(new Color(100, 50, 20));
                g2.fillRect(x + 11, y + 8, 5, 2);
                g2.fillRect(x + 15, y + 6, 2, 5);
            }

            @Override
            public int getIconWidth() {
                return width;
            }

            @Override
            public int getIconHeight() {
                return height;
            }
        };
    }

    private void placeBet() {
        if (selectedHorseIndex == -1) {
            JOptionPane.showMessageDialog(this, "Bitte klicke auf eine Reihe, um ein Pferd auszuwählen!");
            return;
        }

        try {
            double amount = Double.parseDouble(betInputField.getText());
            if (bettingSystem.placeBet(selectedHorseIndex, amount)) {
                updateBalanceLabel();
                placeBetButton.setEnabled(false);
                placeBetButton.setBackground(new Color(15, 35, 15));
                placeBetButton.setForeground(new Color(30, 100, 30));
                betInputField.setEnabled(false);

                // Die Auswahl fixieren (Hover-Effekte stoppen, Cursor zurücksetzen)
                for (JPanel p : horseRowPanels) {
                    p.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                String name = raceLogik.getHorses().get(selectedHorseIndex).getName();
                log(">>> Wette platziert: " + amount + " $ auf " + name);
                breakingNewsLabel.setText("💰 WETTE AUF " + name.toUpperCase() + " WURDE ANGENOMMEN!");
            } else {
                JOptionPane.showMessageDialog(this, "Ungültiger Einsatz oder nicht genug Guthaben!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bitte eine gültige Zahl eingeben!");
        }
    }

    private void startRace() {
        startButton.setEnabled(false);
        startButton.setBackground(new Color(60, 60, 60));
        placeBetButton.setEnabled(false);
        placeBetButton.setBackground(new Color(15, 35, 15));
        placeBetButton.setForeground(new Color(30, 100, 30));
        betInputField.setEnabled(false);
        for (JPanel p : horseRowPanels)
            p.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        logArea.setText("");
        log("Rennen gestartet! Die Boxen öffnen sich...");
        breakingNewsLabel.setText("🚨 DAS RENNEN STARTET! DIE BOXEN SIND OFFEN!");
        commentCooldown = 30;
        raceTimer.start();
    }

    private void updateRaceTick() {
        List<Horse> horses = raceLogik.getHorses();
        boolean raceFinished = false;
        int winningIndex = -1;

        for (int i = 0; i < horses.size(); i++) {
            currentProgress[i] += raceLogik.calculateTickProgress(horses.get(i));

            if (currentProgress[i] >= 100.0) {
                currentProgress[i] = 100.0;
                raceFinished = true;
                if (winningIndex == -1) {
                    winningIndex = i;
                }
            }
        }

        raceTrackPanel.updateParticles(); // Staub updaten und erzeugen
        raceTrackPanel.repaint();

        if (!raceFinished) {
            if (commentCooldown > 0) {
                commentCooldown--;
            } else {
                if (random.nextDouble() < 0.03) {
                    generateCommentary(horses);
                    commentCooldown = 50;
                }
            }
        }

        if (raceFinished) {
            raceTimer.stop();
            finishRace(winningIndex);
        }
    }

    private void generateCommentary(List<Horse> horses) {
        Horse h = horses.get(random.nextInt(horses.size()));
        String name = h.getName();
        String comment = "";

        switch (name) {
            case "Tax Evasion":
                comment = name + " legt einen Zahn zu – das Finanzamt hat den Steuerbescheid geschickt!";
                break;
            case "OnlyFoals":
                comment = name + " verlangsamt das Tempo, um ein Selfie für die Subscriber zu machen.";
                break;
            case "Pony Soprano":
                comment = name + " macht der Konkurrenz ein Angebot, das sie nicht ablehnen kann.";
                break;
            case "Glue Factory":
                comment = name + " rennt um sein Leben – der Transporter hat gewinkt!";
                break;
            case "Divorce Settlement":
                comment = name + " nimmt die halbe Rennstrecke für sich in Anspruch!";
                break;
            case "Sugar Daddy":
                comment = name + " wird plötzlich von jüngeren Stuten umschwärmt und verliert den Fokus.";
                break;
            case "Bad Investment":
                comment = name + " stürzt gnadenlos ab, wie der Krypto-Markt!";
                break;
            case "Al Capony":
                comment = name + " hat angeblich die Jury geschmiert. Wir warten auf den VAR.";
                break;
            case "Hoof Hearted":
                comment = "Der Jockey von " + name + " hält sich verdächtig oft die Nase zu.";
                break;
            case "Bitcoin Crash":
                comment = name + " war kurz vorne und ist jetzt komplett im Keller.";
                break;
            case "Elon's Musk":
                comment = name + " fliegt fast zum Mars, aber verfehlt dann doch die Kurve.";
                break;
            case "Zucker-Burger":
                comment = name + " trackt gerade die Daten der anderen Pferde.";
                break;
            case "Influencer Tears":
                comment = name + " hält an und verlangt Sponsoring für den weiteren Lauf!";
                break;
            case "Student Loan":
                comment = name + " holt dich unweigerlich ein, egal wie schnell du rennst.";
                break;
            case "Wi-Fi Password":
                comment = "Niemand weiß, wo " + name + " ist. Wahrscheinlich auf der Rückseite des Routers.";
                break;
            case "404 Not Found":
                comment = name + " ist plötzlich spurlos von der Rennstrecke verschwunden!";
                break;
            case "Clickbait":
                comment = "Du wirst nicht glauben, was " + name + " an der Ziellinie tut!!!";
                break;
            case "Terms & Conditions":
                comment = "Niemand achtet auf " + name + ", aber am Ende gewinnt er immer.";
                break;
            case "Spam Folder":
                comment = name + " ist voller Überraschungen, aber keiner will sie sehen.";
                break;
            case "NFT Bubble":
                comment = name + " sah kurz nach Millionen aus, ist jetzt aber absolut wertlos.";
                break;
            case "Ctrl Alt Defeat":
                comment = name + " stürzt ab. Ein Neustart ist erforderlich.";
                break;
            case "Blue Screen":
                comment = name + " bleibt einfach stehen und verlangt einen System-Admin.";
                break;
            case "Mane Character":
                comment = name + " drängt sich extrem unangenehm in den Mittelpunkt.";
                break;
            case "Crypto Bro":
                comment = name + " erzählt den anderen Pferden, dass sie in Dogecoin investieren sollen.";
                break;
            case "Wife's Alibi":
                comment = name + " war angeblich zur Zeit des Rennens im Büro.";
                break;
            case "Midlife Crisis":
                comment = name + " hat sich eine teure Satteltasche gekauft und rast jetzt kopflos umher.";
                break;
            case "Fake News":
                comment = name + " behauptet, er sei schon längst im Ziel.";
                break;
            case "Harry Trotter":
                comment = name + " zaubert einen ordentlichen Sprint aus dem Hut!";
                break;
            case "Jon Bon Pony":
                comment = name + " is halfway there, living on a prayer!";
                break;
            case "Sylvester Stallion":
                comment = name + " boxt sich rücksichtslos durch das Teilnehmerfeld!";
                break;
            case "Pony Stark":
                comment = name + " hat sich einen verdächtigen Reaktor auf die Brust geschnallt.";
                break;
            default:
                String[] general = {
                        name + " erleidet einen plötzlichen Motivationsverlust.",
                        name + " scheint vergessen zu haben, dass es sich um ein Rennen handelt.",
                        name + " wird vom Jockey mit der Peitsche an den Ernst der Lage erinnert.",
                        name + " stolpert über die eigenen Hufe. Peinlich.",
                        "Jemand im Publikum hat 'Leckerli' gerufen – " + name + " ist sichtlich abgelenkt.",
                        name + " überlegt kurz, ob ein Leben als Lasagne nicht doch stressfreier wäre.",
                        name + " trabt wie bei einem Sonntagsausflug.",
                        name + " zieht plötzlich gnadenlos an. Wurde da gedopt?",
                        "Der Jockey von " + name + " versucht verzweifelt das Lenkrad zu finden.",
                        name + " schnaubt bedrohlich. Die anderen Pferde machen freiwillig Platz.",
                        name + " bleibt stehen und starrt einen Schmetterling an. Unfassbar.",
                        "Was macht " + name + " da? Das sieht eher nach Breakdance als nach Galopp aus!"
                };
                comment = general[random.nextInt(general.length)];
                break;
        }

        log("🎤 [Kommentator]: \"" + comment + "\"");
        breakingNewsLabel.setText("🚨 " + comment.toUpperCase());
    }

    private void finishRace(int winningIndex) {
        Horse winner = raceLogik.getHorses().get(winningIndex);
        log("\n🏁 Das Rennen ist vorbei! Gewinner: " + winner.getName());

        breakingNewsLabel.setText("🏆 GEWINNER: " + winner.getName().toUpperCase() + " HAT DAS RENNEN GEWONNEN!");

        if (bettingSystem.getSelectedHorseIndex() != -1) {
            double odds = raceLogik.getCurrentOdds().get(winningIndex);
            double winAmount = bettingSystem.evaluateRace(winningIndex, odds);

            if (winAmount > 0) {
                log("$$$ GEWONNEN! Du erhältst " + String.format("%.2f", winAmount) + " $!");
                raceTrackPanel.setMoneyRain(true);
                postRaceTimer.start();
            } else {
                log("--- Verloren. Dein Geld ist nun im Casino-Tresor.");
            }
        } else {
            log("Keine Wette platziert.");
        }

        updateBalanceLabel();

        startButton.setText("Neues Rennen");
        startButton.setEnabled(true);
        startButton.setBackground(COLOR_PRIMARY);
        for (ActionListener al : startButton.getActionListeners()) {
            startButton.removeActionListener(al);
        }
        startButton.addActionListener(e -> prepareNewRace());
    }

    private void updateBalanceLabel() {
        balanceLabel.setText("Guthaben: " + String.format("%.2f", bettingSystem.getBankroll()) + " $");
    }

    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
